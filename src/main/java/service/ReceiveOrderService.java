package service;

import com.alibaba.fastjson.JSON;
import common.CommonUtil;
import entity.Order;
import entity.OrderDetail;

import java.math.BigDecimal;
import java.util.*;

public class ReceiveOrderService {

    private final double receivingRate;

    private final int maxOrderNum;

    private final int minOrderNum;

    private final int maxDetailNum;

    private int waveNo;

    private BigDecimal tNextOrder;

    private List<Order> singleOrders;

    private List<Order> multiOrders;

    //有可能同时两组订单都满足了
    private List<List<Order>> nextWave;

    public ReceiveOrderService() {
        this.receivingRate = 0.025d;
        this.maxOrderNum = 50;
        this.minOrderNum = 10;
        this.maxDetailNum = 100;
        this.waveNo = 0;
        this.tNextOrder = getTNextOrder();
        this.singleOrders = new ArrayList<>(maxOrderNum);
        this.multiOrders = new ArrayList<>(maxOrderNum);

        System.out.println("接单服务已启动");
    }

    public int getWaveNo() {
        return waveNo;
    }

    //订单依次到达，第一次分批的时间节点为固定时间窗，n>=min && n<=max
    //后续分批的时间节点为上一波次订单都拣选完成
    //todo 可以增加一个wave类代表每一波次
    public List<List<Order>> getNextWave(BigDecimal tExceptedNextWave){
        waveNo++;
        nextWave = new ArrayList<>(2);
        System.out.printf("正在接受第%s波次......",waveNo);

        int orderNo = 0;
        //订单间隔时间服从指数分布 t = -1/lambda * ln(1 - p)
        BigDecimal tRealReceiving = BigDecimal.ZERO;
        while (canReceivingNext(tRealReceiving.add(tNextOrder),tExceptedNextWave)) {
            tRealReceiving = tRealReceiving.add(tNextOrder);
            Order order = createOrder(tRealReceiving);
            order.setOrderNo(orderNo++);
            if (order.getSkuNum() == 1){
                singleOrders.add(order);
            }else {
                multiOrders.add(order);
            }
            tNextOrder = getTNextOrder();
        }

        //todo 同时完成两组时间会不会有问题
        TimeSystem.updateTime(tRealReceiving);
        postProcess();

        System.out.println("接收完成，进入分批系统");
        return nextWave;
    }

    private BigDecimal getTNextOrder(){
        //订单间隔时间服从指数分布 t = -1/lambda * ln(1 - p)
        return BigDecimal.valueOf((-1 / receivingRate) * Math.log(Math.random()));
    }

    private boolean canReceivingNext(BigDecimal tReceiving, BigDecimal tExceptedNextWave){
        //是否已经停止接单
        if (TimeSystem.currentTime.add(tReceiving).compareTo(TimeSystem.endReceivingTime) >=0){
            if (!singleOrders.isEmpty()){
                addSingleWave();
            }
            if (!multiOrders.isEmpty()){
                addMultiWave();
            }
            return false;
        }
        //没超时 但单品订单达到上限 拣选单品订单
        else if (singleOrders.size()>=maxOrderNum){
            addSingleWave();
            return false;
        }
        //没超时 单品订单未达上限但多品订单达上限 拣选多品订单
        else if (multiOrders.size()>=maxOrderNum){
            addMultiWave();
            return false;
        }
        //没超时 订单均未达上限 但时间已达到预计下一批次时间
        //todo 同时两种订单会刷新预期时间
        else if (tReceiving.compareTo(tExceptedNextWave) >= 0){
            boolean added = false;
            if (singleOrders.size()>=minOrderNum){
                addSingleWave();
                added = true;
            }
            if (multiOrders.size()>=minOrderNum){
                addMultiWave();
                added = true;
            }
            if (added) return false;
        }
        return true;
    }

    private void addSingleWave(){
        nextWave.add(singleOrders);
        singleOrders = new ArrayList<>(maxOrderNum);
    }
    private void addMultiWave(){
        nextWave.add(multiOrders);
        multiOrders = new ArrayList<>(maxOrderNum);
    }

    private Order createOrder(BigDecimal tRealReceiving){

        int skuNum = randomSkuNum();
        Order order = new Order();
        order.setSkuNum(skuNum);
        order.setArriveTime(TimeSystem.currentTime.add(tRealReceiving));

        List<OrderDetail> detailList = new ArrayList<>(skuNum);
        Map<Integer,Integer> detailMap = new HashMap<>();
        for (int i = 0; i < skuNum; i++) {
            int sku = CommonUtil.random.nextInt(BaseInfo.TOTAL_SKU_CATEGORIES);
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
        double sigma = 0.4348d;
        double num = sigma * CommonUtil.random.nextGaussian() + miu;
        while (num<1 || num>maxDetailNum) num = sigma * CommonUtil.random.nextGaussian() + miu;
        return (int)Math.round(num);
    }

    private boolean randomDepartTime(Order order){
        int car = CommonUtil.random.nextInt(BaseInfo.CAR_NUM);
        for (BigDecimal departTime : BaseInfo.getDepartTime(car)) {
            if (departTime.compareTo(order.getArriveTime()) >= 0){
                order.setDepartTime(departTime);
                return true;
            }
        }
        return false;
    }

    private void postProcess(){
        if (nextWave !=null && !nextWave.isEmpty()){
            for (List<Order> orderList : nextWave) {
                for (Order order : orderList) {
                    order.setEnterTime(TimeSystem.currentTime);
                }
            }
        }
    }
}
