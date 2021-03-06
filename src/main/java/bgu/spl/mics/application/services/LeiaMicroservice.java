package bgu.spl.mics.application.services;

import java.util.*;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private final Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
    }

    /**
     * Initialize the Leia MicroService:
     * subscribes to:
     *          Events: none
     *          Broadcasts: Terminate Broadcast
     *
     * sends Events:
     *          Events: all of the atttackEvents from @attacks, Deactivation attack and BombDestroyerEvent
     *          Broadcasts: NoMoreAttackBroadcast to inform all the others that no more attacks are left to sent
     */
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
        {
            terminate();
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis()); //update diary
        });
        try {
            Thread.sleep(100);  //sleep to let everyone finish subscribe
        }catch(InterruptedException ignored){}

        HashMap<Integer, Future<Boolean>> futureMap = new HashMap<>();      //map to reserve the future objects of the attacks

        //send all the attacks
        for (int i=0; i<attacks.length;i++) {
            Future<Boolean> f = sendEvent(new AttackEvent(attacks[i]));
            futureMap.put(i, f);
        }

        //inform attackers that there aren't any attacks left
        sendBroadcast(new NoMoreAttacksBroadcast());

        //check all the attacks finished
        for (int j = 0; j < attacks.length; j++) {
            futureMap.get(j).get();     //Future.get() is blocked until the future object is done
            futureMap.remove(j);        //after a future object finishes, remove it
        }

        //send the deactivator (R2D2) event so he can start deactivating
        Future<Boolean> f = sendEvent(new DeactivationEvent());
        f.get();                    //waiting for the deactivator finish deactivating shield

        //send the last event of the application to the Bomb Destroyer
        sendEvent(new BombDestroyerEvent());
    }

    public int getTotalAttack(){
        return attacks.length;
    }
}
