package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
       subscribeEvent(BombDestroyerEvent.class,(BombDestroyerEvent bombDestroyerEvent)->
       {
           try {
               Thread.sleep(duration);
           }catch (InterruptedException e){}
           complete(bombDestroyerEvent,true);
           sendBroadcast(new TerminateBroadcast());
           System.out.println("Lando finished bombing");
       });
       subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
       {
           terminate();
           Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
       });
        System.out.println("Lando finished subscribing");
    }
}
