package com.tkzc00.usercenter.service;

import com.tkzc00.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void testMiniDistance() {
        String word1 = "你好，世界";
        String word2 = "你好世界";
        String word3 = "你好啊，世界";
        int score1 = AlgorithmUtils.miniDistance(word1, word2);
        Assertions.assertEquals(1, score1);
        int score2 = AlgorithmUtils.miniDistance(word1, word3);
        Assertions.assertEquals(1, score2);
        int score3 = AlgorithmUtils.miniDistance(word2, word3);
        Assertions.assertEquals(2, score3);
    }

    @Test
    void testMiniDistanceForTags() {
        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java", "大二", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");
        int score1 = AlgorithmUtils.miniDistanceForTags(tagList1, tagList2);
        Assertions.assertEquals(2, score1);
        int score2 = AlgorithmUtils.miniDistanceForTags(tagList1, tagList3);
        Assertions.assertEquals(3, score2);
        int score3 = AlgorithmUtils.miniDistanceForTags(tagList2, tagList3);
        Assertions.assertEquals(1, score3);
    }
}
