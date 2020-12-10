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

    /**
     * Initialize the Lando micro service, he is the bomb destroyer:
     * subscribes to:
     *          Events: BombDestroyer Event
     *          Broadcasts: Terminate Broadcast
     */
    protected void initialize() {
       subscribeEvent(BombDestroyerEvent.class,(BombDestroyerEvent bombDestroyerEvent)->
       {
           try {
               Thread.sleep(duration);      //sleep to bomb
           }catch (InterruptedException e){}
           complete(bombDestroyerEvent,true);   //complete the event
           sendBroadcast(new TerminateBroadcast());     //inform everyone that they should terminate
       });
       subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
       {
           terminate();
           Diary.getInstance().setLandoTerminate(System.currentTimeMillis());       //update the diary
       });
    }
}
