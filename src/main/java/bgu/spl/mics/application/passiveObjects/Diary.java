package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */

/**
 * The Diary Passive object is implemented as a singleton.
 * all of the services are accessing to its instance in runtime to set their parameters to record.
 * At the end of the process, the Diary fields converted to json file,
 * and displays the chronological summary of the 'battle set'.
 */
public class Diary {
    private static class single_instance{
        private static final Diary single_instance = new Diary();
    }

    private  AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    /**
     * CTR
     * initializing the attacks counter with the Atomic class.
     */
    private Diary(){
        totalAttacks = new AtomicInteger(0);
    }

    /**
     * @return the only instance of the Diary.
     */
    public static Diary getInstance(){
        return single_instance.single_instance;
    }

    /**
     * @return the time when C3PO finished his attacks.
     */
    public long getC3POFinish() {
        return C3POFinish;
    }

    /**
     * @return the time when C3PO terminate itself.
     */
    public long getC3POTerminate() {
        return C3POTerminate;
    }

    /**
     * @return the total Attacks that were so far.
     */
    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    /**
     * @return the time when HanSolo finished his attacks.
     */
    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    /**
     * @return the time when HanSolo terminate itself.
     */
    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    /**
     * @return the time when Lando terminate itself.
     */
    public long getLandoTerminate() {
        return LandoTerminate;
    }

    /**
     * @return the time when Leia terminate itself.
     */
    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    /**
     * @return the time when R2D2 deactivating the shield generator.
     */
    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    /**
     * @return the time when R2D2 terminate itself.
     */
    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    /**
     * @param c3POFinish accessed only by C3PO when it gets the 'NoMoreAttacks' broadcast,
     * and there are no more events in its queue.
     */
    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    /**
     * @param c3POTerminate accessed only by C3PO to sets its termination time.
     */
    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    /**
     * @param hanSoloFinish accessed only by HanSolo when it gets the 'NoMoreAttacks' broadcast,
     * and there are no more events in its queue.
     */

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    /**
     * @param hanSoloTerminate accessed only by HanSolo to set its termination time.
     */
    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    /**
     * @param landoTerminate accessed only by Lando to set its termination time.
     */
    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    /**
     * @param leiaTerminate accessed only by Leia to set its termination time.
     */
    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    /**
     * @param r2D2Deactivate accessed only by R2S2 when it gets the 'DeactivationEvent'.
     */
    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    /**
     * @param r2D2Terminate accessed only by R2D2 to set its termination time.
     */
    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    /**
     * add one attack to the attack counter. accessed only by the attacking MicroServices,
     * (in our case- HanSolo and C3PO)
     */
    public void addAttack(){
        totalAttacks.addAndGet(1);
    }

    /**
     * This methods is implemented only for executing multiple tests in a row.
     */
    public void resetTotalAttacks(){
        totalAttacks=new AtomicInteger(0);
    }
}