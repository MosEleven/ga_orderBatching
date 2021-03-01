package entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * Batch简介
 * 拣选批次信息
 *
 * @author zengxin
 * @date 2021-03-01 12:51
 */
@Builder
@Data
public class Batch {

    //优先级
    private int priority;

    //波次号（第几次分批）
    private int waveNo;

    //类型【0：单品】【1：多品】
    private int type;

    //各分区服务时间最大值
    private BigDecimal tService;

    //各分区平均服务时间
    private double tAverageService;

    //todo 该批次预计完成时间，开始时间
    private BigDecimal startTime;

    //当前批次内的订单
    private List<Order> orderList;

    //当前批次按区域划分的拣选明细集合
    private List<Area> areaList;
}
