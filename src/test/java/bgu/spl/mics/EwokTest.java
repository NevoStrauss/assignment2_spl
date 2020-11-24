package bgu.spl.mics;
import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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

    @Test
    public void acquireWhenAlreadyAcquiredTest(){
        ewok.acquire();
        assertThrows(RuntimeException.class, ()->ewok.acquire());
    }

    @Test
    public void releaseWhenAlreadyEwokNotAcquiredTest(){
        assertThrows(RuntimeException.class, ()->ewok.release());
    }
}
