package service;

import common.CU;
import ga.GeneAlgorithm;
import lab.Event;
import lab.EventHandler;
import lab.EventKey;
import lab.TimeSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.entity.Order;
import service.entity.Report;
import service.factory.CalServiceFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceiveOrderHandler implements EventHandler<Order> {

    private Logger log;

    private int maxOrderNum;

    private int minOrderNum;

    private int waveNo;

    private Tw tw;

    private List<Order> singleOrders;

    private List<Order> multiOrders;

    private BigDecimal expectedNextWaveTime;

    private BigDecimal tTotalPick;

    private BigDecimal enterTime;

    private PickType pickType = PickType.GA;

    private Report report = new Report();

    private CalFitnessService calFitnessService;

    private GeneAlgorithm<Order> geneAlgorithm;

    public static ReceiveOrderHandler getDyTwHandler(BigDecimal expectedNextWaveTime){
        return new ReceiveOrderHandler(expectedNextWaveTime);
    }

    public static ReceiveOrderHandler getFixedTwHandler(int maxOrderNum){
        return new ReceiveOrderHandler(maxOrderNum);
    }

    public static Builder getCustomBuilder(){
        return new Builder();
    }

    public static class Builder {
        private ReceiveOrderHandler handler;

        private Builder(){
            this.handler = new ReceiveOrderHandler();
        }

        public Builder maxOrderNum(int maxOrderNum){
            handler.maxOrderNum = maxOrderNum;
            return this;
        }
        public Builder minOrderNum(int minOrderNum){
            handler.minOrderNum = minOrderNum;
            return this;
        }
        public Builder tw(Tw tw){
            handler.tw = tw;
            return this;
        }
        public Builder expectedNextWaveTime(BigDecimal expectedNextWaveTime){
            handler.expectedNextWaveTime = expectedNextWaveTime;
            return this;
        }
        public Builder pickType(PickType pickType){
            handler.pickType = pickType;
            return this;
        }
        public Builder calFitnessService(CalFitnessService calFitnessService){
            handler.calFitnessService = calFitnessService;
            return this;
        }
        public ReceiveOrderHandler build(){
            handler.singleOrders = new ArrayList<>(handler.maxOrderNum);
            handler.multiOrders = new ArrayList<>(handler.maxOrderNum);
            handler.geneAlgorithm = new GeneAlgorithm<>(1000,handler.calFitnessService);
            handler.log = LoggerFactory.getLogger(handler.tw.toString());
            return handler;
        }
    }

    private ReceiveOrderHandler(int maxOrderNum){
        this.maxOrderNum = maxOrderNum;
        this.tw = Tw.Fixed;

        this.expectedNextWaveTime = BigDecimal.ZERO;
        this.waveNo = 1;
        this.tTotalPick = BigDecimal.ZERO;
        this.singleOrders = new ArrayList<>(maxOrderNum);
        this.multiOrders = new ArrayList<>(maxOrderNum);
        this.calFitnessService = CalServiceFactory.buildDefault();
        this.geneAlgorithm = new GeneAlgorithm<>(1000,calFitnessService);
        this.log = LoggerFactory.getLogger(tw.toString());
    }

    private ReceiveOrderHandler(BigDecimal expectedNextWaveTime) {
        this.maxOrderNum = BaseInfo.MAX_ORDER_NUM;
        this.minOrderNum = BaseInfo.MIN_ORDER_NUM;
        this.waveNo = 1;
        this.tTotalPick = BigDecimal.ZERO;
        this.singleOrders = new ArrayList<>(maxOrderNum);
        this.multiOrders = new ArrayList<>(maxOrderNum);

        this.tw = Tw.Dynamic;
        this.expectedNextWaveTime = expectedNextWaveTime;
        this.calFitnessService = CalServiceFactory.buildDefault();
        this.geneAlgorithm = new GeneAlgorithm<>(1000,calFitnessService);
        this.log = LoggerFactory.getLogger(tw.toString());
    }

    private ReceiveOrderHandler(){
        this.maxOrderNum = BaseInfo.MAX_ORDER_NUM;
        this.minOrderNum = BaseInfo.MIN_ORDER_NUM;
        this.waveNo = 1;
        this.tTotalPick = BigDecimal.ZERO;

        this.tw = Tw.Dynamic;
        this.expectedNextWaveTime = new BigDecimal("3600");
        this.calFitnessService = CalServiceFactory.buildDefault();
    }

    @Override
    public EventKey getEventKey() {
        return EventKey.ORDER_ARRIVING;
    }

    @Override
    public boolean handleEvent(Event<Order> e) {
        Order order = e.getEventData();

        tryPick();
        receiveOrder(order);
        tryPick();

        return true;
    }

    public boolean whenEndReceiving(Event e){
        boolean picked = false;
        if (!singleOrders.isEmpty()){
            pickSingleOrders();
            picked = true;
        }
        if (!multiOrders.isEmpty()){
            pickMultiOrders();
            picked = true;
        }
        if (picked){
            whenPicked();
        }
        log.info(report.getSpareTimeReport());
        log.info(calFitnessService.report.getPickReport());
        geneAlgorithm.shutdown();
        return true;
    }

    private void receiveOrder(Order order){
        //log.info("{}: receive order at {}",tw,order.getArriveTime());
        if (order.getSkuNum() == 1){
            singleOrders.add(order);
        }else {
            multiOrders.add(order);
        }
    }

    private boolean isOverExpectedTime(){
        return TimeSystem.getCurrentTime().compareTo(expectedNextWaveTime) >= 0;
    }
    private boolean isOverPickFinishTime(){
        return TimeSystem.getCurrentTime().compareTo(calFitnessService.pickFinishTime) >= 0;
    }

    public enum Tw{
        Fixed,Dynamic;
    }

    private void tryPick(){
        enterTime = TimeSystem.getCurrentTime();
        switch (tw){
            case Fixed:
                tryFixedTwPick();
                break;
            case Dynamic:
                tryDynamicTwPick();
                break;
            default:
                throw new IllegalArgumentException("no such Time-Window type");
        }

    }

    private void tryFixedTwPick(){
        boolean picked = false;
        if (singleOrders.size()>=maxOrderNum){
            picked = true;
            pickSingleOrders();
        }else if (multiOrders.size()>=maxOrderNum){
            picked = true;
            pickMultiOrders();
        }
        if (picked){
            whenPicked();
        }
    }

    private void tryDynamicTwPick(){
        boolean picked = false;
        if (singleOrders.size()>=maxOrderNum ||
                (singleOrders.size()>=minOrderNum && isOverExpectedTime())){
            pickSingleOrders();
            picked = true;
        }
        if (multiOrders.size()>=maxOrderNum ||
                (multiOrders.size()>=minOrderNum && isOverExpectedTime())){
            pickMultiOrders();
            picked = true;
        }

        if (picked){
            //expectedNextWaveTime = TimeSystem.getCurrentTime().add(tTotalPick);
            expectedNextWaveTime = calFitnessService.pickFinishTime;
            tTotalPick = BigDecimal.ZERO;
            whenPicked();
        }
    }

    private void whenPicked() {
        System.out.printf("tw:%s, wave:%s, ",tw,waveNo);
        waveNo++;
        log.info("cur:{}, pickQue:{}, next:{}",
                CU.df.format(TimeSystem.getCurrentTime()),
                CU.df.format(calFitnessService.pickFinishTime),
                CU.df.format(expectedNextWaveTime));
        System.out.printf("cur:%s, pickQue:%s, next:%s%n",
                CU.df.format(TimeSystem.getCurrentTime()),
                CU.df.format(calFitnessService.pickFinishTime),
                CU.df.format(expectedNextWaveTime));
    }

    private void pickMultiOrders() {
        pick(multiOrders);
        multiOrders = new ArrayList<>(maxOrderNum);
    }

    private void pickSingleOrders() {
        pick(singleOrders);
        singleOrders = new ArrayList<>(maxOrderNum);
    }

    private void pick(List<Order> orderList){
        BigDecimal spare = TimeSystem.getCurrentTime().subtract(calFitnessService.pickFinishTime);
        if (spare.compareTo(BigDecimal.ZERO)>=0){
            report.addPSpareTime(spare);
        }else report.addNSpareTime(spare);
        log.info("wave: {}",waveNo);
        log.info("拣选系统空闲{}秒", CU.df.format(spare));
        if (isOverPickFinishTime()){
            calFitnessService.pickFinishTime = TimeSystem.getCurrentTime();
        }
        for (Order order : orderList) {
            order.setEnterTime(TimeSystem.getCurrentTime());
        }

        String type = orderList.get(0).getSkuNum()==1? "单品":"多品";
        System.out.printf("%s: ",type);

        BigDecimal tService = way2Pick(orderList);

        System.out.println("总拣选时间为：" + tService);
        tTotalPick = tTotalPick.add(tService);
    }
    public enum PickType{
        GA,FIFO
    }

    private BigDecimal way2Pick(List<Order> orderList){
        BigDecimal tService;
        switch (pickType){
            case GA:
                tService = pickByGA(orderList);break;
            case FIFO:
                tService = pickByFIFO(orderList);break;
            default:
                throw new IllegalArgumentException("no such pick type");
        }
        return tService;
    }

    private BigDecimal pickByGA(List<Order> orderList){
        geneAlgorithm.loadData(orderList);
        List<Order> sortedOrder = geneAlgorithm.ga();
        return calFitnessService.confirmPick(sortedOrder);
    }
    private BigDecimal pickByFIFO(List<Order> orderList){
        return calFitnessService.confirmPick(orderList);
    }

}
