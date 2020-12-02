package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */

public class Ewoks {

    private static Ewoks single_instance=null;
    private Ewok[] ewokArray;
    private boolean[] ewokAvailableArray;

    private Ewoks(){
        this.ewokArray=new Ewok[0];
        this.ewokAvailableArray=new boolean[0];
    }

    public static Ewoks getInstance(){
        if (single_instance == null)
            single_instance = new Ewoks();
        return single_instance;
    }

    public void setEwokArray(Ewok[] ewokArray){
        this.ewokArray=ewokArray;
        this.ewokAvailableArray=new boolean[ewokArray.length];
    }

    public synchronized void acquire(List<Integer> ewoksSerialNumbers){
        for (Integer i : ewoksSerialNumbers){
            ewokArray[i].acquire();
        }
    }

    public void release(List<Integer> ewoksSerialNumbers){
        for (Integer i : ewoksSerialNumbers){
            ewokArray[i].release();
        }
    }

}
