package com.atguigu.gulimall.seckill.Scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Guo
 * @Create 2020/8/9 14:19
 *
 *  定时任务
 * @EnableScheduling 开启定时任务
 * @Scheduled 开启一个定时任务
 *
 * 异步任务：
 *      1、@EnableAsync 开启异步任务
 *      2、@Async 给希望异步执行的方法上批注
 *      3、自动配置类 TaskExecutionAutoConfiguration 属性绑定了TaskExecutionProperties
 */
//@EnableAsync
@Slf4j
@Component
//@EnableScheduling
public class HelloSchedule {

    /**
     * 1、Spring由6位组成  不允许第七位年
     * 2、在周几位置 1-7代表周一到周日
     * 3、定时任务不应该阻塞 默认是阻塞的
     *      1）、可以让业务运行已异步的方式 自己提交到线程池
     *              CompletableFuture.runAsync(()->{
     *                  xxxService.hello();
     *              })
     *      2）、支持定时任务线程池 设置TaskSchedulingProperties
     *             spring.task.scheduling.pool.size=5
     *      3）、让定时任务异步执行
     *          异步任务：
     *
     *          解决异步+定时任务来完成定时任务
     */
//    @Async
//    @Scheduled(cron = "*/5 * * * * ?")
//    public void hello() throws InterruptedException {
//        log.info("hello....");
//        Thread.sleep(3000);
//    }
}