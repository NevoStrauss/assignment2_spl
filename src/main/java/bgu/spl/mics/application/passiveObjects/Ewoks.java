package bgu.spl.mics.application.passiveObjects;

import java.util.Collections;
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


 * The Ewoks Passive object is implemented as a singleton.
 * The Attacking Microservices are accessing to its instance in runtime,
 * to acquire the ewoks for their attacks.
 */

public class Ewoks {

    private static class single_instance{
        private static final Ewoks single_instance = new Ewoks();
    }

    private Ewok[] ewokArray;
    private Ewoks(){
        this.ewokArray=new Ewok[0];
    }

    /**
     * @return the only instance of the Ewoks.
     */
    public static Ewoks getInstance(){
        return single_instance.single_instance;
    }

    /**
     * @param ewokArray set the resources from the input (ewoks) for the attacks in a list.
     */
    public void setEwokArray(Ewok[] ewokArray){
        this.ewokArray=ewokArray;
    }

    /**
     * @param ewoksSerialNumbers is the list of ewoks serial numbers, demand for an attack,
     * which sent by the Attacking MicroServices before executing an attack.
     * The method sorts the serial numbers, and loops over the ewok list and tries to acquire the requested ewoks
     * in an ascending manner.
     * The method is calling the ewok acquire method, which is synchronized using the
     * Semaphore locker.
     *
     */
    public List<Integer> acquire(List<Integer> ewoksSerialNumbers){
        Collections.sort(ewoksSerialNumbers);
        for (Integer i : ewoksSerialNumbers) {
            ewokArray[i].acquire();
        }
        return ewoksSerialNumbers;
    }

    /**
     *
     * @param ewoksSerialNumbers is the list of ewoks to release after an attack,
     * which sent by the Attacking MicroServices after executing an attack.
     * The method loops over the ewok list and tries to release the requested ewoks.
     * The method is calling the ewok release method, which is synchronized using the
     * Semaphore locker.
     */
    public void release(List<Integer> ewoksSerialNumbers){
        for (Integer i : ewoksSerialNumbers){
            ewokArray[i].release();
        }
    }
}