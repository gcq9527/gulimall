package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 2.逻辑删除
 *  1) 配置全局的逻辑删除
 *  2）配置逻辑删除组件Bean(省略)
 *  3) 加上逻辑删除注解@Table
 *
 *  JSR303
 *  1.给Bean添加效验注解 javax.validation.constraints 并定义自己的message提示
 *  2.开启效验功能注解 @Valid
 *     效果 效验错误以后有默认的响应
 *  3.给效验的bean后紧跟一个BindingResult 就可以得到效验的结果
 *  4.分组效验 (多场景复杂效验)
 *      1) @NotBlank(message="品牌名必须提交",groups={UpdateGroup.class,AddGroup.class}
 *      给效验注解标注什么情况需要进行效验
 *      2) @Validated({AddGroup.class}) 表示会执行添加的对应验证
 *      3 默认没有指定分组的效验注解@NotBlank 在分组效验的情况下不生效 只会在@Validated生效
 *  5.自定义效验
 *      1）、编写一个自定义效验注解
 *      2) 、编写一个自定义效验器
 *      3) 、关联自定义的效验器和自定义的效验注解
 *      @Documented
 *      @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的  】})
 *      @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 *      @Retention(RUNTIME)
 *
 * 统一异常处理
 * @ControllerAdvie
 *
 *
 * 5.模板引擎
 * 1)、thymeleaf-starter 关闭缓存
 * 2)、静态资源都放在static文件夹下就可以按照路径直接访问
 * 3)、页面放在templates下 直接访问
 *      SpringBoot 访问项目的时候，默认会找index
 * 6.整合redis
 * 1）、引入data-redis-starter
 * 2) 、简单配置redis的host信息
 * 3) 、使用springBoot自动配置好的StringRedisTemplate来操作redis
 *  redis-map 存放数据key 数据值value
 *
 * 7、redisson配置
 *      引入依赖
 * <dependency>
 *     <groupId>org.redisson</groupId>
 *     <artifactId>redisson</artifactId>
 *     <version>3.12.0</version>
 * </dependency>
 * 8、整合SringCache 简化缓存开发
 *      1）、引入依赖
 *      spring-boot-starter-cache、springBoot-starter-data-redis
 *      2）、写配置
 *          1）自动配置了那些？
 *          CacheAutoConfiguration 回导入 RedisCacheConfiguration
 *          自动配置好了缓存管理器RedisCacheManage
 *          2)配置使用redis作为缓存
 *          spring.cache.type=redis
 *      3）、测试使用缓存
 *     @Cacheable 触发将数据保存到缓存操作
 *      @CacheEvict 触发将数据从缓存删除的操作
 *      @CachePut 不影响方法执行更新操作
 *      @Caching 组合以上多个操作
 *      @CacheConfig 在类级别共享缓存的相同配置
 *      1)、开启缓存功能
 *          @EnableCaching
 *      2）、只需要使用注解
 *      3）、将数据保存为json格式
 *          自定义RedisCacheConfiguration
 * 4.Spring-cache的不足：
 *      1）、读模式
 *          缓存穿透：查询一个null的数据 解决：缓存空数据 cache-null-value=true
 *          缓存击穿：大量并发进来同时查询一个正好过期的数据。解决：加锁 默认是无加锁的 sync=true
 *          缓存雪崩：大量key同时过期 解决 加随机时间，加上过期时间，spring-cache-redis-time-to
 *      2）、写模式： 缓存与数据一致
 *          1）、读写枷锁
 *          2）、引入Canal 感知到MySQL的更新去更新数据库
 *          3）、读多写多，直接去数据库查询就行
 *      总结：
 *      常规数据 读多写少 即时性，一致性要求不高的数据 完全可以使用Spring-cache：写模式 只要缓存数据有过期时间就行
 *      原理：
 *      cacheManage(RedisCacheManage) -> Cache(RedisCache)->Cache复制缓存的读写
 *
 */
@EnableRedisHttpSession //整合redis存储到session
@EnableCaching
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication()
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
