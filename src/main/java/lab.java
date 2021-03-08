import lab.TimeSystem;
import lab.EventKey;

import service.OrderArriving;
import service.ReceiveOrderHandler;
import service.factory.CalServiceFactory;

import java.math.BigDecimal;

public class lab {


    public static void main(String[] args) {

        TimeSystem timeSystem = new TimeSystem();

        OrderArriving orderArriving = new OrderArriving(new BigDecimal("14400"));
        timeSystem.addEventSource(orderArriving);

        //registerFixedTwHandler(timeSystem);
        registerCustomHandler1(timeSystem);
        registerCustomHandler2(timeSystem);

        //registerDyTwHandler(timeSystem);

        timeSystem.start();
    }

    private static void registerDyTwHandler(TimeSystem timeSystem) {
        ReceiveOrderHandler dyTwHandler = ReceiveOrderHandler.getDyTwHandler(new BigDecimal("1400"));
        timeSystem.registerHandler(dyTwHandler);
        timeSystem.registerHandler(EventKey.END_RECEIVING,dyTwHandler::whenEndReceiving);
    }

    private static void registerFixedTwHandler(TimeSystem timeSystem) {
        ReceiveOrderHandler fixedTwHandler = ReceiveOrderHandler.getFixedTwHandler(220);
        timeSystem.registerHandler(fixedTwHandler);
        timeSystem.registerHandler(EventKey.END_RECEIVING,fixedTwHandler::whenEndReceiving);
    }

    private static void registerCustomHandler1(TimeSystem timeSystem){
        ReceiveOrderHandler build = ReceiveOrderHandler.getCustomBuilder()
                .tw(ReceiveOrderHandler.Tw.Fixed)
                .maxOrderNum(150)
                .calFitnessService(CalServiceFactory.builder().setCalDistanceMethod(CalServiceFactory.CalDistanceMethod.S).build())
                .pickType(ReceiveOrderHandler.PickType.FIFO)
                .build();
        timeSystem.registerHandler(build);
        timeSystem.registerHandler(EventKey.END_RECEIVING,build::whenEndReceiving);
    }

    private static void registerCustomHandler2(TimeSystem timeSystem){
        ReceiveOrderHandler build = ReceiveOrderHandler.getCustomBuilder()
                .tw(ReceiveOrderHandler.Tw.Dynamic)
                .maxOrderNum(250)
                .minOrderNum(150)
                .expectedNextWaveTime(new BigDecimal("1100"))
                .pickType(ReceiveOrderHandler.PickType.GA)
                .calFitnessService(CalServiceFactory.builder().setCalDistanceMethod(CalServiceFactory.CalDistanceMethod.Best).build())
                .build();
        timeSystem.registerHandler(build);
        timeSystem.registerHandler(EventKey.END_RECEIVING,build::whenEndReceiving);
    }
}
