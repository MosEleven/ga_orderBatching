package service;

import lab.Event;
import lab.EventKey;
import lab.EventSource;
import service.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderArrivingEvent implements EventSource<Order> {

    @Override
    public EventKey getEventKey() {
        return EventKey.ORDER_ARRIVING;
    }

    @Override
    public Event<Order> getNextEvent() {
        Event<Order> e = new Event<>();
        List<Order> list = new ArrayList<>();
        e.setDataList(list);
        return e;
    }
}
