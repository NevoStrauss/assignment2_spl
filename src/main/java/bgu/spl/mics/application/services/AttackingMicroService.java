package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishedSubscribedBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

public class AttackingMicroService extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackingMicroService(String name) {
        super(name);
    }

    private static void call(AttackEvent attackEvent) {
        try {
            Thread.sleep(attackEvent.getAttack().getDuration());
        } catch (InterruptedException ignored) {
        }
    Diary d = Diary.getInstance();
    d.addAttack();
    }

    /**
     * this method is called once when the event loop starts.
     */
    @Override
    protected void initialize(){
        subscribeEvent(AttackEvent.class, AttackingMicroService::call);
        sendBroadcast(new FinishedSubscribedBroadcast());
    }
}
