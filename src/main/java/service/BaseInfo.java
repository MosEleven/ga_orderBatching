package service;

import service.entity.Position;

import java.math.BigDecimal;

/**
 * BaseInfo简介
 * 仓库基础数据
 *
 * @author zengxin
 * @date 2021-03-01 12:51
 */
public class BaseInfo {

    //********************************************************************************************//
    //订单生成数据

    public static final double RECEIVING_RATE = 0.2d;

    public static final double SIGMA = 0.4348d;

    public static final int MAX_DETAIL_NUM = 100;

    //********************************************************************************************//
    //订单分批数据

    public static final int MAX_ORDER_NUM = 250;

    public static final int MIN_ORDER_NUM = 150;

    //********************************************************************************************//
    //仓库数据

    //区域数量
    public static final int AREA_NUM = 4;

    //巷道数量
    public static final int TUNNEL_NUM = 4;

    //每个巷道货位数量
    public static final int SHELF_NUM = 24;

    //仓库内总sku种类
    public static final int TOTAL_SKU_CATEGORIES = AREA_NUM * TUNNEL_NUM * SHELF_NUM;

    //起始区域名
    public static final char START_AREA = 'A';

    //每个区域分区时最大拣选数量
    public static final int THRESHOLD = 25;

    //巷道间的宽度
    public static final double TUNNEL_WIDTH = 3.6d;

    //每个货位长度
    public static final double SHELF_LENGTH = 1.1d;

    //巷道长度
    public static final double TUNNEL_LENGTH = SHELF_NUM * SHELF_LENGTH / 2;

    //行走速度
    public static final double V_TRAVEL = 0.5d;

    //基础拣选时间（秒）
    public static final double V_BASE_PICK = 12.0d;

    //每件拣选所需时间（秒/个）
    public static final double V_ADDITION_PICK = 1.0d;

    //打包时间（秒）
    public static final double V_BASE_PACK = 15.0d;

    //找出每件货的时间（秒/件）
    public static final double V_ADDITION_PACK = 5.0d;

    //货位管理
    private static final Position[] positions;

    //车类
    public static final int CAR_NUM = 4;

    //车次
    public static final int CAR_SHIFT = 4;

    //车辆离开时间表
    public static final BigDecimal[][] departTime;

    static {
        positions = new Position[TOTAL_SKU_CATEGORIES];
        for (int i = 0; i < AREA_NUM; i++) {
            char area = (char) ('A'+i);
            for (int tunnel = 0; tunnel < TUNNEL_NUM; tunnel++) {
                for (int shelf = 0; shelf < SHELF_NUM; shelf++) {
                    int n = i* TUNNEL_NUM * SHELF_NUM + tunnel* SHELF_NUM + shelf;
                    positions[n] = Position.builder().area(area).tunnel(tunnel).shelf(shelf+1).build();
                }
            }
        }

        departTime = new BigDecimal[CAR_NUM][CAR_SHIFT];
        departTime[0][0] = BigDecimal.valueOf(7200);
        departTime[0][1] = BigDecimal.valueOf(14400);
        departTime[1][0] = BigDecimal.valueOf(5400);
        departTime[1][1] = BigDecimal.valueOf(12600);
        departTime[1][2] = BigDecimal.valueOf(19800);
        departTime[2][0] = BigDecimal.valueOf(6840);
        departTime[2][1] = BigDecimal.valueOf(10400);
        departTime[2][2] = BigDecimal.valueOf(14040);
        departTime[2][3] = BigDecimal.valueOf(17640);
        departTime[3][0] = BigDecimal.valueOf(7200);
        departTime[3][1] = BigDecimal.valueOf(10800);
        departTime[3][2] = BigDecimal.valueOf(14400);
    }

    private BaseInfo(){}

    public static Position getPosition(int sku){
        if (sku >= TOTAL_SKU_CATEGORIES){
            throw new IllegalArgumentException(String.format("超出sku种类上限，当前传入%s，上限%s",sku,TOTAL_SKU_CATEGORIES));
        }
        return positions[sku];
    }

    public static BigDecimal[] getDepartTime(int car){
        if (car >= CAR_NUM){
            throw new IllegalArgumentException(String.format("超出车辆种类上限，当前传入%s，上限%s",car,CAR_NUM));
        }
        return departTime[car];
    }
}
