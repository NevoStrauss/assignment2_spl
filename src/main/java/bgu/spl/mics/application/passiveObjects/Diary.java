package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private  AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;
    private static Diary single_instance=null;

    private Diary(){
        totalAttacks = new AtomicInteger(0);
    }

    public static Diary getInstance(){
        if (single_instance==null)
            single_instance = new Diary();
        return single_instance;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void addAttack(){
        totalAttacks.addAndGet(1);
    }

    public void resetTotalAttacks(){
        totalAttacks=new AtomicInteger(0);
    }
}
