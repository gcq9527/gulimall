package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService detailService;
    @Autowired
    WareSkuService skuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );
        return new PageUtils(page);
    }

    /**
     * 合并
     * @param mergeVo
     */
    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            //1.新建一个
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            //设置状态为新建
            purchaseEntity.setStatus(WareConstant.PurchaseStautsEnum.CREATED.getCode());
            //设置更新和创建时间
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            //拿到采购单Id
            purchaseId = purchaseEntity.getId();
        }
        //TODO 确认采购单状态是0，1才可以合并
        // 拿到采购单集合
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            // 采购单id
            detailEntity.setPurchaseId(finalPurchaseId);
            //采购单状态 1 已分配
            detailEntity.setStatus(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        //批量保存
        detailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
    /**
     * 根据采购单Id
     *      修改采购信息PurcahseEntity 的status 为正在采购
     *      修改采购详情PurchaseDetailEntity的status为领取
     * @param ids
     */
    @Override
    public void received(List<Long> ids) {
        //1.确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id); //根据id查询出采购信息
            return byId;
        }).filter(item -> {
            //状态为：创建 或者已分配 true  否则过滤 false
            if (item.getStatus() == WareConstant.PurchaseStautsEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStautsEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            // 设置已领取
            item.setStatus(WareConstant.PurchaseStautsEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2.改变采购单的状态
        this.updateBatchById(collect);

        //3.改变采购项的状态
        collect.forEach((item) -> {
            // 根据采购单id查询出商品相信信息
            List<PurchaseDetailEntity> entities = detailService.listDetailByPurchaseId(item.getId());

            List<PurchaseDetailEntity> collect1 = entities.stream().map(entity -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(entity.getId());
                //设置状态为正在采购
                detailEntity.setStatus(WareConstant.PurchaseDetailEnum.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            //更新状态
            detailService.updateBatchById(collect1);
        });
    }

    /**
     *
     * @param doneVo
     */
    @Override
    public void done(PurchaseDoneVo doneVo) {

        Long id = doneVo.getId();
        //2.改变采购项状态
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailEnum.HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailEnum.FINISH.getCode());
                //3.将成功采购的进行入库
                PurchaseDetailEntity entity = detailService.getById(item.getItemId());
                skuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        detailService.updateBatchById(updates);

        //1.改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ?WareConstant.PurchaseStautsEnum.FINISH.getCode():WareConstant.PurchaseStautsEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}