package ga;

import lombok.Data;

import java.util.List;

/**
 * Population简介
 * 种群信息
 *
 * @author zengxin
 * @date 2021-03-01 12:54
 */
@Data
public class Population {

    //种群大小（即染色体数量）
    private int size;

    //种群
    private List<Chromosome> chromosomes;
}
