package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.excepiton.PhoneExitExcption;
import com.atguigu.gulimall.member.excepiton.UsernameExitException;
import com.atguigu.gulimall.member.service.MemberLevelService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberVo;
import com.atguigu.gulimall.member.vo.SociaIUserVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    MemberLevelDao memberLevelDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void regist(MemberVo memberVo) throws PhoneExitExcption, UsernameExitException {
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();

        //设置默认等级
        MemberLevelEntity defaultLevel = memberLevelDao.getDefaultLevel();
        entity.setLevelId(defaultLevel.getId());

        //检查用户名和手机是否唯一  异常机制
        checkPhoneUnique(memberVo.getPhone());
        checkUserNameUnique(memberVo.getUserName());

        entity.setMobile(memberVo.getPhone());
        entity.setUsername(memberVo.getUserName());

        //密码加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(memberVo.getPassword());
        entity.setPassword(encode);

        entity.setNickname(memberVo.getUserName());

        memberDao.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExitExcption {
        MemberDao memberDao = this.baseMapper;
        Integer mobile = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExitExcption();
        }
    }

    @Override
    public void checkUserNameUnique(String username) throws UsernameExitException {
        MemberDao memberDao = this.baseMapper;
        Integer mobile = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (mobile > 0) {
            throw new UsernameExitException();
        }

    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {

        String loginacct = memberLoginVo.getLoginacct();
        String password = memberLoginVo.getPassword();

        MemberDao memberDao = this.baseMapper;

        //SELECT * FROM `ums_member` WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (memberEntity == null) {
            //没有该用户
            return null;
        } else {
            //拿到密码 $2a$10$FOh7Fyb6VVZsUWrCUOBwhOT8cciEVkWgUobIUu3MmrT6B/Jd54zGG
            String passwordDB = memberEntity.getPassword();
            //验证密码
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordDB);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }


    @Override
    public MemberEntity login(SociaIUserVo sociaIUserVo) throws Exception {
        //登录和注册合并逻辑
        String uid = sociaIUserVo.getUid();
        //1、判断当前用户是否已经 登录过系统
        MemberDao memberDao = this.baseMapper;
        //查询数据库 uid是否存在
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null ){
            //这个用户已经注册 设置 token 过期时间 更新
           MemberEntity update = new MemberEntity();
           update.setId(memberEntity.getId());
           update.setAccessToken(sociaIUserVo.getAccess_token());
           update.setExpiresIn(sociaIUserVo.getExpires_in());
           memberDao.updateById(update);

           memberEntity.setAccessToken(sociaIUserVo.getAccess_token());
           memberEntity.setExpiresIn(sociaIUserVo.getExpires_in());
           return memberEntity;
        } else {
            //2)、没有查到社交用户 需要注册
            MemberEntity regist = new MemberEntity();
            //3)、查询社交用户账户信息
            //https://api.weibo.com/2/users/show.json
            try {
                Map<String, String> query = new HashMap<>();
                query.put("access_token", sociaIUserVo.getAccess_token());
                query.put("uid", sociaIUserVo.getUid());
                //请求用户信息
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    //昵称
                    String name = jsonObject.getString("name");
                    String gener = jsonObject.getString("gender");
                    //....拿到多个信息

                    regist.setNickname(name);
                    System.out.println(name);
                    regist.setGender("m".equals(gener) ? 1 : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            regist.setSocialUid(sociaIUserVo.getUid());
            regist.setAccessToken(sociaIUserVo.getAccess_token());
            regist.setExpiresIn(sociaIUserVo.getExpires_in());
            memberDao.insert(regist);

            return regist;
        }




    }
}