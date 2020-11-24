package bgu.spl.mics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;

public class MessageBusTest {
    private MessageBus messageBus;
    private MicroService microService;

    @BeforeAll
    public void setUp(){messageBus = MessageBusImpl.getInstance();}

    @Test
    public void subscribeEventTest(){

    }

}
