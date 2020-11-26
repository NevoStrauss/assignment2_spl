package bgu.spl.mics.application.services;

import java.util.*;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;

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

    @Override
    protected void initialize() {
        HashMap<Integer,Future> futureMap = new HashMap<>();
        int i = 0;
        for (Attack attack:attacks) {
            Future f = sendEvent(new AttackEvent(attack));
            futureMap.put(i,f);
            i++;
        }
        for (int j = 0; j < i; j++){
            while (!futureMap.get(j).isDone()){
                continue;
            }
            futureMap.remove(j);
        }
    }

    public int getTotalAttack(){
        return attacks.length;
    }
}
