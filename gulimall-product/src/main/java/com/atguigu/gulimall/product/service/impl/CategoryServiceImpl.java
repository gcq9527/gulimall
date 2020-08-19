package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redsson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2.组装成父子的书形结构
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单 是否别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        //逆序
        Collections.reverse(parentPath);
        return (Long[]) paths.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @CacheEvict:失效模式
     * @param category
     */
    @CacheEvict(value = "category",allEntries = true) //失效模式
//    @CachePut //双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        //根据自身id修改
        this.updateById(category);
        // 根据catid修改name
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     *
     //1、每一个需要缓存的数据我们都来指定要放到那个名字的缓存【缓存的分区（按照业务类型划分）】
     //2、@Cacheable({"category"})
     //      代表当前方法的个结果缓存，如果缓存中，方法不调用
     //      如果缓存中没有，会调用方法 最后将结果放入缓存中
     //3)、默认行为
            1）、如果缓存中有 方法不用调用
            2）、 key默认生成 缓存的名字simplekey 自动生成的key值
            3）、缓存的value值 默认使用java虚拟化机制 将序列化的数据存到redis
            4）、默认过期时间为-1

            自定义
            1）、指定生成的缓存使用的key key的属性指定接受一个spel表达式
                spel表达式地址
            2)、指定缓存的数据存活时间 配置文件中修改ttl
            3）、将数据修改为json格式-
     * @return
     */
    @Cacheable(value = {"category"},key = "'getLevel1Categorys'")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevellCategorys.....");
        List<CategoryEntity> parent_cid = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return parent_cid;
    }

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库.....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1.查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.每一个一级分类 查找这个这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                //从二级分类中
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        //三级分类封装
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        //2.封装成指定格式
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }

    /**
     * 查出所有分类
     *
     * @return
     */
    // TODO 堆外溢出 OutOfDirectMemoryError
    //1）、sprngboot2.0以后默认使用lettuce作为操作redis的客户端 它使用netty进行网络通信
    //2) 、lettuce的bug导致netty堆外内存溢出 -Xmx300m nett如果没有指定堆外内存 默认使用-Xmx300m
    // 可以通过-Dio.netty.maxDirectMemory进行设置
    //解决方案 不能使用-Dio.netty.maxDirectMemory进行设置
    //1.升级lettuce客户端 2.切换使用jedis

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存种放json字符串，拿出json字符串 还用逆转为能用的对象类型 【序列化和反序列化】
        /**
         * 1、空结果缓存，解决缓存穿透
         * 2、设置过期时间（加随机值） 解决缓存雪崩
         * 3、加锁：解决缓存击穿
         */
        //1.如果缓存中有则使用缓冲中的数据
        //JSON跨语言 跨平台兼容
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存不命中。。。。查询数据库");
            //2.缓存中没有查询数据库
            Map<String, List<Catelog2Vo>> catalogJsonFromDd = getCatalogJsonFromDdWithRedissonLock();
            //3.查到的数据再放入缓存 将对象转未json放在缓存中
            String s = JSON.toJSONString(catalogJsonFromDd);
            stringRedisTemplate.opsForValue().set("catalogJSON", s);
            return catalogJsonFromDd;
        }
        System.out.println("缓存命中...直接返回...");
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }

    /**
     *
     * 缓存数据如何和数据库一致
     * 缓存数据一致性
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDdWithRedissonLock() {

        //1.锁的名字 锁的粒度 越细越快
        // 锁的粒度 具体缓存是某个数据 11-号商品
        RLock lock = redsson.getLock("CatalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFormDb = null;
        try {

            dataFormDb = getDataFormDb();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return dataFormDb;


    }


    /**
     * setnx  key存在 sexnx不产生对应操作
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDdWithLocalLock() {
        //1.占分布式锁 去redis占坑 TimeUnit.SECONDS 秒
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            //加锁失败。。执行事务
            //设置过期时间 必须和加锁同步
//            stringRedisTemplate.expire("lock",30, TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFormDb = null;
            try {
                System.out.println("获取分布式锁成功...");
                //查询数据库
                dataFormDb = getDataFormDb();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //使用脚本删除锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]) else return 0 end";
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
                //lock对应keys[1] uuid对应ARGC[1] 如果有就删除 没有就返回0
            }
//          stringRedisTemplate.delete("lock");//删除锁
            //获取值对比+对比成功是删除=原子性操作lua脚本解锁

//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if (lockValue.equals(uuid)) {
//                //删除自己的锁
//                stringRedisTemplate.delete("lock");
//            }
            return dataFormDb;
        } else {
            //加锁失败...重试 synchronized
            //休眠100毫秒
            System.out.println("获取分布式锁失败...等待重试");
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDdWithLocalLock(); //自旋
        }


    }

    private Map<String, List<Catelog2Vo>> getDataFormDb() {
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //缓存不为null 直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库.....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.每一个一级分类 查找这个这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                //从二级分类中
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        //三级分类封装
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        //2.封装成指定格式
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        //3.查到的数据再放入缓存 将对象转未json放在缓存中
        String s = JSON.toJSONString(parent_cid);
        stringRedisTemplate.opsForValue().set("catalogJSON", s);
        return parent_cid;
    }

    //从数据库查询并封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDd() {

        /**
         * 1.将数据库的多次查询变成一次  查询全部数据
         */
        //只要是同一把锁就能锁住这个锁的所有线程
        // 1.synchronized (this) SpringBoot所有容器再容器中都是单例的
        //TODO 本地锁 synchronized JUC(lock) 锁住当前线程 在分布式情况下 必须使用分布式锁
        synchronized (this) {
            //得到锁之后 我们应该再去缓存中确定一次如果没有才继续查询
            return getDataFormDb();
        }
    }

    /**
     * 从已有数据开始过滤 从而达成优化
     *
     * @param selectList
     * @param parent_cid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        //  return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }

    //递归找父节点
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1.收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1.找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2.菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


}