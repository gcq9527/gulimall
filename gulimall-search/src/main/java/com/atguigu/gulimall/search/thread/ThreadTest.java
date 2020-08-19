package com.atguigu.gulimall.search.thread;

import com.atguigu.common.utils.PageUtils;

import java.util.concurrent.*;

/**
 * @author Guo
 * @Create 2020/7/29 12:01
 */
public class ThreadTest {
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start");
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果:" + i);
//        }, executor);
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, executor).whenComplete((res,excption) -> {
//            //虽然能得到异常信息 但是没法修改返回数据
//            System.out.println("异步任务完成了。。结果是：" + res + ";异常是" + excption);
//        }).exceptionally(throwable -> {
//            //可以感知异常 同时返回默认值
//            return 10;
//        });
//        Integer integer = integerCompletableFuture.get();
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, executor).handle((res,thr)->{
//          if (res != null) {
//              return res*2;
//          }
//          if (thr != null) {
//              return 0;
//          }
//          return 0;
//        });
//        Integer integer = integerCompletableFuture.get();
        /**
         * 1）、thenRun不能获取到上一步的执行效果 无返回值
         * thenRunAsync(()->{
         *             System.out.println("任务2启动了");
         *         },executor);
         * 2)、thenAcceptAsync能接受上一步结果，但是没有返回值
         * 3）、thenApplyAsync; 能接受上一步返回结果,有返回值
         */
//        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, executor).thenApplyAsync(res -> {
//            System.out.println("任务2启动了。。" + res);
//            return "hello" + res;
//        }, executor);
        /**
         * 两个都完成
         */
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程:" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("任务1运行结果:" + i);
//            return i;
//        }, executor);
//
//        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程:" + Thread.currentThread().getId());
////            int i = 10 / 4;
//            System.out.println("任务2运行结果:");
//            return "Hello";
//        }, executor);

//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3开始...");
//        },executor);

//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务三开始。。。之前结果为:" + f1 +"-->" + f2);
//        },executor);
        //返回f1和f2的结果
//        CompletableFuture<String> future03 = future01.thenCombineAsync(future02, (f1, f2) -> {
//            return f1 + ":" + f2 + "-> HaHa";
//        }, executor);

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, executor);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+156GB";
        }, executor);

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("查询商品介绍");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  return "华为";
        }, executor);

        CompletableFuture<Object> allOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        allOf.get();

        System.out.println("main....end" + allOf.get());
    }

    public static void thread(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start");
        /**
         * 1)、继承Thread
         *        Thread01 thread01 = new Thread01();
         *         thread01.start();
         * 2)、实现Runnable接口
         *         Runnable01 runnable01 = new Runnable01();
         *         new Thread(runnable01).start();
         * 3)、实现Callable接口 + FutureTask (可以拿到返回结果，可以处理异常)
         *       FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
         *         new Thread(integerFutureTask).start();
         *
         *         //阻塞等待线程执行完成 获取返回结果
         *         Integer integer = integerFutureTask.get();
         *         System.out.println(integer);
         * 4)、线程池
         *        给线程池直接提交任务
         */

        /**
         * corePoolSize 保留在池中的线程数 即使处于空闲状态 除非设置了allowCoreThreadTimeOut
         *
         * maximumPoolSize *池中允许的最大线程数
         *
         * keepalivueTime 存活时间 如果当前线程大于core的数量
         *          释放空闲的线程 maximumPoolsize-corePoolSize 只要线程空闲大于指定的keepAlivuetime
         * unit:时间单位
         * BlockingQUeue<Runnable> workQueue 阻塞队列 如果任务有很多 就会将目前多的任务放在队列里面
         *      只要有线程空闲，就会去队列里面取出新的任务继续执行
         * threadFactory 线程创建工厂
         * RejectedExecutionHandler 如果队列满了 按照我们指定得拒绝策略拒绝指定任务
         *
         * 工作顺序
         * 1)、线程池创建好 准备好core数量的核心线程，准备接受任务
         * 1.1、core满了 就将在进来的任务放入阻塞队列中 空闲的core就会自己去阻塞队列获取任务执行
         * 1.2、阻塞队列满了 就直接开新线程执行 最大只能开到max指定数量
         * 1.3、max满了就用RejectedExecutionHandler 拒绝任务
         * 1.4、max都执行完成，有很多空闲 指定时间以后keepAlivueTime以后 释放max-core这些线程
         *
         *       new LinkedBlockingQueue<>() 默认是Integer最大值 内存不够
         *
         *       一个线程池 core:7 max:20 queue:50 100并发进来怎么分配
         *       7个会立即得到执行 50个进入队列 再开13个进行执行，剩下的30个就使用拒绝策略
         *       如果不想抛弃还要执行 CallerRunsPolicy
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
//          Executors.newCachedThreadPool() core是0 所有都可回收
//          Executors.newFixedThreadPool() 固定大小 core=max 都不可以回收
//          Executors.newScheduledThreadPool() 定时任务的线程池
//            Executors.newSingleThreadExecutor() 单线程的线程池,后台从队列里面获取任务 挨个执行
        System.out.println("main....end");
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:" + i);
        }
    }
    public static class Runnable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果:" + i);
            return i;
        }
    }
}