package lab;

import java.math.BigDecimal;

public interface EventSource<E> {

    EventKey getEventKey();

    Event<E> getNextEvent();

    default Event<E> buildEvent(BigDecimal arriveTime, E eventData){
        Event<E> e = new Event<>();
        e.setKey(getEventKey());
        e.setArriveTime(arriveTime);
        e.setEventData(eventData);
        return e;
    }
    default Event<E> buildEvent(EventKey key, BigDecimal arriveTime, E eventData){
        Event<E> e = new Event<>();
        e.setKey(key);
        e.setArriveTime(arriveTime);
        e.setEventData(eventData);
        return e;
    }
}
