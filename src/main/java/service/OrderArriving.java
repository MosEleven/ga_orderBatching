package service;

import com.alibaba.fastjson.JSON;
import common.CU;
import lab.Event;
import lab.EventKey;
import lab.EventSource;
import lab.TimeSystem;
import service.entity.Order;
import service.entity.OrderDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderArriving implements EventSource<Order> {

    private final BigDecimal endReceivingTime;

    private final double receivingRate;

    private final int maxDetailNum;

    public OrderArriving(BigDecimal endReceivingTime) {
        this.endReceivingTime = endReceivingTime;
        this.receivingRate = BaseInfo.RECEIVING_RATE;
        this.maxDetailNum = BaseInfo.MAX_DETAIL_NUM;

    }

    @Override
    public EventKey getEventKey() {
        return EventKey.ORDER_ARRIVING;
    }

    @Override
    public Event<Order> getNextEvent() {
        BigDecimal tInterval = getTInterval();
        if (TimeSystem.getCurrentTime().add(tInterval).compareTo(endReceivingTime) >= 0){
            TimeSystem.publishEvent(buildEvent(EventKey.END_RECEIVING,endReceivingTime,null));
            return null;
        }
        Order order = createOrder(tInterval);
        return buildEvent(order.getArriveTime(),order);
    }

    private BigDecimal getTInterval(){
        //订单间隔时间服从指数分布 t = -1/lambda * ln(1 - p)
        return BigDecimal.valueOf((-1 / receivingRate) * Math.log(Math.random()));
    }

    public Order createOrder(BigDecimal tInterval){

        int skuNum = randomSkuNum();
        Order order = new Order();
        order.setSkuNum(skuNum);
        order.setArriveTime(TimeSystem.getCurrentTime().add(tInterval));

        List<OrderDetail> detailList = new ArrayList<>(skuNum);
        Map<Integer,Integer> detailMap = new HashMap<>();
        for (int i = 0; i < skuNum; i++) {
            int sku = CU.random.nextInt(BaseInfo.TOTAL_SKU_CATEGORIES);
            detailMap.put(sku,detailMap.getOrDefault(sku,0)+1);
        }
        detailMap.forEach((sku,num)->detailList.add(OrderDetail.builder().sku(sku).pickNum(num).position(BaseInfo.getPosition(sku)).build()));
        order.setDetailList(detailList);
        if (!randomDepartTime(order)){
            throw new RuntimeException("没有合适的车次，当前订单内容为:"+ JSON.toJSON(order));
        }

        return order;
    }

    private int randomSkuNum(){
        //todo 再校验一下
        //订单75%概率单品,[miu=1; sigma=0.4348]
        double miu = 1.d;
        double sigma = BaseInfo.SIGMA;
        double num = sigma * CU.random.nextGaussian() + miu;
        while (num<1 || num>maxDetailNum) num = sigma * CU.random.nextGaussian() + miu;
        return (int)Math.round(num);
    }

    private boolean randomDepartTime(Order order){
        int car = CU.random.nextInt(BaseInfo.CAR_NUM);
        for (BigDecimal departTime : BaseInfo.getDepartTime(car)) {
            if (departTime.compareTo(order.getArriveTime()) >= 0){
                order.setDepartTime(departTime);
                return true;
            }
        }
        return false;
    }
}
