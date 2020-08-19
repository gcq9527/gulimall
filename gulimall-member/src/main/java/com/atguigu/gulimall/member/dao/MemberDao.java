package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:24:37
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
