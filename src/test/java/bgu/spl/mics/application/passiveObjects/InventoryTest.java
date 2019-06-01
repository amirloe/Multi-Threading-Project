package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory testingInventory;
    private BookInventoryInfo [] booksArray;
    @Before
    public void setUp() throws Exception {
        testingInventory = Inventory.getInstance();
        booksArray = new BookInventoryInfo[3];
        booksArray[0] = new BookInventoryInfo("Ari Potter",3,30);
        booksArray[1] = new BookInventoryInfo("Hanzel And Gretel",5,15);
        booksArray[2] = new BookInventoryInfo("One And Only",1,15);
    }

    @After
    public void tearDown() throws Exception {
    }



    @Test
    public void load(){

        testingInventory.load(booksArray);
        assertEquals(30,testingInventory.checkAvailabiltyAndGetPrice("Ari Potter"));
    }

    @Test
    /*
    check if taking a book that is inside the inventory results in SUCCESSFULLY_TAKEN output
     */
    public void takeGoodBook(){
        testingInventory.load(booksArray);
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN,testingInventory.take("Ari Potter"));
    }

    @Test
    /*
    check if taking a book from the inventory decrease its amount from the inventory
     */
    public void takeReduceNumber(){
        testingInventory.load(booksArray);
        testingInventory.take("One And Only");
        assertEquals(-1,testingInventory.checkAvailabiltyAndGetPrice("One And Only"));
    }

    @Test
        /*
    check if taking a book that is not inside the inventory results in NOT_IN_STOCK output
     */
    public void takeBadBook() {
        testingInventory.load(booksArray);
        assertEquals(OrderResult.NOT_IN_STOCK,testingInventory.take("Kuki Pipo"));
    }

    @Test
    /*
    check if the function returns the real price
     */
    public void checkAvailabiltyAndGetPriceRealOtput() {
        testingInventory.load(booksArray);
        assertEquals(15,testingInventory.checkAvailabiltyAndGetPrice("Hanzel And Gretel"));

    }

    @Test
        /*
    check if the function does not returns false price
     */
    public void checkAvailabiltyAndGetPriceFalseOutput() {
        testingInventory.load(booksArray);
        assertNotEquals(-1,testingInventory.checkAvailabiltyAndGetPrice("Hanzel And Gretel"));

    }

}