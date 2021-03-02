package service;

import lab.Event;
import lab.EventHandler;
import lab.EventKey;
import service.entity.Order;

import java.util.List;

public class ReceiveOrderHandler implements EventHandler<Order> {

    @Override
    public EventKey getEventKey() {
        return EventKey.ORDER_ARRIVING;
    }

    @Override
    public boolean handleEvent(Event<Order> e) {
        List<Order> orderList = e.getDataList();
        return false;
    }
}
