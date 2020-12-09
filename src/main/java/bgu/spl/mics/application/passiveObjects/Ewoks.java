package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */

public class Ewoks {

    private static class single_instance{
        private static final Ewoks single_instance = new Ewoks();
    }
    private Ewok[] ewokArray;
    private Ewoks(){
        this.ewokArray=new Ewok[0];
    }

    public static Ewoks getInstance(){
        return single_instance.single_instance;
    }

    public void setEwokArray(Ewok[] ewokArray){
        this.ewokArray=ewokArray;
    }

    public void acquire(List<Integer> ewoksSerialNumbers){
        for (Integer i : ewoksSerialNumbers) {
            ewokArray[i].acquire();
        }
    }

    public void release(List<Integer> ewoksSerialNumbers){
        for (Integer i : ewoksSerialNumbers){
            ewokArray[i].release();
        }
    }
}
