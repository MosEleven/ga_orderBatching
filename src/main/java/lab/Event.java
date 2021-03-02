package lab;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Event<E> {

    private EventKey key;

    private BigDecimal arriveTime;

    //todo BUYAO LIST
    private List<E> dataList;
}
