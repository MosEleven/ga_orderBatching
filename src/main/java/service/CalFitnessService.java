package service;

import common.CU;
import ga.GaCalculate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.entity.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static service.BaseInfo.*;

/**
 * CalFitnessService简介
 * 订单拣选适应度计算的实现
 *
 * @author zengxin
 * @date 2021-03-01 12:52
 */
public abstract class CalFitnessService implements GaCalculate<Order> {

    public BigDecimal pickFinishTime = BigDecimal.ZERO;

    public Report report = new Report();

    //小数输出格式，精确到小数点后2位

    @Override
    public boolean checkData(List<Order> dataList) {
        List<Batch> batches = batching(dataList);
        return batches.size() > 1;
    }

    @Override
    public double calFitness(List<Order> orderList){

        List<Batch> batches = batching(orderList);
        calTService(batches);
        calTDelay(batches);

        BigDecimal tTotalService = batches.stream().map(Batch::getTService).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tTotalDelay = orderList.stream().map(Order::getTDelay).reduce(BigDecimal.ZERO, BigDecimal::add);

        double power = tTotalService.add(tTotalDelay).doubleValue();

        //计算适应度
        double base = 0d;
        for (Batch batch : batches) {
            double average = batch.getTAverageService();
            base += batch.getAreaList().stream().mapToDouble(a->Math.abs(average-a.getTService())).reduce(0d,Double::sum);
        }

        double score = Math.log(base) * power;
        if (Double.MAX_VALUE <= score){
            System.out.printf("base:%s,power:%s%n",base,power);
            throw new IllegalArgumentException("double溢出,值为："+score);
        }
        return -score;
    }

    private void addReport(BigDecimal tTotalService, BigDecimal tTotalDelay, double base) {
        report.addTTotalPick(tTotalService);
        report.addTTotalDelay(tTotalDelay);
        report.addTTotalDiff(base);
    }

    @Override
    public void debug(List<Order> orderList) {
        List<Batch> batches = batching(orderList);
        calTService(batches);
        calTDelay(batches);

        BigDecimal tTotalService = batches.stream().map(Batch::getTService).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tTotalDelay = orderList.stream().map(Order::getTDelay).reduce(BigDecimal.ZERO, BigDecimal::add);

        double power = tTotalService.add(tTotalDelay).doubleValue();

        //计算适应度
        double base = 0d;
        for (Batch batch : batches) {
            double average = batch.getTAverageService();
            base += batch.getAreaList().stream().mapToDouble(a->Math.abs(average-a.getTService())).reduce(0d,Double::sum);
        }

        addReport(tTotalService,tTotalDelay,base);
        CU.calLog.info("集合共分为{}个批次",batches.size());
        //System.out.printf("集合共分为%s个批次%n",batches.size());
        for (int i = 0; i < batches.size(); i++) {
            CU.calLog.info("批次{}：",i);
            //System.out.printf("批次%s：",i);
            int a = 1;
            for (Area area : batches.get(i).getAreaList()) {
                CU.calLog.info("区域{}的拣选时间为{}；",a++, CU.df.format(area.getTService()));
                //System.out.printf("区域%s的拣选时间为%s；",a++,CommonUtil.df.format(area.getTService()));
            }
        }
        CU.calLog.info("汇总：拣选时间：{}，延迟时间：{}，差异时间：{}", CU.df.format(tTotalService), CU.df.format(tTotalDelay), CU.df.format(base));
        //System.out.printf("%n汇总：%n");
        //System.out.printf("拣选时间：%s，延迟时间：%s，差异时间：%s%n", CommonUtil.df.format(tTotalService),CommonUtil.df.format(tTotalDelay),CommonUtil.df.format(base));
    }

    /**
     * 订单分批方法
     * @param orderList 订单列表
     * @return 分批结果
     */
    public List<Batch> batching(List<Order> orderList){
        List<Batch> batches = new ArrayList<>();
        List<Order> list = new ArrayList<>();
        int[] areas = new int[AREA_NUM];
        for (Order order : orderList) {
            boolean exceed = false;
            for (OrderDetail detail : order.getDetailList()) {
                int area = detail.getPosition().getArea() - START_AREA;
                areas[area]++;
                if (areas[area] > THRESHOLD){
                    exceed = true;
                    break;
                }
            }
            if (exceed){
                batches.add(Batch.builder().orderList(list).build());
                list = new ArrayList<>();
                for (int i = 0; i < AREA_NUM; i++) {
                    areas[i] = 0;
                }
            }
            list.add(order);
        }
        if (!list.isEmpty()) batches.add(Batch.builder().orderList(list).build());
        return batches;
    }

    /**
     * 计算总服务时间
     * @param batches 拣选批次
     */
    public void calTService(List<Batch> batches){
        for (Batch batch : batches) {
            //先分区
            Map<Character, Area> areaMap = divideArea(batch);
            batch.setAreaList(new ArrayList<>(areaMap.values()));

            //计算每个区的服务时间时间
            batch.getAreaList().forEach(this::calAreaTService);

            //计算这个批次的服务时间和平均时间
            double tTotalService = 0.0d;
            double tMaxService = 0.0d;
            for (Area area : batch.getAreaList()) {
                tTotalService = tTotalService + area.getTService();
                if (area.getTService() > tMaxService){
                    tMaxService = area.getTService();
                }
            }
            batch.setTService(BigDecimal.valueOf(tMaxService));
            batch.setTAverageService(tTotalService / batch.getAreaList().size());
        }
    }

