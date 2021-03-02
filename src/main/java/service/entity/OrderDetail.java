package service.entity;


import lombok.Builder;
import lombok.Data;

/**
 * OrderDetail简介
 * 订单明细【sku】
 *
 * @author zengxin
 * @date 2021-03-01 12:54
 */
@Builder
@Data
public class OrderDetail {

    private int sku;

    private int pickNum;

    private Position position;
}
