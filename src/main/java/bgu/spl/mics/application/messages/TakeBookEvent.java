package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event{
    //fields
    private String bookToTake;
    public TakeBookEvent(String bookName) {
        this.bookToTake= bookName;
    }

    /**
     *
     * @return the name of the book to be taken
     */
    public String getBookName(){
        return  bookToTake;
    }
}
