# 用户中心-后端项目

> by tkzc00

## 使用的技术

- SpringBoot
- MybatisPlus
- Redis
- Spring Scheduler
- Spring Data Redis

## 项目亮点

- 统一的返回格式
- 统一的异常封装，并用全局异常处理器进行处理
- 全局错误码封装，方便前端进行错误提示和返回
- 使用 Redis 来存储用户信息，减少数据库的压力，提高用户访问速度
- 使用 MybatisPlus 来简化数据库操作
- 使用 Spring Scheduler 来定时进行缓存预热，减少用户访问时的等待时间