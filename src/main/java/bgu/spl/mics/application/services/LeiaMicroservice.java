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
	private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
    }

    /**
     * Initialize the Leia micro service:
     * subscribes to:
     *          Events: none
     *          Broadcasts: Terminate Broadcast
     *
     * sends Events:
     *
     */
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
        {
            Diary d = Diary.getInstance();
            terminate();
            d.setLeiaTerminate(System.currentTimeMillis());
            System.out.println("Leia terminates");
        });
        try {
            Thread.sleep(100);
        }catch(InterruptedException ignored){}

        HashMap<Integer, Future<Boolean>> futureMap = new HashMap<>();
        System.out.println("Leia starts sending attacks");
        for (int i=0; i<attacks.length;i++) {
            Future<Boolean> f = sendEvent(new AttackEvent(attacks[i]));
            futureMap.put(i, f);
        }
        System.out.println("Leia finishes sending attacks");
        sendBroadcast(new NoMoreAttacksBroadcast());
        for (int j = 0; j < attacks.length; j++) {
            futureMap.get(j).get();
            futureMap.remove(j);
        }
        System.out.println("Leia finished checking Future Objects");
        Future<Boolean> f = sendEvent(new DeactivationEvent());
        f.get();                    //waiting for R2D2 finish deactivatin shield
        sendEvent(new BombDestroyerEvent());
    }

    public int getTotalAttack(){
        return attacks.length;
    }
}
