package com.atguigu.gulimall.coupon;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.atguigu.gulimall.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author Guo
 * @Create 2020/7/15 10:47
 * 1.引入oss-starter
 * 2.配置key,endpoint相关信息即可
 * 3. 使用osClient 进行相关操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GulimallProductApplicationTest {



    @Autowired
    CouponService couponService;

    @Test
    public void testFindPath(){
        List<CouponEntity> list = couponService.list();
        System.out.println(list);
    }


}