package com.tkzc00.usercenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void testRedisson() {
        // list
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("a");
        // map
        RMap<Object, Object> rMap = redissonClient.getMap("test-map");
        rMap.put("a", "1");
        // set
        // stack
    }
}
