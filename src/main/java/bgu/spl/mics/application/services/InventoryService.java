package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	//fields
		private Inventory inventory;


	public InventoryService(int num) {
		super("InventoryService "+num);
		inventory= Inventory.getInstance();
	}

	@Override
	protected void initialize() {


		subscribeEvent(CheckAvailabilityEvent.class, checkAv ->{
			complete(checkAv,inventory.checkAvailabiltyAndGetPrice(checkAv.getBook()));
		});

		subscribeEvent(TakeBookEvent.class, takeBook->{
			complete(takeBook,inventory.take(takeBook.getBookName()));
		});

		subscribeBroadcast(TickBroadcast.class, term->{
			if(term.isFinalTick())
				terminate();
		});
		
	}

}
