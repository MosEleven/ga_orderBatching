package service;

import entity.Order;
import ga.GeneAlgorithm;
import lombok.extern.slf4j.Slf4j;
import service.ReceiveOrderService;
import service.TimeSystem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
public class OrderBatching {


    public static void main(String[] args) {
        log.info("**********{},系统开始**********",new Date());
        //初始下一波次时间1小时
        TimeSystem timeSystem = new TimeSystem(new BigDecimal("3600"));

        GeneAlgorithm<Order> geneAlgorithm = new GeneAlgorithm<>(1000,TimeSystem.calFitnessService);

        ReceiveOrderService receiveOrderService = new ReceiveOrderService();

        List<List<Order>> nextWave = receiveOrderService.getNextWave(timeSystem.getTExceptedNextWave());
        while (nextWave!=null && !nextWave.isEmpty()){

            for (List<Order> orderList : nextWave) {

                geneAlgorithm.loadData(orderList);
                List<Order> sortedOrder = geneAlgorithm.ga();

                timeSystem.processWave(sortedOrder);

                //todo 优先级队列
                //1：紧急系数 2：波次优先

                //System.out.println(sortedOrder);
            }

            nextWave = receiveOrderService.getNextWave(timeSystem.getTExceptedNextWave());
        }
        System.out.println("已无订单，模型结束");

    }

}
