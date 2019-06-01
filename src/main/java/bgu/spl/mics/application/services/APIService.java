package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import javafx.util.Pair;

import java.util.List;
import java.util.Vector;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	//fields
	private Customer customer;
	private List<Pair<String,Integer>> OrderingSchedule;
	private List<Future<OrderReceipt>> OrderResults;

	public APIService(Customer customer , List<Pair<String,Integer>> OrderingSchedule) {

		super(customer.getName());
		this.customer=customer;
		this.OrderingSchedule = OrderingSchedule;
		OrderResults = new Vector<>();
	}

	/**
	 *  Returns the title of the book to be ordered and remove the order from the list
	 * @param tick the current tick
	 * @return the book to be ordered in the tick, null if no such book exist
	 */
	public Vector<String> getBookByTick(int tick) {
		Vector<String> books = new Vector<>();
		for (Pair<String,Integer> p :
				OrderingSchedule) {
			if (p.getValue().equals(tick)) {
				books.add(p.getKey());
			}
		}
		return books;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, c -> {

			Vector<String> theBooks = getBookByTick(c.getTick());

			if (!theBooks.isEmpty()) {

				for(String currBook:theBooks) {
					Future<OrderReceipt> currFuture = sendEvent(new BookOrderEvent(customer,currBook,c.getTick()));

					if(currFuture!=null){
						OrderResults.add(currFuture);
				}}

			}
			WaitForResults();

			if(c.isFinalTick())
				terminate();
		});


}

	/**
	 * Runs on the Futures who didn't resolved yet at this service
	 * and creates for them a delivery service
	 */

	private void WaitForResults() {
		for(Future<OrderReceipt> currFuture:OrderResults){
			if( currFuture.get()!=null) {

				customer.addReciept(currFuture.get());
				sendEvent(new DeliveryEvent(customer));
				currFuture.resolve(null);
			}
		}
	}
}
