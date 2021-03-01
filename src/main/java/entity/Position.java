package entity;

import lombok.Builder;
import lombok.Data;

/**
 * Position简介
 * 货位信息
 *
 * @author zengxin
 * @date 2021-03-01 12:54
 */
@Builder
@Data
public class Position {

    //分区（共4个）
    private final char area;

    //巷道（0到2）
    private final int tunnel;

    //货格（从1到20）
    private final int shelf;
}
