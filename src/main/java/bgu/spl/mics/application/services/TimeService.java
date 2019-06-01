package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    //fields
    private int duration;
    private int speed;
    private int currTick;
    private Timer timer;

    public TimeService(int duration, int speed) {
        super("Time Service");
        this.duration = duration;
        this.speed = speed;
        currTick = 1;
        timer = new Timer();

    }

    public TimeService(JsonObject timeService) {
        super("Time Service");
        duration = timeService.get("duration").getAsInt();
        speed = timeService.get("speed").getAsInt();
        currTick = 1;
        timer = new Timer();

    }

    @Override
    protected void initialize() {

        Thread timerThread = Thread.currentThread();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currTick <= duration) {
                    sendBroadcast(new TickBroadcast(currTick, currTick == duration));
                    currTick++;
                } else {
                    timer.cancel();
                    timer.purge();
                    terminate();
                    timerThread.interrupt();

                }
            }
        }, 0, speed);




    }


}
