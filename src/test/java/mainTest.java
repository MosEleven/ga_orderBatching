import common.CommonUtil;
import service.entity.Area;
import service.entity.Batch;
import service.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import service.CalFitnessService;
import service.ReceiveOrderService;
import service.factory.CalServiceFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class mainTest {

    DecimalFormat df = CommonUtil.df;

    ReceiveOrderService receiveOrderService = new ReceiveOrderService(new BigDecimal("3600"),new BigDecimal("14400"));

    CalFitnessService calFitnessService = CalServiceFactory.buildDefault();

    @Test
    void compareRoute(){
        int waveNum = 100;
        int orderSize = 50;
        List<List<Order>> waves = new ArrayList<>(waveNum);
        for (int i = 0; i < waveNum; i++) {
            List<Order> list = new ArrayList<>(orderSize);
            for (int j = 0; j < orderSize; j++) {
                list.add(receiveOrderService.createOrder(BigDecimal.ZERO));
            }
            waves.add(list);
        }
        int pickTime = 0;
        //=,>,<
        int[] res = new int[3];
        for (List<Order> wave : waves) {
            List<Batch> batches = calFitnessService.batching(wave);
            for (Batch batch : batches) {
                for (Area area : calFitnessService.divideArea(batch).values()) {
                    if (!area.getDetailList().isEmpty()){
                        double s = calFitnessService.calDistanceByS(area);
                        double sp = calFitnessService.calDistanceBySPlus(area);
                        char comp = compareRouteResult(res,sp,s);
                        log.info("pickTime:{}, sp{}s, sp={}, s={}",++pickTime,comp,df.format(sp),df.format(s));
                    }
                }
            }
        }
        log.info("****************SUMMARIZE****************");
        log.info("total pick : {}",pickTime);
        log.info("sp = s {} times", res[0]);
        log.info("sp > s {} times", res[1]);
        log.info("sp < s {} times", res[2]);
    }

    private char compareRouteResult(int[] res,double sp, double s){
        if (sp == s){
            res[0]++;
            return '=';
        }else if (sp > s){
            res[1]++;
            return '>';
        }
        res[2]++;
        return '<';
    }


}
