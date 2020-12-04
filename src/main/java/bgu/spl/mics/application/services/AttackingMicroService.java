package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;
import java.util.Comparator;
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
            System.out.println(ewokSerialNumbers);
            Collections.sort(ewokSerialNumbers);
            System.out.println(ewokSerialNumbers);
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.acquire(ewokSerialNumbers);
            System.out.println(getName()+" starts attacking with "+ewokSerialNumbers.toString());
            try {
                Thread.sleep(attackEvent.getAttack().getDuration());
            } catch (InterruptedException ignored) {}
            complete(attackEvent,true);
            System.out.println(getName()+" finished attack with Ewoks: "+ewokSerialNumbers.toString());
            ewoks.release(ewokSerialNumbers);
            Diary.getInstance().addAttack();
        });
        System.out.println(getName()+" finished subscribing to attackEvents");
    }
}
