package common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilTest {

    @Test
    void shuffle(){
        int[] nums = {1,2,3,4,5,6,7,8};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(CommonUtil.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CommonUtil.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CommonUtil.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CommonUtil.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CommonUtil.shuffleIntArray(nums)));
    }

    @Test
    void randomSelect() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(CommonUtil.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CommonUtil.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CommonUtil.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CommonUtil.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CommonUtil.randomSelect(nums, 4)));
    }

}