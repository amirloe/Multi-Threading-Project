package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    //Fields:
    //Map that gives each microService a message queue, and a Locking object for each queue
    private ConcurrentMap<MicroService, BlockingQueue<Message>> MicroServicesQueues;
    private ConcurrentMap<MicroService, Object> lockingKeys;
    //Maps that gives each message a list of subscribers to it
    private ConcurrentMap<Class<? extends Event>, ConcurrentLinkedDeque<MicroService>> EventSubscriptionsLists;
    private ConcurrentMap<Class<? extends Broadcast>, ConcurrentLinkedDeque<MicroService>> BrodcastSubscriptionsLists;
    //Map that gives each event the future he returns
    private ConcurrentMap<Event, Future> EventToFutureMap;


    private static class SingeltonHolder {
        private static MessageBus instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        MicroServicesQueues = new ConcurrentHashMap<>();
        lockingKeys = new ConcurrentHashMap<>();
        EventSubscriptionsLists = new ConcurrentHashMap<>();
        BrodcastSubscriptionsLists = new ConcurrentHashMap<>();
        EventToFutureMap = new ConcurrentHashMap<>();
    }

    public static MessageBus getInstance() {
        return SingeltonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

        if (isRegistered(m)) {

            ConcurrentLinkedDeque<MicroService> tmp = new ConcurrentLinkedDeque<>();

            synchronized (type) {
                ConcurrentLinkedDeque<MicroService> currList = EventSubscriptionsLists.get(type);
                //if the queue does not exist create it
                if (currList == null) {
                    tmp.add(m);
                    EventSubscriptionsLists.put(type, tmp);
                } else {//put in the queue the event
                    if (!currList.contains(m))
                        currList.add(m);
                }
            }
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

        if (isRegistered(m)) {
            ConcurrentLinkedDeque<MicroService> tmp = new ConcurrentLinkedDeque<>();
            synchronized (type) {
                ConcurrentLinkedDeque<MicroService> currList = BrodcastSubscriptionsLists.get(type);
                //if the queue does not exist create it
                if (currList == null) {
                    tmp.add(m);
                    BrodcastSubscriptionsLists.put(type, tmp);
                } else {//put in the queue the event
                    if (!currList.contains(m))
                        currList.add(m);
                }
            }
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> f = EventToFutureMap.get(e);
        if (f != null)
            f.resolve(result);
    }

    /**
     * check if {@code m} is registered to the message bus
     * <p>
     *
     * @param m the {@link MicroService}  to check
     **/

    private Boolean isRegistered(MicroService m) {
        return MicroServicesQueues.get(m) != null;
    }

    @Override
    public void sendBroadcast(Broadcast b) {

        ConcurrentLinkedDeque<MicroService> microsToSub = BrodcastSubscriptionsLists.get(b.getClass());
        if (microsToSub != null) {
            for (MicroService m : microsToSub) {
                if (isRegistered(m)) {
                    try {
                        MicroServicesQueues.get(m).put(b);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        //Find the right queue by round robin manner
        ConcurrentLinkedDeque<MicroService> tmpList = EventSubscriptionsLists.get(e.getClass());

        if (tmpList == null || tmpList.isEmpty())
            return null;

        MicroService theChosenMicros = RoundRobin(tmpList);
        if (theChosenMicros == null)
            return null;

        Future<T> eventFuture = new Future<>();
        //Put the event on the relevant microservice queue
        try {
            //sync with the unregister method
            synchronized (lockingKeys.get(theChosenMicros)) {
                BlockingQueue<Message> queue = MicroServicesQueues.get(theChosenMicros);
                if (queue != null) {
                    EventToFutureMap.put(e, eventFuture);
                    queue.put(e);
                } else
                    return null;
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }


        return eventFuture;
    }

    /**
     * Function that select the next MicroService to receive the event in a round robbin manner
     *
     * @param tmpList a list of microservices
     * @return the chosen microservice
     */
    private MicroService RoundRobin(ConcurrentLinkedDeque<MicroService> tmpList) {

    synchronized (tmpList) {
        MicroService relevantEvent = tmpList.poll();
        if (relevantEvent == null)
            return null;
        tmpList.add(relevantEvent);
        return relevantEvent;
    }

    }


    @Override
    public void register(MicroService m) {

        BlockingQueue<Message> t_queue = new LinkedBlockingDeque<>();
        MicroServicesQueues.putIfAbsent(m, t_queue);
        lockingKeys.put(m, new Object());
    }


    @Override
    public void unregister(MicroService m) {
        if (isRegistered(m)) {

            UnsubscribeFromEvents(m);
            UnsubscribeFromBroadcast(m);

            synchronized (lockingKeys.get(m)) {

                BlockingQueue<Message> tmp = MicroServicesQueues.get(m);
                for (Message mes : tmp) {
                    if (mes instanceof Event) {
                        EventToFutureMap.get(mes).resolve(null);
                    }

                }
                MicroServicesQueues.remove(m);
            }
        }
    }

    /**
     * unsubscribe a microservice from all the broadcast whom he is subscribed to
     *
     * @param m the microservice
     */
    private void UnsubscribeFromBroadcast(MicroService m) {
        for (Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedDeque<MicroService>> entry : BrodcastSubscriptionsLists.entrySet()) {
            if (entry.getValue().contains(m))
                entry.getValue().remove(m);

        }
    }

    /**
     * unsubscribe a microservice from all the events whom he is subscribed to
     *
     * @param m
     */
    private void UnsubscribeFromEvents(MicroService m) {
        for (Map.Entry<Class<? extends Event>, ConcurrentLinkedDeque<MicroService>> entry : EventSubscriptionsLists.entrySet()) {
            if (entry.getValue().contains(m))
                entry.getValue().remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> q = MicroServicesQueues.get(m);
        return q.take();
    }


}
