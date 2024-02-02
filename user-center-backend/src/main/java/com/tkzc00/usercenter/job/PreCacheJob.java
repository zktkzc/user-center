package com.tkzc00.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @author tkzc00
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;
    private final List<Long> mainUserIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

    /**
     * 缓存推荐用户，每天执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void preCacheRecommendUser() {
        for (Long mainUserId : mainUserIds) {
            String key = String.format("yupao:user:recommend:%s", mainUserId);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
            try {
                valueOperations.set(key, userPage, 30, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("Redis set error: {}", e.getMessage());
            }
        }
    }
}
