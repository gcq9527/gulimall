package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author Guo
 * @email sunlightcs@gmail.com
 * @date 2020-07-10 13:24:37
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    /**
     * 获取默认会员等级
     */
    MemberLevelEntity getDefaultLevel();

}
