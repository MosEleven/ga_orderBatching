package lab;

import common.CommonUtil;
import service.entity.Batch;
import service.entity.Order;
import service.CalFitnessService;

import java.math.BigDecimal;
import java.util.*;

/**
 * TimeSystem简介
 * 时间系统
 *
 * @author zengxin
 * @date 2021-03-01 12:57
 */
public class TimeSystem {

    //系统当前时间
    private static BigDecimal currentTime = BigDecimal.ZERO;


    public TimeSystem() {
        System.out.println("【时间系统】已启动");
    }

    public static BigDecimal getCurrentTime(){
        return currentTime;
    }

    public static void updateTime(BigDecimal realReceivingTime){
        currentTime = currentTime.add(realReceivingTime);
    }


    private Map<EventKey,EventSource> sourceMap;
    private Map<EventKey,List<EventHandler>> handlerMap;
    private PriorityQueue<Event> eq = new PriorityQueue<>(Comparator.comparing(Event::getArriveTime));

    public void addEventSource(EventSource source){
        EventKey key = source.getEventKey();
        if (sourceMap.containsKey(key)){
            throw new IllegalArgumentException("key is repeated");
        }
        sourceMap.put(key,source);
    }

    public void registerHandler(EventHandler handler){
        EventKey key = handler.getEventKey();
        List<EventHandler> handlers;
        if ( (handlers=handlerMap.get(key)) == null){
            handlers = new ArrayList<>();
            handlerMap.put(key,handlers);
        }
        handlers.add(handler);
    }

    private void processNextEvent(){
        if (eq.isEmpty()){
            throw new IllegalArgumentException("ending because there is no event");
        }
        Event e = eq.poll();
        List<EventHandler> eventHandlers = handlerMap.get(e.getKey());
        if (eventHandlers == null){
            throw new IllegalArgumentException("no such handler");
        }
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.handleEvent(e);
        }
        EventSource eventSource = sourceMap.get(e.getKey());
        Event nextEvent = eventSource.getNextEvent();
        if (nextEvent != null){
            eq.add(nextEvent);
        }

    }
}
