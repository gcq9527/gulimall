package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:13:05
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
