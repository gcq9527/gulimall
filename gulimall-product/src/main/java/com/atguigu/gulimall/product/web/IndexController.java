package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Guo
 * @Create 2020/7/24 9:24
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redsson;

    @Autowired
    RedissonClient redisson;


    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/","index.html"})
    public String indexPage(Model model) {

        //TODO 1、查出所有的1级分类
        List<CategoryEntity> categoryEntities =  categoryService.getLevel1Categorys();

        //视图解析器进行拼串
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    /**
     * 三级分类
     * @return
     */
    @ResponseBody
    @GetMapping("index/catalog.json")
    public  Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        //1.获取一把锁 只要锁的名字一样 就是同一把锁
        RLock lock = redsson.getLock("my-lock");

        //2、加锁
        lock.lock(); //阻塞式等待 加不到锁 就一直等待
        //锁的自动续期 如果业务超长 运行期间自动给锁续上新的30s 不用担心业务时间长 锁过自动删掉
        //2、加锁的业务只要运行完成 就不会给当前续期 即使不手动解锁 锁默认在30s以后自动删除
        //      10秒后自动解锁 自动解锁时间 一定要大于业务执行的时间 时间到了后不会自动续期
        //lock.lock(10,TimeUnit.SECONDS) 时间到了不会给锁续期
        //1、如果我们传递了锁的超时时间 就发送给redis执行脚本 进行站所 默认超时就是我们指定的时间
        //2、如果我们未指定锁的超时时间 就是用30 * 1000 【LockWatchdogTimeOut看门狗的默认使用时间】
        //      只要占锁成功，就会启动一个定时任务 【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】 每隔10s就会自动续期
        // internaLLockLeaseTime[看门狗的时间] / 3.，10s

        //最佳实战
        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功...执行业务...." + Thread.currentThread().getId());
            //模拟业务执行时间
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("释放锁..."+ Thread.currentThread().getId());
        }
        return  "hello";
    }


    /**
     * 保证一定能读到最新数据 修改期间 写锁是一个排他锁（互斥锁） 读锁是一个共享锁
     * 写锁没有释放 读锁就读取不到 必须等待写锁释放
     *  读锁 都可以读取到数据
     *  写锁 -》读锁读取不到数据 写锁完成后 才能读取到数据 保证数据最新 一致性
     *  读 + 读 无锁模式 并发读 只会在redis中记录好 所有当前读锁他们都会加锁成功
     *  写 + 读 等待写锁释放
     *  写 + 写 阻塞方式
     *  读 + 写 有读锁 写也需要等待
     *  只要有写的存在 都需要等待
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
        String s = "";
        RReadWriteLock lock = redisson.getReadWriteLock("my-lock");
        RLock rLock = lock.writeLock();
        try{
            //1、改数据写锁 读数据读锁
            rLock.lock();
            System.out.println("写锁加锁成功...." + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放成功...." + Thread.currentThread().getId());
        }

        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        String s = "";
        RReadWriteLock lock = redisson.getReadWriteLock("my-lock");
        RLock rLock = lock.readLock();
        rLock.lock();
        try{
            System.out.println("读锁加锁成功...." + Thread.currentThread().getId());
            s = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放成功...." + Thread.currentThread().getId());
        }
        return s;
    }


    /**
     * 放假 锁门
     * 1班没人了
     * 5个班都走玩 可以关大门
     * @return
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); //等待闭锁都完成
        return "放假了";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); //计数减一

        return id+ "班的人都走了";
    }

    /**
     * 车库停车
     * 3车位
     *
     * 信号量限流
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire(); //获取一个信号 值 占一个车位
        boolean b = park.tryAcquire(); //有就获取 没有就算了
        if (b) {
             //执行业务
        } else {
            return "error";
        }
        return "ok" + b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        park.release(); //释放一个车位

        return "ok";
    }
}