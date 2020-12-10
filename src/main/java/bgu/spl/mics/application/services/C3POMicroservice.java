package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.NoMoreAttacksBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends AttackingMicroService {

    public C3POMicroservice() {
        super("C3PO");
    }

    /**
     * Initialize the R2D2 micro service:
     * subscribes to:
     *          Events: Attack Event
     *          Broadcasts: NoMoreAttacks, Terminate Broadcast
     */
    protected void initialize() {
        subscribeToAttackEvent();
        subscribeBroadcast(NoMoreAttacksBroadcast.class,(NoMoreAttacksBroadcast noMoreAttacksBroadcast)->
                Diary.getInstance().setC3POFinish(finishAttack));   //update the time of the last attack
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
        {
            terminate();
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());   //update diary when terminating
        });
    }
}
