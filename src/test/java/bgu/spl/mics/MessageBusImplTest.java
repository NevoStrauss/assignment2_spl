package bgu.spl.mics;

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

//    @Test
//    void subscribeEvent() {
//    }

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
        MS ms1 = new MS("test1");
        StringEvent e = new StringEvent("str");
        messageBus.subscribeEvent(e.getClass(),ms);
        Future<String> f = ms1.sendEvent(e);
        messageBus.complete(e,"completed");
        assert f != null;
        assertTrue(f.isDone());
        assertEquals("completed",f.get());
    }

    @Test
    void sendBroadcast() {

    }

    @Test
    void sendEvent() {

    }

//    @Test
//    void register() {
//    }

//    @Test
//    void unregister() {
//    }

    @Test
    void awaitMessage() {       //with assumption that the queue NOT empty

    }
}