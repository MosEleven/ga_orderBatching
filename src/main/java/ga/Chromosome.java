package ga;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Chromosome简介
 * 染色体
 *
 * @author zengxin
 * @date 2021-03-01 12:53
 */
@Builder
@Data
public class Chromosome {

    //适应度
    private double score;

    //等级【预留，暂时没用上】
    private int rank;

    //基因
    private int[] genes;

    //基因深拷贝
    public Chromosome cloneGenes() {
        return Chromosome.builder().genes(genes.clone()).build();
    }

    //返回按基因排序的数据
    public <T> List<T> getListFromGenes(List<T> dateList){
        return Arrays.stream(genes).mapToObj(dateList::get).collect(Collectors.toList());
    }
}
