package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

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

    @Test
    public void testGetWithTimeOut() throws InterruptedException {
        assertFalse(future.isDone());
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve("foo");
    }
}
