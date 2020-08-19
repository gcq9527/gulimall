package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeginService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeginService couponFeginService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * //TODO 高级部分完善
     * spu: 描述了一个产品的特性
     * sku: 即库存进出计量的单位
     * 1.保存Spu基本信息 pms_spu_info
     * 2.保存Spu的描述图片 pms_spu_info_desc
     * 3.保存Spu的图片集 pms_spu_images
     * 4.保存Spu的规格参数 pms_product_attr_value
     * 远程调用保存
     * 5.保存spu的积分信息 gulimall_sms -> sms_spu_bounds
     *   5.1) 保存sku基本信息 pms_sku_info
     *   5.2) 保存sku的图片信息 pms_sku_images
     *   5.3) sku的销售属性信息 pms_sku_sale_attr_value
     *   5.4) sku的优惠 满减信息 gulimall_sms->sms_sku_ladder
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1.保存Spu基本信息 pms_spu_info
        /**
         * 将商品名称 描述信息 上架状态 分组id 品牌id 赋值给spuInfoEntity对象
         */
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2.保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript(); //拿到图片集合
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        // 设置商品id
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        // 逗号分隔 拿到图片
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.保存Spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        //参数商品id 和图片
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4.保存Spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs(); //拿到基本属性
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            //设置对应属性值 属性id 属性名称 属性值 是否显示 商品id
            valueEntity.setAttrId(attr.getAttrId());
            //通过属性id查询属性对象 设置属性名字
            AttrEntity byId = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(byId.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        //批量保存
        attrValueService.saveProductAttr(collect);

        // 5.保存spu的积分信息 gulimall_sms -> sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        //远程调用服务保存
        R r = couponFeginService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }
        //5.保存当前spu对应的所有的sku信息
        //5.1) 保存sku基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImage = "";
                for (Images image : item.getImages()) {
                    //设置默认图片
                    if (image.getDefaultImg() == 1) {
                        defaultImage = image.getImgUrl();
                    }
                }
                // private String skuName
                // private BigDecimal price
                // private String skuTitle
                // private String skuSubtitle
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L); //销量
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                //插入数据后自增id
                Long skuId = skuInfoEntity.getSkuId();

                //5.2) 保存sku的图片信息 pms_sku_images
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity ->{
                    //返回true就是需要 返回false不需要 图片路径为空过滤
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                //5.3) sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.4) sku的优惠 满减信息 gulimall_sms->sms_sku_ladder
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //验证
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1 ) {
                    R r1 = couponFeginService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }
      
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondtion(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) ->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
                wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(status) && !"0".equalsIgnoreCase(catelogId)) {
                wrapper.eq("catalog_id",catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params),
                wrapper);

        return new PageUtils(page);
    }

    /**
     * 将商品信息上传到Easticsearch
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        //当前spuid对应的sku信息 品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        //拿到skuId集合 map getskuID
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODO 4.查询当前sku的所有可以被用来检索的规格属性

        //根据 spuid 查询 spu属性
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);

        //进行过滤 拿到属性id 从集合里面的spu对象中 返回属性id
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        //在指定的所有属性的集合里面 挑出检索属性 根据id IN查询
        List<Long> searchAttrIds = attrService.saleSearchAttrs(attrIds);

        //将searchAttrIds转换成set
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        
        List<SkuEsModel.Attrs> attrs = new ArrayList<>();

        //从baseAttrs(SPU属性对象)中过滤  主要是拿到属性对象
        List<SkuEsModel.Attrs> attrList = baseAttrs.stream().filter(item -> {
            //idset中是否有item的attrid属性 没有就过滤
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            //过滤好的数据(属性对象) 复制到skuEsModel中的attrs对象中
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        /**
         * 远程调用查询库存 返回true(有库存) false(无库存)
         */
        Map<Long, Boolean> stockMap = null;
        try{
            R r = wareFeignService.getSkusHasStock(skuIdList);
            //泛型的反序列化 拿到数据
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            stockMap = r.getData(typeReference).stream().
                    collect(Collectors.toMap(SkuHasStockVo::getSkuId,
                            item -> item.getHasStock()));
        } catch(Exception e) {
            log.error("远程调用查询库存失败:" + e);
        }

        //2.封装每个sku的信息 从sku信息集合中遍历
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //1.组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            //将现有的sku信息转换到ESmodel中
            BeanUtils.copyProperties(sku, skuEsModel);
            //sku中未和esmodel对应的数据将单独设置
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

            //TODO: 发送远程调用 库存系统查询是否有库存
            //设置库存信息
            if (finalStockMap == null) {
                skuEsModel.setHasStock(false);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            //TODO 热度评分
            skuEsModel.setHotScore(0L);

            //TODO 查询品牌和分类的名字
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());

            //通过esmodel中的分类id查询出分类对象 设置分类名称
            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(category.getName());

            //设置检索属性
            skuEsModel.setAttrs(attrList);

            return skuEsModel;
        }).collect(Collectors.toList());


        //TODO 5.将数据发送给es保存 gulimall-search upProduct封装好的数据
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode() != 0) {
            //远程调用成功
            // TODO 6.修改当前状态 将spu_info属性publish_status修改为状态修改为1 表示已上架
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败 调用失败重新调用一次
            // TODO 7.重复调用?接口冥等性 重试机制? xxx
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        Long spuId = byId.getSpuId();
        SpuInfoEntity byId1 = getById(spuId);
        return byId1;
    }


}