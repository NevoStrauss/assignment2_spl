package bgu.spl.mics;

import bgu.spl.mics.application.messages.ESPNBroadcast;
import bgu.spl.mics.application.messages.StringEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBus messageBus;

    @BeforeAll
    public void setUp(){messageBus = MessageBusImpl.getInstance();}

    @Test
    void getInstance() {
        MessageBus messageBus1 = MessageBusImpl.getInstance();
        assertEquals(messageBus1,messageBus);
    }

    /**
     * this method is being tested in complete() and sendEvent()
     */
//    @Test
//    void subscribeEvent() {
//    }

    /**
     * this method is being tested in complete() and sendBroadcast()
     */

//    @Test
//    void subscribeBroadcast() {
//    }

    /**
     * if this works, it checks sendEvent as well, as microservice sendEvent uses
     * messegeBus sendEvent
     */

    @Test
    void complete() {
        MS ms = new MS("test");
        messageBus.register(ms);
        StringEvent e = new StringEvent("str");
        messageBus.subscribeEvent(e.getClass(),ms);
        Future<String> f = ms.sendEvent(e);
        messageBus.complete(e,"completed");
        assert f != null;
        assertTrue(f.isDone());
        assertEquals("completed",f.get());
        messageBus.unregister(ms);
    }

    @Test
    void sendBroadcast() throws InterruptedException {
        MS ms = new MS("test");
        MS ms1 = new MS("test1");
        MS ms2 = new MS("test2");
        ESPNBroadcast ronaldo = new ESPNBroadcast("ronaldo");
        messageBus.register(ms);
        messageBus.register(ms1);
        messageBus.register(ms2);
        messageBus.subscribeBroadcast(ronaldo.getClass(),ms);
        messageBus.subscribeBroadcast(ronaldo.getClass(),ms1);
        ms2.sendBroadcast(ronaldo);
        Message toCheck = messageBus.awaitMessage(ms);
        Message toCheck1 = messageBus.awaitMessage(ms1);
        assertEquals(toCheck,ronaldo);
        assertEquals(toCheck1,ronaldo);
        assertEquals(toCheck,toCheck1);
        messageBus.unregister(ms);
        messageBus.unregister(ms1);
        messageBus.unregister(ms2);
    }

    @Test
    void sendEvent() throws InterruptedException {
        MS ms = new MS("test");
        MS ms1 = new MS("test1");
        MS ms2 = new MS("test2");
        StringEvent ronaldo = new StringEvent("ronaldo");
        messageBus.register(ms);
        messageBus.register(ms1);
        messageBus.register(ms2);
        messageBus.subscribeEvent(ronaldo.getClass(),ms);
        messageBus.subscribeEvent(ronaldo.getClass(),ms1);
        ms2.sendEvent(ronaldo);
        Message toCheck = messageBus.awaitMessage(ms);
        Message toCheck1 = messageBus.awaitMessage(ms1);
        assertEquals(toCheck,ronaldo);
        assertEquals(toCheck1,ronaldo);
        assertEquals(toCheck,toCheck1);
        messageBus.unregister(ms);
        messageBus.unregister(ms1);
        messageBus.unregister(ms2);
    }

    /**
     * this method is the very basic of the class. if any other test passes, this works
     */

//    @Test
//    void register() {
//    }

    /**
     * this method is the very basic of the class. if any other test passes, this works
     */

//    @Test
//    void unregister() {
//    }

    @Test
    void awaitMessage() throws InterruptedException {       //with assumption that the queue NOT empty
        MS ms = new MS("test");
        StringEvent ronaldo = new StringEvent("ronaldo");
        messageBus.register(ms);
        messageBus.subscribeEvent(ronaldo.getClass(),ms);
        ms.sendEvent(ronaldo);
        Message toCheck = messageBus.awaitMessage(ms);
        assertEquals(toCheck,ronaldo);
        MS ms1 = new MS("test1");
        assertThrows(InterruptedException.class,()->messageBus.awaitMessage(ms1));
    }
}