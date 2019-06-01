package bgu.spl.mics.application.passiveObjects;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
    //fields
    ArrayList<OrderReceipt> listOfReceipt;


    /**
     * Retrieves the single instance of this class.
     */
    private static class SingletonHolder {
        private static MoneyRegister instance = new MoneyRegister();
    }

    private MoneyRegister() {
        listOfReceipt = new ArrayList<>();
    }

    public static MoneyRegister getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Saves an order receipt in the money register.
     * <p>
     *
     * @param r The receipt to save in the money register.
     */
    public void file(OrderReceipt r) {

        listOfReceipt.add(r);
    }

    /**
     * Retrieves the current total earnings of the store.
     */
    public int getTotalEarnings() {
        int sum = 0;
        for (OrderReceipt r : listOfReceipt
        ) {
            if (r != null)
                sum = sum + r.getPrice();

        }
        return sum;
    }

    /**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     *
     * @param amount amount to charge
     */

    public void chargeCreditCard(Customer c, int amount) {
        synchronized (c) {
            if (c.getAvailableCreditAmount() >= amount) {
                c.setAvailableAmountInCreditCard(amount);
            }
        }
    }

    /**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output..
     */

    public void printOrderReceipts(String filename) {

        FileWriter.write(filename, listOfReceipt);

    }

}
