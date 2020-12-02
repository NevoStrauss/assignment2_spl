package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    private final int serialNumber;
	private boolean available;

	public Ewok(int serialNumber){
	    this.serialNumber=serialNumber;
	    available=true;
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
		while (!available)
		    try{
		        wait();
            }catch (InterruptedException e){}
		available=false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
    	if(!available){
    	    available=true;
    	    notifyAll();
        }
    }

    public synchronized boolean isAvailable(){
        return available;
    }

    public int getSerialNumber(){
        return serialNumber;
    }
}
