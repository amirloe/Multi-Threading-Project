package bgu.spl.mics.application.passiveObjects;

import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	//Fields:

	private String bookTitle;
	private AtomicInteger amount;
	private int price;


	/**
	 *
	 * Constructor that Builds a {@link BookInventoryInfo} from simple objects representing its fields
	 *
	 * <p>
	 * @param _bookTitle the name of the book
	 * @param _amount the amount of the book in the inventory
	 * @param _price the price of the book
	 */

	public BookInventoryInfo(String _bookTitle,int _amount,int _price)
	{
		bookTitle=_bookTitle;
		amount =new AtomicInteger(_amount);
		price=_price;
	}

	/**
	 *
	 * Constructor that Builds a {@link BookInventoryInfo} from {@link JsonObject} object.
	 *
	 * <p>
	 * @param BookAsJsonObject
	 */

    public BookInventoryInfo(JsonObject BookAsJsonObject) {
		bookTitle = BookAsJsonObject.get("bookTitle").getAsString();
		amount =new AtomicInteger( BookAsJsonObject.get("amount").getAsInt());
		price = BookAsJsonObject.get("price").getAsInt();
    }

    /**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount.get();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}


	/**
	 * Decrease the amount of the Book in the inventory by one in an atomic operation.
	 */

	public void decreaseAmount() {
		amount.decrementAndGet();
	}
}
