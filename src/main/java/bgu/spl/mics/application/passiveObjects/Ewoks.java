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
    private List<Ewok> ewokList;

    private Ewoks(){
        this.ewokList=new LinkedList<>();
    }
    public static Ewoks getSingle_instance(){
        if (single_instance == null)
            single_instance = new Ewoks();
        return single_instance;
    }

    public void add(Ewok ewok){
        single_instance.add(ewok);
    }

}
