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
	private int counter;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		counter = 0;
    }

    @Override
    protected void initialize() {
        Callback<FinishedSubscribedBroadcast> callback1 = (FinishedSubscribedBroadcast fs) ->
        {
            counter++;
            if (counter == 2) {
                HashMap<Integer, Future<Boolean>> futureMap = new HashMap<>();
                System.out.println("Leia starts sending attacks");
                for (int i=0; i<attacks.length;i++) {
                    Future<Boolean> f = sendEvent(new AttackEvent(attacks[i]));
                    futureMap.put(i, f);
                }
                System.out.println("Leia finishes sending attacks");
                sendBroadcast(new NoMoreAttacksBroadcast());
                for (int j = 0; j < attacks.length; j++) {
                    Boolean result = futureMap.get(j).get();
                    futureMap.remove(j);
                }
                System.out.println("Leia finished checking Future Objects");
                Future<Boolean> f = sendEvent(new DeactivationEvent());
            }
        };
            subscribeBroadcast(FinishedSubscribedBroadcast.class, callback1);
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
        {
            Diary d = Diary.getInstance();
            terminate();
            d.setLeiaTerminate(System.currentTimeMillis());
            System.out.println("Leia terminates");
        });
    }

    public int getTotalAttack(){
        return attacks.length;
    }
}
