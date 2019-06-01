package bgu.spl.mics.application.passiveObjects;


import java.util.HashMap;
import java.util.Vector;


/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

    //Fields:
    private Vector<BookInventoryInfo> books;

    /**
     * Threadsafe Singleton as seen in practical sessions
     */

    private static class SingeltonHolder {
        private static Inventory instance = new Inventory();
    }

    private Inventory() {
        books = new Vector<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Inventory getInstance() {
        return SingeltonHolder.instance;
    }

    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(BookInventoryInfo[] inventory) {
        for (BookInventoryInfo currBook : inventory) {
            books.add(currBook);
        }
    }

    /**
     * Attempts to take one book from the store.
     * <p>
     *
     * @param book Name of the book to take from the store
     * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * The first should not change the state of the inventory while the
     * second should reduce by one the number of books of the desired type.
     */

    public OrderResult take(String book) {


        synchronized (book) {

            BookInventoryInfo t_book = findByName(book);
            //if the book is not available or does not exist
            if (t_book == null || t_book.getAmountInInventory() == 0) {
                return OrderResult.NOT_IN_STOCK;
                //Sync By Book name For the option that two threads will arrive here while having 1 book in stock
            } else {
                t_book.decreaseAmount();
            }


            return OrderResult.SUCCESSFULLY_TAKEN;
        }
    }

    /**
     * Finds the {@link BookInventoryInfo} that related to a certain book
     *
     * <p>
     *
     * @param book Name of the book to find in the store
     * @return a {@link BookInventoryInfo} that has the value of {@param book} as his name,
     * if there is no one info with that name, returns null.
     */


    private BookInventoryInfo findByName(String book) {
        for (BookInventoryInfo currBook : books) {
            if (currBook.getBookTitle().equals(book))
                return currBook;
        }
        return null;
    }


    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     *
     * @param book Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public int checkAvailabiltyAndGetPrice(String book) {

        BookInventoryInfo t_book = findByName(book);

        if (t_book == null || t_book.getAmountInInventory() == 0) {
            return -1;
        }
        else {
            return t_book.getPrice();
        }


    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public void printInventoryToFile(String filename) {

        HashMap<String, Integer> output = createMap();
        FileWriter.write(filename,output);
    }

    /**
     * Creates the {@link HashMap} of Books and their amount in the inventory
     * <p>
     * @return the {@link HashMap}
     */
    private HashMap<String, Integer> createMap() {
        HashMap<String, Integer> output = new HashMap<>();
        for (BookInventoryInfo b : books) {
            output.put(b.getBookTitle(), b.getAmountInInventory());
        }
        return output;
    }

}
