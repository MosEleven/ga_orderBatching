package entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Area简介
 * 区拣选信息
 *
 * @author zengxin
 * @date 2021-03-01 10:46
 */
@Data
public class Area {

    //服务时间【即拣选时间】
    private double tService;

    //拣选时行走时间
    private double tTravel;

    //拣货时间
    private double tPick;

    //需要拣选的巷道编号
    private Set<Integer> pickedTunnelSet;

    //需要拣选的巷道数量
    private int pickedTunnelNum;

    //最远的巷道编号
    private int maxTunnelNo;

    //最远那个巷道的最大的货位编号
    private int farthestShelf;

    //该区域内待拣选的订单明细
    private List<OrderDetail> detailList;

    public int getPickedTunnelNum(){
        return pickedTunnelSet.size();
    }
}
