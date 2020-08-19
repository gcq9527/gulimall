package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Guo
 * @Create 2020/8/5 12:18
 */
@FeignClient("gulimall-member")
public interface MemberFeginService {


    @GetMapping("member/memberreceiveaddress/{memeberId}/address")
    public List<MemberAddressVo> getAddress(@PathVariable("memeberId") Long memberId);

}