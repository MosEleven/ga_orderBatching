import lab.TimeSystem;
import lab.EventKey;

import service.OrderArriving;
import service.ReceiveOrderHandler;

import java.math.BigDecimal;

public class lab {


    public static void main(String[] args) {

        TimeSystem timeSystem = new TimeSystem();

        OrderArriving orderArriving = new OrderArriving(new BigDecimal("14400"));
        timeSystem.addEventSource(orderArriving);

        registerFixedTwHandler(timeSystem);

        registerDyTwHandler(timeSystem);

        timeSystem.start();
    }

    private static void registerDyTwHandler(TimeSystem timeSystem) {
        ReceiveOrderHandler dyTwHandler = ReceiveOrderHandler.getDyTwHandler(new BigDecimal("3600"));
        timeSystem.registerHandler(dyTwHandler);
        timeSystem.registerHandler(EventKey.END_RECEIVING,dyTwHandler::whenEndReceiving);
    }

    private static void registerFixedTwHandler(TimeSystem timeSystem) {
        ReceiveOrderHandler fixedTwHandler = ReceiveOrderHandler.getFixedTwHandler(50);
        timeSystem.registerHandler(fixedTwHandler);
        timeSystem.registerHandler(EventKey.END_RECEIVING,fixedTwHandler::whenEndReceiving);
    }

    private static void registerCustomHandler(TimeSystem timeSystem){
        ReceiveOrderHandler build = ReceiveOrderHandler.getCustomBuilder().build();
    }
}
