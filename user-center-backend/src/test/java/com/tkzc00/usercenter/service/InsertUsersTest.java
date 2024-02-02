package com.tkzc00.usercenter.service;

import com.tkzc00.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fake" + i);
            user.setAvatarUrl("https://p.qqan.com/up/2020-12/16070652272519101.jpg");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("13512345678");
            user.setEmail("fake" + i + "@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("111111");
            user.setTags("[]");
            user.setProfile("这是一个假用户");
            users.add(user);
        }
        userService.saveBatch(users, 10000);
        stopWatch.stop();
        System.out.println("插入" + INSERT_NUM + "条用户数据，耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrentInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        final int BATCH_SIZE = 5000;
        // 分10组
        int j = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> users = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fake" + i);
                user.setAvatarUrl("https://p.qqan.com/up/2020-12/16070652272519101.jpg");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("13512345678");
                user.setEmail("fake" + i + "@qq.com");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("111111");
                user.setTags("[]");
                user.setProfile("这是一个假用户");
                users.add(user);
                if (j % BATCH_SIZE == 0) break;
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> userService.saveBatch(users, BATCH_SIZE));
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("插入" + INSERT_NUM + "条用户数据，耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }
}
