package com.atguigu.gulimall.product;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

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
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao attrValueDao;

    @Test
    public void test() {
//        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(8L, 225L);
//        System.out.println(attrGroupWithAttrsBySpuId);
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = attrValueDao.getSaleAttrsBySpuId(13l);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    public void redsison() {
        System.out.println(redissonClient);
    }

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(62L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

    @Test
    public void testList() throws Exception {
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("key","");
//        PageUtils pageUtils = attrService.queryBaseAttrPage(map, 0, type);
//        System.out.println(pageUtils);
    }

    @Test
    public void testRedis() {
        //
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //保存
        ops.set("hello","world"+ UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据是:" + hello);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class Prize {
        private Integer id;
        private String data; //奖品具体内容
    }
    @Test
    public void arraytest() {
        Prize prize = new Prize(1,"1号奖品");
        Prize prize2 = new Prize(2,"2号奖品");
        Prize prize3 = new Prize(3,"3号奖品");

        //对象放入集合
        List<Prize> list = new ArrayList<>();
        list.add(prize);
        list.add(prize2);
        list.add(prize3);

        Random r = new Random();
        int num = r.nextInt(3); //生成随机数范围

        System.out.println("随机数为" + num);
        Prize data = list.get(num); //根据id拿取数据 从0开始拿
        System.out.println("奖品信息是" + data);

    }

}