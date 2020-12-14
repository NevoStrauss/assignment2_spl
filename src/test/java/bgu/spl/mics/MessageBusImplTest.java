package bgu.spl.mics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private static MessageBus messageBus;

    @BeforeAll
    public static void setUp(){messageBus = MessageBusImpl.getInstance();}

    @Test
    void getInstance() {
        MessageBus messageBus1 = MessageBusImpl.getInstance();
        assertSame(messageBus1, messageBus);
    }

    /**
     * Test flow:
     * 1. create & register a new micro service (ms).
     * 2. create an event & subscribe ms to the event type.
     * 3. send the event & save the returned Future object.
     * 4. complete the event with the result: "completed".
     * 5. check if the Future object is Done with the expected result.
     * 6. unregister ms from messageBus.
     *
     * If this test passes, it verifies that the methods: sendEvent, register, subscribe event and complete are working.
     */

    @Test
    void EventFlowTest() {
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

    /**
     * Test flow:
     * 1. create & register 3 new micro services (ms,ms1,ms2).
     * 2. create new (type) Broadcast.
     * 3. subscribe 2 microservices to (type) Broadcast
     * 4. let ms2 send the Broadcast to the messageBus.
     * 5. ms, ms1- getting the message from the messageBus (awaitMessage)
     * 6. check that the messages that been received are equals.
     * 7. unregister microservices from messageBus.
     *
     * If the test passes, the methods: register, subscribeBroadcast,sendBroadcast and send awaitMessage are working.
     * @throws InterruptedException: if ms,ms1 are not registered when awaiting for Message.
     */

    @Test
    void BroadcastTest() throws InterruptedException {
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
        messageBus.unregister(ms);
        messageBus.unregister(ms1);
        messageBus.unregister(ms2);
    }

    /**
     * Test flow:
     * 1. create register and subscribe 3 microservices (ms,ms1,ms2) to(type) StringMessage.
     * 2. first for loop: create numOfMicroServices MS, store them in array, register and subscribe them to StringEvent.
     * 3. second for loop: create numOfEventsToSend StringEvents,store them in array and send the StringEvents to the messageBus.
     * 4. third for loop: await for the distribution of the messages by the messageBus, and store the events in array.
     * 5. fourth for loop: check if each of the messages is assigned to a MS in a round Robbin Manner.
     * 6. fifth for loop: unregister all of the MS's from the messageBus.
     *
     * @throws InterruptedException:if one of the MS's are not registered when awaiting for Message
     */

    @ParameterizedTest
    @CsvSource({"0,0","1,1","2,2","3,2","100,4","100000,17053"})
    void sendEventRoundRobbinManner(int numOfEventsToSend, int numOfMicroServices ) throws InterruptedException {
        MS[] msArray = new MS[numOfMicroServices];
        for (int i = 0; i < msArray.length; i++) {
            msArray[i] = new MS("test"+i);
            messageBus.register(msArray[i]);
            messageBus.subscribeEvent(StringEvent.class, msArray[i]);
        }

        StringEvent[] StringEventArray = new StringEvent[numOfEventsToSend];
        for (int i = 0; i < StringEventArray.length; i++) {
            StringEventArray[i] = new StringEvent(""+i);
            msArray[0].sendEvent(StringEventArray[i]);
        }

        StringEvent[] toCheck = new StringEvent[numOfEventsToSend];
        for (int i = 0; i < toCheck.length; i++) {
            toCheck[i] = (StringEvent) messageBus.awaitMessage(msArray[i % numOfMicroServices]);
        }

        for (int i = 0; i < numOfEventsToSend; i++) {
            assertEquals(toCheck[i], StringEventArray[i]);
        }

        for (int i = 0; i < msArray.length; i++) {
            messageBus.unregister(msArray[i]);
        }
    }

    /**
     * check if throws exception when awaitMessage to unregistered microservice.
     * Test flow:
     * 1. create new microservice.
     * 2. try to get the message without registering to messageBus.
     */

    @Test
    void awaitMessageWhenMsNotRegistered(){
        MS ms = new MS("test");
        assertThrows(RuntimeException.class,()->messageBus.awaitMessage(ms));
    }
}