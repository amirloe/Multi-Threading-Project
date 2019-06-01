package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private int currentTick;
    private MoneyRegister moneyle;

    public SellingService(int num) {
        super("Selling Service " + num);
        currentTick = 0;
        moneyle = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeEvent(BookOrderEvent.class, bookEvent -> {
                    //Creates new reciept
                    OrderReceipt receipt = new OrderReceipt(0, getName(), bookEvent.getCustomer().getId(), bookEvent.getBookName(), bookEvent.getOrderTick());
                    receipt.setProcessTick(currentTick);
                    //Ask for the book price
                    Integer price = null;
                    Future<Integer> priceOfTheBook = sendEvent(new CheckAvailabilityEvent(bookEvent.getBookName()));
                    if (priceOfTheBook != null)
                        price = priceOfTheBook.get();
                    //if the check availability resolved in a good way and the book is in stock:
                    if ((price != null) && (price != -1)) {
                        //sync by customer
                        synchronized (bookEvent.getCustomer()) {
                            //if the customer have enough money
                            if (bookEvent.getCustomer().getAvailableCreditAmount() >= price) {
                                chargeAndTakeTheBook(bookEvent,price,receipt);

                            } else {
                                complete(bookEvent, null);
                            }
                        }
                    } else {
                        complete(bookEvent, null);
                    }
                }
        );

        subscribeBroadcast(TickBroadcast.class, term -> {
            currentTick++;
            if (term.isFinalTick())
                terminate();
        });

    }

    /**
     * A Function that take a book from the inventory and charge the customer
     * @param bookEvent
     * @param price
     * @param receipt
     */
    private void chargeAndTakeTheBook(BookOrderEvent bookEvent, Integer price, OrderReceipt receipt) {
        //First take the book than charge the customer
        Future<OrderResult> orderResultFuture = sendEvent(new TakeBookEvent(bookEvent.getBookName()));
        if (orderResultFuture != null) {
            if (orderResultFuture.get() == OrderResult.SUCCESSFULLY_TAKEN) {
                moneyle.chargeCreditCard(bookEvent.getCustomer(), price);
                receipt.setPrice(price);
                receipt.setIssuedTick(currentTick);
                moneyle.file(receipt);
                complete(bookEvent, receipt);
            } else {
                complete(bookEvent, null);
            }
        } else {
            complete(bookEvent, null);
        }
    }

}
