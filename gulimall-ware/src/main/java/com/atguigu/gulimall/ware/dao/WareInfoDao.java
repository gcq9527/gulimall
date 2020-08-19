package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:41:33
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
