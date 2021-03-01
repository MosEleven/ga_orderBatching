package service;

import entity.Batch;
import entity.Order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * TimeSystem简介
 * 时间系统
 *
 * @author zengxin
 * @date 2021-03-01 12:57
 */
public class TimeSystem {

    private final DecimalFormat df = new DecimalFormat("####.00");

    //系统当前时间
    public static BigDecimal currentTime = BigDecimal.ZERO;

    //拣选队列完成时间
    public static BigDecimal pickFinishTime = BigDecimal.ZERO;

    //计算适应度及服务时间
    public static CalFitnessService calFitnessService = new CalFitnessService();

    private static BigDecimal tExceptedNextWave;

    public static BigDecimal endReceivingTime = new BigDecimal("14400");

    public TimeSystem(BigDecimal tExceptedNextWave) {
        TimeSystem.tExceptedNextWave = tExceptedNextWave;
        System.out.printf("【时间系统】已启动，预期下一波次截单时间为%s秒，停止接单时间为%s秒%n",tExceptedNextWave.add(currentTime).toString(),endReceivingTime.toString());
    }

    public BigDecimal getTExceptedNextWave(){
        return TimeSystem.tExceptedNextWave;
    }

    public static void updateTime(BigDecimal realReceivingTime){
        currentTime = currentTime.add(realReceivingTime);

        if (currentTime.compareTo(pickFinishTime) > 0){
            pickFinishTime = currentTime;
        }

        tExceptedNextWave = BigDecimal.ZERO;
    }

    public void processWave(List<Order> orderList){

        List<Batch> batches = calFitnessService.batching(orderList);
        calFitnessService.calTService(batches);
        //todo 订单startTime
        BigDecimal tTotalService = batches.stream().map(Batch::getTService).reduce(BigDecimal.ZERO, BigDecimal::add);

        pickFinishTime = pickFinishTime.add(tTotalService);
        tExceptedNextWave = tExceptedNextWave.add(tTotalService);

        System.out.printf("更新系统时间，当前时间：%s；拣选队列完成时间%s；预期下波间隔%s%n",df.format(currentTime),df.format(pickFinishTime),df.format(tExceptedNextWave));
    }
}
