package common;

import ga.Chromosome;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * CommonUtil简介
 * 公用工具类
 *
 * @author zengxin
 * @date 2021-03-01 12:53
 */
@UtilityClass
public class CommonUtil {

    public static final Random random = new Random();

    //返回一个打乱了的数组，原数组不变
    public int[] shuffleIntArray(int[] nums){
        int len = nums.length;
        int[] shuffled = nums.clone();
        for (int i = len; i > 0; i--) {
            int change = random.nextInt(i);
            swapIntArray(shuffled,change,i-1);
        }
        return shuffled;
    }

    //从nums中任选n个数
    public int[] randomSelect(int[] nums, int n){
        int len = nums.length;
        int[] selected = nums.clone();
        for (int i = 0; i < n; i++) {
            int r = random.nextInt(len-i);
            swapIntArray(selected,i,i+r);
        }
        return Arrays.copyOf(selected,n);
    }

    //交换数组中的两个位置
    public void swapIntArray(int[] nums, int a, int b){
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }

    //根据适应度比较两条染色体的大小
    public int chromoComparator(Chromosome a, Chromosome b){
        double d = a.getScore() - b.getScore();
        if (d > 0) return 1;
        else if (d < 0) return -1;
        return 0;
    }

    //返回[0,n)的两个不相同的随机数，小的排在前面
    public int[] randomTwoPoints(int n){
        if (n<2) throw new IllegalArgumentException("random two point should accept a param greater than 1");
        int a = random.nextInt(n);
        int b = random.nextInt(n);
        while (b==a) b = random.nextInt(n);
        int[] res = new int[2];
        if (a<b){
            res[0] = a;res[1] = b;
        }else {
            res[0] = b;res[1] = a;
        }
        return res;
    }

}
