package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrResVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationDao relationDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //1.保存基本数据
        this.save(attrEntity);
        //2.保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId()!= null) {
            AttrAttrgroupRelationEntity ralation  = new AttrAttrgroupRelationEntity();
            ralation.setAttrGroupId(attr.getAttrGroupId());
            ralation.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(ralation);
        }
    }

    /**
     * 规格参数查询
     * @param param
     * @param catelogId
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> param, long catelogId, String type) throws Exception{
        //根据attr_type(属性类型 0-销售属性 1-新增属性) 进行查询
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(type)?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():
                ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catelogId != 0) {
            wrapper.eq("catelog_id",catelogId);
        }
        //key不为空添加查询条件
        String key = (String)param.get("key");
        if(!StringUtils.isEmpty(key)) {
            // attr_id attr_name
            wrapper.and( (wrappers)->{
               wrappers.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //设置分页参数 以及条件
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(param), wrapper);

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrResVo> respVos = records.stream().map((attrEntity) -> {
            AttrResVo attrResVo = new AttrResVo();
            BeanUtils.copyProperties(attrEntity, attrResVo);

            //1.设置分类和分组名字
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity attrId = attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //2.设置分类
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null ) {
                attrResVo.setCatelogName(categoryEntity.getName());
            }
            return attrResVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Cacheable(value = "attr",key = "'attrinfo:' + #root.args[0]" )
    @Override
    public AttrResVo getAttrInfo(Long attrId) {
        //商品信息对象
        AttrResVo resVo = new AttrResVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,resVo);

        //1.设置分组信息
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrgroupRelation = attrAttrgroupRelationDao.
                    selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrId));
            if (attrgroupRelation != null ) {
                resVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    resVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        //2.设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        resVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        if (categoryEntity != null ) {
            resVo.setCatelogName(categoryEntity.getName());
        }
        return resVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);
        //1.修改分组关联
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();

            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            Integer count = attrAttrgroupRelationDao.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id",attr.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(relationEntity);
            }

        }

    }

    /**
     * 根据分组id查找关联得所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 通过属性分组id查询
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationDao.
                selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrgroupId));

        //返回属性id 通过流对象
        List<Long> attrIds = entities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        if (attrIds == null || attrIds.size() == 0) {
            return null;
        }
        //id批量查询信息 返回collection集合
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    /**
     * 批量删除
     * @param vos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //将vos转换为List 从中拿到对应对象放入集合
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            //将item对象内属性值赋值给relationEntity
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        //批量删除
        relationDao.deleteBatchRelation(entities);
    }

    /**
     *
     * @param params  分页参数
     * @param attrgroupId 属性分组id
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能管理自己所属分类里面的所有属性 拿到分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //拿到分组id
        Long catelogId = attrGroupEntity.getCatelogId();

        //2.当分组只能关联别的分组没有引用的属性
        //2.1)当前分类的其他分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = group.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2)这些分类关联的属性
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = groupId.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //2.3)从当前分类中所有的属性中移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds != null && attrIds.size() > 0) {
            wrapper.notIn("attr_id",attrIds);
        }
        //拿到key 判断是否为空 不为空 查询attr_id  模糊查询attr_name
        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    @Override
    public List<Long> saleSearchAttrs(List<Long> attrIds) {

        /**
         *  attr_id FROM pms_attr WHERE attr_id IN(????) AND search_type = 1
         */
        return baseMapper.selectSearchAttrIds(attrIds);
    }

}