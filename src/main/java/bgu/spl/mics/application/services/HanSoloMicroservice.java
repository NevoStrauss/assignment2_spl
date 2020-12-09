package bgu.spl.mics.application.services;


import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.NoMoreAttacksBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends AttackingMicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        super.initialize();
        subscribeBroadcast(NoMoreAttacksBroadcast.class,(NoMoreAttacksBroadcast noMoreAttacksBroadcast)->
        {Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            System.out.println(getName()+" finished attacking at " + System.currentTimeMillis());});
        subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast tb)->
            {
                terminate();
                Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            });
        System.out.println("Han Solo finished subscribing");

    }
}
