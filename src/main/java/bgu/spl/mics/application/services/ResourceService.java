package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.ArrayList;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
    //Fields:
    private ResourcesHolder resourcesHolder;
    private ArrayList<Future<DeliveryVehicle>> FutureVehicles;

    public ResourceService(int num) {
        super("ResourcesService " + num);
        resourcesHolder = ResourcesHolder.getInstance();
        FutureVehicles = new ArrayList<>();
    }

    @Override
    protected void initialize() {

        subscribeEvent(AcquireVehicleEvent.class, acquireEv -> {
            Future<DeliveryVehicle> result = resourcesHolder.acquireVehicle();
            FutureVehicles.add(result);
            complete(acquireEv, result);
        });
        subscribeEvent(ReleaseVehicleEvent.class, releaseEv -> {
            resourcesHolder.releaseVehicle(releaseEv.getVehicle());
            complete(releaseEv, true);
        });

        subscribeBroadcast(TickBroadcast.class, term -> {
            if (term.isFinalTick()) {
                terminate();
                deleteTheFuture();
            }
        });
    }

    /**
     * Goes on every Future that the service responsible for and resolve it with null
     */
    private void deleteTheFuture() {
        for (Future<DeliveryVehicle> currFuture : FutureVehicles) {
            if (!currFuture.isDone())
                currFuture.resolve(null);
        }
    }

}
