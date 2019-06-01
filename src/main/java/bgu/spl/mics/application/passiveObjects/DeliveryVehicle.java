package bgu.spl.mics.application.passiveObjects;

import com.google.gson.JsonObject;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class DeliveryVehicle {
	//fields
	private int licence;
	private int speed;
	/**
     * Constructor.   
     */
	 public DeliveryVehicle(JsonObject vehicleAsJsonObject) {
		this.licence = vehicleAsJsonObject.get("license").getAsInt();
		this.speed = vehicleAsJsonObject.get("speed").getAsInt();
	  }
	/**
     * Retrieves the license of this delivery vehicle.   
     */
	public int getLicense() {
		return licence;
	}
	
	/**
     * Retrieves the speed of this vehicle person.   
     * <p>
     * @return Number of ticks needed for 1 Km.
     */
	public int getSpeed() {
		return speed;
	}
	
	/**
     * Simulates a delivery by sleeping for the amount of time that 
     * it takes this vehicle to cover {@code distance} KMs.  
     * <p>
     * @param address	The address of the customer.
     * @param distance	The distance from the store to the customer.
     */
	public void deliver(String address, int distance) {
		int timeForSleep = distance/getSpeed();
		try {
			Thread.currentThread().sleep(timeForSleep*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
