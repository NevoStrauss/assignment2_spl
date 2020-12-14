package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {
    private Ewok ewok;

    @BeforeEach
    public void setup(){ewok = new Ewok(1);}

    @Test
    public void acquireTest(){
            assertTrue(ewok.isAvailable());
            ewok.acquire();
            assertFalse(ewok.isAvailable());
    }

    @Test
    public void releaseTest(){
            ewok.acquire();
            assertFalse(ewok.isAvailable());
            ewok.release();
            assertTrue(ewok.isAvailable());
    }


    /**
     * removed because we changed the logic of the Ewok class
     */
//    @Test
//    public void acquireWhenAlreadyAcquiredTest(){
//        ewok.acquire();
//        assertThrows(InterruptedException.class,()->ewok.acquire());
//    }

//    @Test
//    public void releaseWhenNotAcquiredTest(){
//        assertThrows(InterruptedException.class,()->ewok.release());
//    }
}
