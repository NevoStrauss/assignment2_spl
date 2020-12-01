package bgu.spl.mics.application.services;

import java.util.*;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.FinishedSubscribedBroadcast;
import bgu.spl.mics.application.messages.StartSendAttacks;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;
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
	private int numOfAttackers;
	private int counter;
	
    public LeiaMicroservice(Attack[] attacks,int numOfAttackers) {
        super("Leia");
		this.attacks = attacks;
		this.numOfAttackers = numOfAttackers;
		counter = 0;
    }

    @Override
    protected void initialize() {
        Callback<FinishedSubscribedBroadcast> callback1 = (FinishedSubscribedBroadcast fs) ->
        {
            counter++;
            if (counter == numOfAttackers) {
                HashMap<Integer, Future<Boolean>> futureMap = new HashMap<>();
                int i = 0;
                for (Attack attack : attacks) {
                    Future<Boolean> f = sendEvent(new AttackEvent(attack));
                    futureMap.put(i, f);
                    i++;
                }
                for (int j = 0; j < i; j++) {
                    Boolean result = futureMap.get(j).get();
                    futureMap.remove(j);
                }
                future
            }
        };
            subscribeBroadcast(FinishedSubscribedBroadcast.class, callback1);
    }

    public int getTotalAttack(){
        return attacks.length;
    }
}
