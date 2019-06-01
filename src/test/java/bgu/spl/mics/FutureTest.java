package bgu.spl.mics;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<Integer> F = new Future<>();
    @Test
    public void get() {

    }

    @Test
    public void resolve() {
        F.resolve(7);
        assertEquals(new Integer(7),F.get());
    }

    @Test
    public void isDone() {
        assertFalse(F.isDone());
        F.resolve(7);
        assertTrue(F.isDone());
    }



    @Test
    public void get1() {
        long startTime = System.currentTimeMillis();
        assertNull (F.get(1000, TimeUnit.MILLISECONDS));
        long estimatedTime = System.currentTimeMillis() - startTime;
        assertTrue(estimatedTime>1000);

    }
}