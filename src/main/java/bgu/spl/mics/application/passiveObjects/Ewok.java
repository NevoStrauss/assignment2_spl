package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).

 * This class is implemented using a Semaphore,
 * to restrict the obtaining of the ewoks by the Threads.
 * each thread must acquire a permit from the Semaphore,
 * guaranteeing that the ewok is available for use.
 */

public class Ewok {
    private final int serialNumber;
    private boolean available;
    private final Semaphore ewokLocker;

    /**
     * CTR
     * initializing this ewok's seril number, available for true (until acquired),
     * and the Semaphore to unfair and with 1 Thread permit.
     */
    public Ewok(int serialNumber){
        this.serialNumber=serialNumber;
        available=true;
        ewokLocker = new Semaphore(1, false);
    }

    /**
     * Acquires an Ewok
     */
    public void acquire() {
        try {
            ewokLocker.acquire();
        } catch (InterruptedException ignored){}
        available=false;
    }

    /**
     * release an Ewok
     */
    public void release() {
        available=true;
        ewokLocker.release();
    }

    /**
     *
     * @return if this ewok can be acquired.
     */
    public synchronized boolean isAvailable(){
        return available;
    }

    /**
     *
     * @return this ewok serial number.
     */
    public int getSerialNumber(){
        return serialNumber;
    }
}