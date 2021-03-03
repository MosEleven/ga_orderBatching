package lab;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

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
    private Map<EventKey,EventSource> sourceMap;
    private Map<EventKey,List<Function<Event,Boolean>>> handlerMap;
    private static final PriorityQueue<Event> eq = new PriorityQueue<>(Comparator.comparing(Event::getArriveTime));

    public TimeSystem() {
        sourceMap = new HashMap<>();
        handlerMap = new HashMap<>();
        System.out.println("【时间系统】已启动");
    }

    public static BigDecimal getCurrentTime(){
        return currentTime;
    }

    public static void publishEvent(Event e){
        if (e == null || e.getKey()==null || e.getArriveTime()==null){
            throw new IllegalArgumentException("event is lack of args");
        }
        eq.add(e);
    }


    public void addEventSource(EventSource source){
        EventKey key = source.getEventKey();
        if (sourceMap.containsKey(key)){
            throw new IllegalArgumentException("key is repeated");
        }
        sourceMap.put(key,source);
    }

    public void registerHandler(EventHandler handler){
        EventKey key = handler.getEventKey();
        registerHandler(key,handler::handleEvent);
    }
    public void registerHandler(EventKey key,Function<Event,Boolean> handler){
        List<Function<Event, Boolean>> handlers;
        if ( (handlers=handlerMap.get(key)) == null){
            handlers = new ArrayList<>();
            handlerMap.put(key,handlers);
        }
        handlers.add(handler);
    }

    public void start(){
        invokeInitSource();
        while (!eq.isEmpty()){
            processNextEvent();
        }
        System.out.println("ending because there is no event");
    }

    private void invokeInitSource(){
        for (EventSource source : sourceMap.values()) {
            Event e = source.getNextEvent();
            if (e != null){
                eq.add(e);
            }
        }
    }

    private void processNextEvent(){
        Event e = eq.poll();
        currentTime = e.getArriveTime();
        List<Function<Event, Boolean>> eventHandlers = handlerMap.get(e.getKey());
        if (eventHandlers == null){
            throw new IllegalArgumentException("no such handler");
        }
        for (Function<Event, Boolean> eventHandler : eventHandlers) {
            eventHandler.apply(e);
        }
        EventSource eventSource = sourceMap.get(e.getKey());
        if (eventSource != null){
            Event nextEvent = eventSource.getNextEvent();
            if (nextEvent != null){
                eq.add(nextEvent);
            }
        }
    }
}
