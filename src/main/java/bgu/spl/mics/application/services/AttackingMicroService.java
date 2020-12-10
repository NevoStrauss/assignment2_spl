package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;
import java.util.List;

/**
 * an abstract class for all the attacking micro services. it has a protected method @subscribeToAttackEvent that
 * all attacking microservices need to have
 */
public abstract class AttackingMicroService extends MicroService {
    protected long finishAttack;
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackingMicroService(String name) {
        super(name);
        finishAttack=0;
    }

    protected abstract void initialize();

    /**
     * subscribes to an AttackEvent type
     */
    protected void subscribeToAttackEvent(){
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent)->
        {
            List<Integer> ewokSerialNumbers = attackEvent.getAttack().getSerials();     //get the list of the ewoks required
            Collections.sort(ewokSerialNumbers);        //sort the ewoks that are required in an ascenting matter to avoid deadlock
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.acquire(ewokSerialNumbers);           //acquire the ewoks that are required for the attack
            try {
                Thread.sleep(attackEvent.getAttack().getDuration());    //ATTACK!!!
            } catch (InterruptedException ignored) {}
            complete(attackEvent,true);     //complete the event
            finishAttack = System.currentTimeMillis();  //save the current time of finish attack
            ewoks.release(ewokSerialNumbers);       //release the ewoks
            Diary.getInstance().addAttack();        //update +1 in the diary attack number
        });
    }
}