    /**
     * 计算延迟时间
     * @param batches 拣选批次
     */
    public void calTDelay(List<Batch> batches){
        BigDecimal tQueue = BigDecimal.ZERO;
        for (Batch batch : batches) {
            tQueue = tQueue.add(batch.getTService());
            for (Order order : batch.getOrderList()) {
                calOrderTPackage(order);
                order.setFinalTime(pickFinishTime.add(tQueue).add(order.getTPackage()));
            }
        }
    }

    /**
     * 订单拣选分区
     * @param batch 拣选批次
     * @return 分区信息
     */
    public Map<Character, Area> divideArea(Batch batch){
        Map<Character, Area> map = new HashMap<>(10);
        for (int i = 0; i < AREA_NUM; i++) {
            char areaName = (char) ('A'+i);
            Area area = new Area();
            area.setDetailList(new ArrayList<>());
            area.setPickedTunnelSet(new HashSet<>());
            map.put(areaName,area);
        }
        for (Order order : batch.getOrderList()) {
            for (OrderDetail detail : order.getDetailList()) {
                Position position = detail.getPosition();
                Area area = map.get(position.getArea());
                area.getDetailList().add(detail);
                int tunnel = position.getTunnel();
                if (tunnel >= area.getMaxTunnelNo()){
                    int shelf = position.getShelf();
                    shelf = (shelf+1)/2;
                    if (shelf > area.getFarthestShelf()){
                        area.setFarthestShelf(shelf);
                    }
                    area.setMaxTunnelNo(tunnel);
                }
                area.getPickedTunnelSet().add(tunnel);
            }
        }
        return map;
    }

    /**
     * 计算每个区域的服务时间
     * @param area 分取拣选信息
     */
    private void calAreaTService(Area area){
        List<OrderDetail> detailList = area.getDetailList();
        if (!detailList.isEmpty()){
            calAreaTTravel(area);
            calAreaTPick(area);
            area.setTService(area.getTTravel() + area.getTPick());
        }
    }

    /**
     * 计算分区拣选行走时间
     * @param area 分取拣选信息
     */
    private void calAreaTTravel(Area area){
        double distance = calDistance(area);
        area.setTTravel(distance / V_TRAVEL);
    }

    public abstract double calDistance(Area area);

    public static double getBestRoute(Area area){
        return Math.min(calDistanceByS(area), calDistanceBySPlus(area));
    }

    public static double calDistanceByS(Area area){
        int pickedTunnelNum = area.getPickedTunnelNum();
        double distance;
        if (pickedTunnelNum%2 == 0){
            distance = TUNNEL_WIDTH *area.getMaxTunnelNo()*2 + pickedTunnelNum* TUNNEL_LENGTH;
        }else {
            distance = TUNNEL_WIDTH *area.getMaxTunnelNo()*2 + (pickedTunnelNum - 1)* TUNNEL_LENGTH + 2* SHELF_LENGTH *area.getFarthestShelf();
        }
        return distance;
    }

    /**
     * 使用改进S路线计算
     * @param area
     * @return
     */
    public static double calDistanceBySPlus(Area area){
        List<OrderDetail> detailList = area.getDetailList();
        int[][] ts = new int[TUNNEL_NUM][2];
        for (OrderDetail detail : detailList) {
            Position position = detail.getPosition();
            int tunnel = position.getTunnel();
            int shelf = (position.getShelf()+1)/2;
            ts[tunnel][0] = CU.minExceptZero(ts[tunnel][0],shelf);
            ts[tunnel][1] = CU.maxExceptZero(ts[tunnel][1],shelf);
        }
        int maxTunnelNo = area.getMaxTunnelNo();
        ts[maxTunnelNo][0] = ts[maxTunnelNo][1];
        int point = 0;
        int top = SHELF_NUM / 2;
        int distance = 0;
        for (int[] tunnel : ts) {
            if (tunnel[0]!=0){
                int up = 2*top - point - tunnel[0];
                int down = point + tunnel[1];
                if (up < down){
                    point = tunnel[0];
                    distance += up;
                }else {
                    point = tunnel[1];
                    distance += down;
                }
            }
        }
        distance += point;
        return distance* SHELF_LENGTH + 2* maxTunnelNo *TUNNEL_WIDTH;
    }

    /**
     * 计算分区拣选拣货时间
     * @param area 分取拣选信息
     */
    private void calAreaTPick(Area area){
        double tPick = 0d;
        Map<Integer, Integer> skuAndPickNum = area.getDetailList().stream()
                .collect(Collectors.toMap(OrderDetail::getSku, OrderDetail::getPickNum, Integer::sum));
        for (Integer pickNum : skuAndPickNum.values()) {
            tPick += V_BASE_PICK + pickNum* V_ADDITION_PICK;
        }
        area.setTPick(tPick);
    }

    /**
     * 计算订单打包时间
     * @param order 订单信息
     */
    private void calOrderTPackage(Order order){
        double tPackage = V_BASE_PACK;
        if (order.getSkuNum() > 1){
            tPackage += order.getSkuNum() * V_ADDITION_PACK;
        }
        order.setTPackage(BigDecimal.valueOf(tPackage));
    }

    public BigDecimal confirmPick(List<Order> orderList){
        debug(orderList);
        List<Batch> batches = batching(orderList);
        calTService(batches);

        BigDecimal startTime = BigDecimal.ZERO;
        for (Batch batch : batches) {
            batch.setStartTime(pickFinishTime.add(startTime));
            for (Order order : batch.getOrderList()) {
                order.setStartTime(batch.getStartTime());
            }
            startTime = startTime.add(batch.getTService());
        }

        BigDecimal tTotalService = batches.stream().map(Batch::getTService).reduce(BigDecimal.ZERO, BigDecimal::add);

        pickFinishTime = pickFinishTime.add(tTotalService);
        return tTotalService;
    }
}
