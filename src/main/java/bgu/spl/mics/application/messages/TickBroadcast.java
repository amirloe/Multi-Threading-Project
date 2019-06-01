package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    //fields
    int cuurTick;
    boolean isFinalTick;

    public TickBroadcast(int currTick , boolean isFinalTick){
        this.cuurTick= currTick;
        this.isFinalTick = isFinalTick;
    }

    /**
     *
     * @return the tick which the broadcast was sent
     */
    public int getTick(){
        return cuurTick;
    }

    /**
     *
     * @return whether the current tick is the last one (=duration)
     */
    public boolean isFinalTick() {
        return isFinalTick;
    }
}
