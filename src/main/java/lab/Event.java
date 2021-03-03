package lab;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Event<E> {

    private EventKey key;

    private BigDecimal arriveTime;

    private E eventData;
}
