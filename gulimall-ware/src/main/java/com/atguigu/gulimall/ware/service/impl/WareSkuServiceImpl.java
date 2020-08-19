package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.fegin.OrderFeignService;
import com.atguigu.gulimall.ware.fegin.ProductFeginService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.vo.*;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.x509.NoSuchStoreException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeginService productFeginService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId:1
         * wareId:2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如何还没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().
                eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            // TODO 远程查询sku名字 失败整个事务无需回滚
            //1.自己catch异常
            // TODO 还可以用什么方法让异常出现不会回滚
            //远程查询sku名字
            try{
                R info = productFeginService.info(skuId);
                Map<String,Object> data  =(Map<String,Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock( skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询当前sku的总库存量
            //select sum(stock-stock_locked) from wms_ware_sku where sku_id=1
            Long count = baseMapper.getSkuStock(skuId);

            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count == null ? false:count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
          return collect;
    }

    /**
     * 为某个订单锁定库存
     * 默认只要是运行时异常都会回滚
     * @param vo
     * @return
     *
     * 库存解锁场景
     * 1）、下订单成功 订单过期没有支付被系统自动取消 被用户手动取消 都要解锁库存
     * 2）、下订单成功 库存锁定成功 接下来业务调用失败 导致订单回滚
     * 之前锁定的库存就要自动解锁
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单的详情
         * 追溯
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);


        //1、按照下单的收货地址 找到一个就近的仓库 锁定库存
        //1、找到每个商品在那个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询商品在哪里有库存
            List<Long> wareid = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareid);
            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for(SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品的库存
               throw new NoStockException(skuId);
            }
            //1、每一个商品都锁定成功 当前商品锁定了几件的工作单记录发送给MQ
            //2、锁定失败 前面保存的工作单信息就回滚 发送出去的消息 即使要解锁记录 由于去数据库查不到id所以就不用3
            for (Long wareId : wareIds) {
                //成功返回1 否则返回
                Long count = wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
                if (count == 1) {
                    //当前仓库失败
                    skuStocked = true;
                    //TODO 告诉MQ 库存锁定成功
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(entity);
                    StockLockTo stockLockTo = new StockLockTo();
                    stockLockTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(entity,stockDetailTo);
                    //只发id不行
                    stockLockTo.setDetailId(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockTo);
                    break;
                } else {
                    //当前仓库锁失败 重试下一个仓库
                }
            }
            if (skuStocked == false) {
                //当前仓库所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }
        //3全部锁定成功

        return true;
    }

    public void unlockStock(Long skuId,Long wareId,Integer num,Long taskDetailId) {
        //库存解锁
        wareSkuDao.unlockStock(skuId,wareId,num);
        //更新库存工作单状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLock_status(2);//变为已解锁
        wareOrderTaskDetailService.updateById(entity);
    }

    @Override
    public void unlockStock(StockLockTo to) {

            Long id = to.getId();
            StockDetailTo detail = to.getDetailId();
            Long skuId = detail.getSkuId();
            Long detailId = detail.getId();
            //解锁
            //1、查询数据库关于这个订单的锁定库存消息
            //有 证明库存锁定成功
            // 解锁 订单情况
            //      1、没有这个订单 必须解锁
            //      2、有这个订单 不是解锁库存
            //         订单状态 已取消 解锁库存
            //          没取消 不能解锁
            //没有库存锁定失败了 库存回滚了 这种情况无需解锁
            WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
            if (byId != null) {
                //解锁
                to.getId();
                WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
                //根据订单号查询订单状态
                String orderSn = taskEntity.getOrderSn();
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0) {
                    OrderVo data = r.getData(new TypeReference<OrderVo>() {
                    });
                    if (data == null || data.getStatus() == 4) {
                        //订单已经取消 才能解锁库存
                        if (byId.getLock_status() == 1) {
                            //当前库存工作单详情 状态1 已锁定但是未解锁才可以解锁
                            unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(),detailId);
                        }
                     }
                } else {
                    //消息拒绝后重新放到队列里面 让别人继续消费
                    throw new RuntimeException("远程服务失败");
                }
            } else {

            }
    }

    /**
     * 防止订单服务卡顿 导致订单状态消息一致改不了 库存消息优先到期 查订单新建状态 什么都不做就走了
     * 导致卡顿订单 永远不能解锁库存
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存状态 防止重复解锁库存 根据订单编号
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //找到工作单找到 进行解锁
        List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id)
                .eq("lock_status", 1));

        for (WareOrderTaskDetailEntity entity : entities) {
            unlockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}