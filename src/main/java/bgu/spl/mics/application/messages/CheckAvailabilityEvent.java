package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event<Integer> {
    //fields
    private String bookToCheck;

    public CheckAvailabilityEvent(String bookName) {
        this.bookToCheck=bookName;
    }
    public String getBook (){
        return  bookToCheck;
    }
}
