package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

    public LogisticsService(int num) {
        super("Logistics Service " + num);
    }

    @Override
    protected void initialize() {

        subscribeEvent(DeliveryEvent.class, DeliveryEv -> {

            Future<Future<DeliveryVehicle>> futureV = sendEvent(new AcquireVehicleEvent());
            DeliveryVehicle resultVehicle = null;

            if (futureV != null) {
                Future<DeliveryVehicle> tmpFuture = futureV.get();
                if (tmpFuture != null)
                    resultVehicle = tmpFuture.get();
            }

            if (resultVehicle != null) {

                resultVehicle.deliver(DeliveryEv.getCustomer().getAddress(), DeliveryEv.getCustomer().getDistance());
                sendEvent(new ReleaseVehicleEvent(resultVehicle));
            }

        });

        subscribeBroadcast(TickBroadcast.class, term -> {
            if (term.isFinalTick())
                terminate();
        });

    }

}
