package com.itheima.reggie;

import org.junit.jupiter.api.Test;

import java.util.*;


class Solution {
    @Test
    public void test() {

    }
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        HashMap<Integer,Integer> map1 = new HashMap<>();
        int res = 0;
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                map1.put(nums1[i] + nums2[j],map1.getOrDefault(nums1[i] + nums2[j],0) + 1);
            }
        }
        for (int i = 0; i < nums3.length; i++) {
            for (int j = 0; j < nums4.length; j++) {
                int sum = nums3[i] + nums4[j];
                Integer val = map1.getOrDefault(-sum, 0);
                res += val;
            }
        }
        return res;
    }

}
