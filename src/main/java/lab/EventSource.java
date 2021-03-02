package lab;

public interface EventSource<E> {

    EventKey getEventKey();

    Event<E> getNextEvent();
}
