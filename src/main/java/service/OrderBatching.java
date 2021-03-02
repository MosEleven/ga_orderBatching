package service;

import service.entity.Order;
import ga.GeneAlgorithm;
import lombok.extern.slf4j.Slf4j;
import lab.TimeSystem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
public class OrderBatching {


    public static void main(String[] args) {
        log.info("**********{},系统开始**********",new Date());


        ReceiveOrderService receiveOrderService = new ReceiveOrderService(new BigDecimal("3600"),new BigDecimal("14400"));

        GeneAlgorithm<Order> geneAlgorithm = new GeneAlgorithm<>(1000,receiveOrderService.calFitnessService);

        List<List<Order>> nextWave = receiveOrderService.getNextWave();
        while (nextWave!=null && !nextWave.isEmpty()){

            for (List<Order> orderList : nextWave) {

                geneAlgorithm.loadData(orderList);
                List<Order> sortedOrder = geneAlgorithm.ga();

                receiveOrderService.processWave(sortedOrder);

            }

            nextWave = receiveOrderService.getNextWave();
        }
        System.out.println("已无订单，模型结束");

    }


}
