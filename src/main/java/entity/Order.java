package entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order简介
 * 订单信息
 *
 * @author zengxin
 * @date 2021-03-01 12:54
 */
@Data
public class Order {

    private int orderNo;

    private int skuNum;

    //订单抵达系统的时间
    private BigDecimal arriveTime;

    //定单经过了时间窗后，进入拣选系统的时间
    private BigDecimal enterTime;

    //订单分批后，经过了排队后的开始拣选时间
    private BigDecimal startTime;

    //订单打包所需时间
    private BigDecimal tPackage;

    //所有服务完成时间
    private BigDecimal finalTime;

    //车辆离开时间
    private BigDecimal departTime;

    private List<OrderDetail> detailList;

    public BigDecimal getTDelay() {
        BigDecimal tDelay = finalTime.subtract(departTime);
        return BigDecimal.ZERO.compareTo(tDelay)>0? BigDecimal.ZERO : tDelay;
    }

}
