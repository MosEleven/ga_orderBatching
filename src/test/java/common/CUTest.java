package common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CUTest {

    @Test
    void shuffle(){
        int[] nums = {1,2,3,4,5,6,7,8};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(CU.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CU.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CU.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CU.shuffleIntArray(nums)));
        System.out.println(Arrays.toString(CU.shuffleIntArray(nums)));
    }

    @Test
    void randomSelect() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(CU.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CU.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CU.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CU.randomSelect(nums, 4)));
        System.out.println(Arrays.toString(CU.randomSelect(nums, 4)));
    }

}