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
        try {
            assertTrue(ewok.isAvailable());
            ewok.acquire();
            assertFalse(ewok.isAvailable());
        } catch (InterruptedException e){
            fail();
        }
    }

    @Test
    public void releaseTest(){
        try {
            ewok.acquire();
            assertFalse(ewok.isAvailable());
            ewok.release();
            assertTrue(ewok.isAvailable());
        }catch (InterruptedException e){
            fail();
        }
    }

    @Test
    public void acquireWhenAlreadyAcquiredTest(){
        try {
            ewok.acquire();
        }catch (InterruptedException e){
            fail();
        }
        assertThrows(InterruptedException.class,()->ewok.acquire());
    }

    @Test
    public void releaseWhenNotAcquiredTest(){
        assertThrows(InterruptedException.class,()->ewok.release());
    }
}
