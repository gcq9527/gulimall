package com.atguigu.gulimall.ware.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by GuoChengQian on 2020/8/5 17:41
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 根据id查询用户地址信息
     * @param id
     * @return
     */
    @RequestMapping("member/memberreceiveaddress/info/{id}")
    public R info(@PathVariable("id") Long id);
}
