package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusTest {

    private MessageBus testedBus;
    private MicroService microserviceForTest;
    private Broadcast broadcastForTest;
    private Event<String> eventForTest;

    @Before
    public void setUp() throws Exception {
        testedBus = MessageBusImpl.getInstance();
        microserviceForTest = new ExampleBroadcastListenerService("Test",new String[2]);
        broadcastForTest = new ExampleBroadcast("The bus");
        eventForTest = new ExampleEvent("the bus");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSendBrodcast() throws Exception{
        testedBus.register(microserviceForTest);
        testedBus.subscribeBroadcast(broadcastForTest.getClass(),microserviceForTest);
        testedBus.sendBroadcast(broadcastForTest);
        Message m = testedBus.awaitMessage(microserviceForTest);
        assertEquals(broadcastForTest,m);
    }

    @Test
    public void testSendEvent() throws Exception{
        testedBus.register(microserviceForTest);
        testedBus.subscribeEvent((Class<? extends Event<String>>)eventForTest.getClass(),microserviceForTest);
        testedBus.sendEvent(eventForTest);
        Message m = testedBus.awaitMessage(microserviceForTest);
        assertEquals(eventForTest,m);
    }

    @Test
    public void complete() {
        testedBus.register(microserviceForTest);
        testedBus.subscribeEvent((Class<? extends Event<String>>)eventForTest.getClass(),microserviceForTest);
        Future<String> testFuture = testedBus.sendEvent(eventForTest);
        testedBus.complete(eventForTest,"Good");
        assertEquals("Good",testFuture.get());

    }


    @Test
    public void sendEventCaseNull() {
        Future<String> testFuture = testedBus.sendEvent(eventForTest);
        assertNull(testFuture);
    }


    @Test
    public void unregister() {
        testedBus.register(microserviceForTest);
        testedBus.subscribeEvent((Class<? extends Event<String>>)eventForTest.getClass(),microserviceForTest);
        testedBus.unregister(microserviceForTest);
        Future<String> testFuture = testedBus.sendEvent(eventForTest);
        assertNull(testFuture);
    }

    @Test
    public void awaitMessage() {
    }



}