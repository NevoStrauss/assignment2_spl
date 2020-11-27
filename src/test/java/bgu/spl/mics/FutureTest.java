package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(future.get(), str);
    }

    @Test
    public void doubleResolveTest(){
        future.resolve("");
        assertThrows(RuntimeException.class,()->future.resolve(""));
    }

    @Test
    public void testIsDone(){
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
    }

    @Test
    public void testGet(){
        assertFalse(future.isDone());
        String s = "some result";
        future.resolve(s);
        String result = future.get();
        assertTrue(future.isDone());
        assertEquals(result,s);
    }

    /**
     * Test flow:
     * 1. check that future is not resolved.
     * 2. try to get the result in time limits.
     * 3. check that future is still unresolved.
     * 4. resolve future, try to get the result, and check if its resolved.
     */

    @Test
    public void testGetWithTimeOut(){
        assertFalse(future.isDone());
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve("some result");
        assertEquals(future.get(100, TimeUnit.MICROSECONDS),"some result");
    }
}
