package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

    private List<DeliveryVehicle> vehicles;
    private Hashtable<Integer, Boolean> acquireTable;
    private ConcurrentLinkedDeque<Future<DeliveryVehicle>> futureQueue;


    private static class SingeltonHolder {
        private static ResourcesHolder instance = new ResourcesHolder();
    }


    private ResourcesHolder() {
        vehicles = new Vector<>();
        acquireTable = new Hashtable<>();
        futureQueue = new ConcurrentLinkedDeque<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static ResourcesHolder getInstance() {
        return SingeltonHolder.instance;
    }

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     *
     * @return {@link Future<DeliveryVehicle>} object which will resolve to a
     * {@link DeliveryVehicle} when completed.
     */
    public Future<DeliveryVehicle> acquireVehicle() {
        Future<DeliveryVehicle> future = new Future<>();

        DeliveryVehicle v = hasVehicle();
        //There is an availab
        if (v != null) {
            future.resolve(v);
        } else {
            futureQueue.add(future);
        }
        return future;
    }

    /**
     * search for an available vehicle and returns it,
     *
     * @return an available vehicle or null
     */
    private DeliveryVehicle hasVehicle() {
        for (DeliveryVehicle v : vehicles) {
            synchronized (v) {

                if (acquireTable.get(v.getLicense())) {
                    acquireTable.put(v.getLicense(), false);
                    return v;

                }
            }
        }
        return null;
    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     *
     * @param vehicle {@link DeliveryVehicle} to be released.
     */
    public void releaseVehicle(DeliveryVehicle vehicle) {
        acquireTable.put(vehicle.getLicense(), true);
        Future<DeliveryVehicle> testThePoll;
        if ((testThePoll = futureQueue.poll()) != null)
            testThePoll.resolve(vehicle);
    }


    /**
     * Receives a collection of vehicles and stores them.
     * <p>
     *
     * @param vehicles Array of {@link DeliveryVehicle} instances to store.
     */
    public void load(DeliveryVehicle[] vehicles) {
        for (DeliveryVehicle dv : vehicles) {
            this.vehicles.add(dv);
            acquireTable.put(dv.getLicense(), true);
        }
    }

}