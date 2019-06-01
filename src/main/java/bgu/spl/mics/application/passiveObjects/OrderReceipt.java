package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

	//fields
	private final int orderId;
	private final String seller;
	private final int customerId;
	private final String bookTitle;
	private  int price;
	private int issuedTick;
	private final int orderTick;
	private int processTick;

	public OrderReceipt(int orderId, String seller, int customrId,String bookTitle,int orderTick) {
		this.orderId= orderId;
		this.seller= seller;
		this.customerId=customrId;
		this.bookTitle= bookTitle;
		this.orderTick= orderTick;
		this.issuedTick=-1;
	}



	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return processTick;
	}

	/**
	 *
	 * @param price
	 */

	public void setPrice(int price){
		this.price=price;
	}

	/**
	 *
	 * @param issuedTick
	 */
	public void setIssuedTick(int issuedTick){
		this.issuedTick=issuedTick;
	}

	/**
	 *
	 * @param proccessTick
	 */
	public void setProcessTick(int proccessTick){
		this.processTick =proccessTick;
	}

	public String toString(){
		return ("CustId: "+this.customerId+" bookTitle: "+this.bookTitle+" orderTick: "+this.orderTick+" processTick: "+this.processTick +" issuedTick: "+this.issuedTick+"\n");
	}
}
