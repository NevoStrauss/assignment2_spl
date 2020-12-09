package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    private final int serialNumber;
	private boolean available;
	private final Semaphore ewokLocker;

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

    public synchronized boolean isAvailable(){
        return available;
    }

    public int getSerialNumber(){
        return serialNumber;
    }
}
