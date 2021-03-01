package ga;

import java.util.List;

/**
 * GaCalculate简介
 * 遗传算法自定义计算接口
 *
 * @author zengxin
 * @date 2021-03-01 11:47
 */
public interface GaCalculate<T> {

    /**
     * 计算适应度
     * @param dataList 数据list
     * @return 计算结果
     */
    double calFitness(List<T> dataList);

    /**
     * 检车数据合理性
     * @param dataList 数据list
     * @return return true if data validation is passed
     */
    boolean checkData(List<T> dataList);

    /**
     * 调试时使用方法
     * @param dataList 数据list
     */
    void debug(List<T> dataList);
}
