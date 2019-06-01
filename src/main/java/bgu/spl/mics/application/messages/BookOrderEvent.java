package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import com.sun.org.apache.xpath.internal.operations.Or;

public class BookOrderEvent implements Event<OrderReceipt> {
    //Fields:
    private final String bookName;
    private final Customer customer;
    private final int orderTick;


    public BookOrderEvent(Customer customer, String currBook, int orderTick) {
        bookName = currBook;
        this.customer=customer;
        this.orderTick = orderTick;

    }

    /**
     *
     * @return the ordered book name
     */
    public String getBookName() {
        return bookName;
    }

    /**
     *
     * @return the ordering customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     *
     * @return the tick which the order took place in
     */
    public int getOrderTick(){
        return orderTick;
    }


}
