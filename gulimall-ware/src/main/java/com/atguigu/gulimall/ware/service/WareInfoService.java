package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:41:33
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户地址计算运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

