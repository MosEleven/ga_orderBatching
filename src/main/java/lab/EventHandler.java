package lab;

public interface EventHandler<E> {

    EventKey getEventKey();

    boolean handleEvent(Event<E> e);
}
