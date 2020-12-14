package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    /**
     * @param duration: how much time it takes to deactivate the shield
     */
    long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    /**
     * Initialize the R2D2 micro service:
     * subscribes to:
     *          Events: Deactivation Event
     *          Broadcasts: Terminate Broadcast
     */
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class,(DeactivationEvent deactivationEvent)->{
            try{
                Thread.sleep(duration);     //DEACTIVATE !!!!
            }catch (InterruptedException e){}
            complete(deactivationEvent,true);       //completes the event that triggered this callback
            Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());      //update the diary after he finished
        });
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
        {
            terminate();        //sends to @MicroService terminate()
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis()); //update diary
        });
    }
}
