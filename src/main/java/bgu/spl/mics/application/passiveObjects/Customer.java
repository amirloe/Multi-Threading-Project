package bgu.spl.mics.application.passiveObjects;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */

public class Customer implements Serializable {

	//fields
	private final int id;
	private final String name;
	private String address;
	private int distance;
	private ArrayList<OrderReceipt> issuedReceipts;
	private int creditCard;
	private AtomicInteger availableAmountInCreditCard ;


	public Customer(JsonObject custAsJsonObject) {
		this.id = custAsJsonObject.get("id").getAsInt();
		this.name = custAsJsonObject.get("name").getAsString();
		this.address = custAsJsonObject.get("address").getAsString();
		this.distance = custAsJsonObject.get("distance").getAsInt();
		this.issuedReceipts = new ArrayList<>();
		this.creditCard = custAsJsonObject.get("creditCard").getAsJsonObject().get("number").getAsInt();
		this.availableAmountInCreditCard = new AtomicInteger(custAsJsonObject.get("creditCard").getAsJsonObject().get("amount").getAsInt());

	}


	/**
     * Retrieves the name of the customer.
     */
	public String getName() {

		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {

		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return issuedReceipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return availableAmountInCreditCard.get();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return  creditCard;
	}

	/**
	 * if has enough credit, charge the customer
	 * @param amount amount to charge
	 */
	public void setAvailableAmountInCreditCard(int amount){
		int oldAmmount,newAmmount;
		do{
			oldAmmount = getAvailableCreditAmount();
			newAmmount = getAvailableCreditAmount()-amount;
		}
		while(!availableAmountInCreditCard.compareAndSet(oldAmmount,newAmmount));
	}

	/**
	 * adds the given receipt to the list
	 * @param result the receipt to add
	 */
    public void addReciept(OrderReceipt result) {
	    issuedReceipts.add(result);
    }


}

