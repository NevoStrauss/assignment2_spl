package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishedSubscribedBroadcast;
import bgu.spl.mics.application.messages.NoMoreAttacksBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

public class AttackingMicroService extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackingMicroService(String name) {
        super(name);
    }

    /**
     * this method is called once when the event loop starts.
     */
    @Override
    protected void initialize(){
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent)->
        {
            List<Integer> ewokSerialNumbers = attackEvent.getAttack().getSerials();
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.acquire(ewokSerialNumbers);
            try {
                Thread.sleep(attackEvent.getAttack().getDuration());
            } catch (InterruptedException ignored) {}
            complete(attackEvent,true);
            ewoks.release(ewokSerialNumbers);
            Diary.getInstance().addAttack();
        });
        sendBroadcast(new FinishedSubscribedBroadcast());
    }
}
