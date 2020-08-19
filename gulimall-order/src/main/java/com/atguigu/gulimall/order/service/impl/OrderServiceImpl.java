package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.order.constant.OrderConstant;
import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.entity.PaymentInfoEntity;
import com.atguigu.gulimall.order.enume.OrderStatusEnum;
import com.atguigu.gulimall.order.feign.CartFeginService;
import com.atguigu.gulimall.order.feign.MemberFeginService;
import com.atguigu.gulimall.order.feign.ProductFeginService;
import com.atguigu.gulimall.order.feign.WmsFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.atguigu.gulimall.order.service.PaymentInfoService;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();


    @Autowired
    MemberFeginService memberFeginService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    CartFeginService cartFeginService;

    @Autowired
    ProductFeginService productFeginService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    PaymentInfoService paymentInfoService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        //获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //每一个请求都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1、远程查询收货地址列表
            List<MemberAddressVo> address = memberFeginService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);


        CompletableFuture<Void> getOrderItem = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //2、远程查询购物车所有选中的购物项
            List<OrderItemVo> currentUserCartItems = cartFeginService.getCurrentUserCartItems();
            confirmVo.setItems(currentUserCartItems);
        }, executor).thenRunAsync(() -> {
            //拿到购物项拿到所有 skuid
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item ->
                    item.getSkuId()
            ).collect(Collectors.toList());
            //远程调用查询是否有货
            R skusHasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                //list转换成map
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, executor);

        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4.其他数据自动计算
        //TODO 5、防重令牌

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //30分钟过期
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);

        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture, getOrderItem).get();

        return confirmVo;
    }

    /**
     * 订单处理
     * @param vo
     * @return
     */
//    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo sumbitOrder(OrderSubmitVo vo) {
        //设置提交数据共享
        orderSubmitVoThreadLocal.set(vo);
        //订单返回数据
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        //设置状态为0
        responseVo.setCode(0);
        //下单 去创建订单 验证令牌 验证价格 锁库存
        //1、验证令牌
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        //拦截器中 拿到用户对象
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        String orderToken = vo.getOrderToken();
        //原子验证令牌 和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                //组装key查询
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),
                orderToken);
        if (result == 0) {
            //验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //令牌验证成功
            //创建订单 订单项等信息
            OrderCreateTo order = createOrder();
            //2、验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            //应付金额 应付总额  相差小于0.01
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                saveOrder(order);
                //4、库存锁定 有异常回滚数据订单数据
                //订单号
                WareSkuLockVo lockVo = new WareSkuLockVo();
                //订单号 所有订单项 skuId skuName num
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                //TODO 远程锁库存
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    //锁成功
                    responseVo.setOrder(order.getOrder());
                     //TODO 订单创建成功 发送消息给mq
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return responseVo;
                } else {
                    //锁定失败
                    //throw new NoStockException();
                    responseVo.setCode(3);
                    return responseVo;
                }
            } else {
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前这个订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            //关单
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            try {
                //TODO 保证消息一定会发送出去 每一个消息都可以做好日志记录
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PayVo    getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);

        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(order.getOrderSn());
        //查询商品名字
        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity itemEntity = order_sn.get(0);
        payVo.setSubject(itemEntity.getSkuName());
        payVo.setBody(itemEntity.getSkuAttrsVals());

        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params)
    {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> order_sn1 = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntityList(itemEntities);
            return order;
        }).collect(Collectors.toList());

       page.setRecords(order_sn1);

        return new PageUtils(page);
    }

    /**
     * 处理支付宝支付结果
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1.保存交易流水
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());

        paymentInfoService.save(infoEntity);
        //2。修改订单状态
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付成功
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo,OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    @Override
    public void createSeckillOrder(SeckillOrderTo entity) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(entity.getOrderSn());
        orderEntity.setMemberId(entity.getMemberId());

        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = entity.getSeckillPrice().multiply(new BigDecimal("" + entity.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        //TODO 保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(entity.getOrderSn());
        itemEntity.setRealAmount(multiply);
        //TODO 获取当前sku信息的详细信息进行设置 productFeignService.getSpuInfoBySkuid()
        itemEntity.setSkuQuantity(entity.getNum());

        orderItemService.save(itemEntity);
    }


    /**
     * 保存订单数据
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity entity = order.getOrder();
        entity.setModifyTime(new Date());
        this.save(entity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 创建订单
     * @return
     */
    private OrderCreateTo createOrder() {

        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1、生成订单号 根据时间随机生成
        String orderSn = IdWorker.getTimeId();
        //创建订单号
        OrderEntity orderEntity = buildOrder();
        orderEntity.setOrderSn(orderSn);
        //2、获取到所有的所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        //3、计算价格、积分相关
        computePrice(orderEntity, itemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(itemEntities);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        //总价格
        BigDecimal total = new BigDecimal("0.0");
        //优惠卷
        BigDecimal coupon = new BigDecimal("0.0");
        //积分
        BigDecimal interation = new BigDecimal("0.0");
        //打折
        BigDecimal promotion = new BigDecimal("0.0");
        //赠送积分
        BigDecimal gift = new BigDecimal("0.0");
        //赠送成长值
        BigDecimal growth = new BigDecimal("0.0");

        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            interation = interation.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth()));
        }
        //1、订单价格相关
        orderEntity.setTotalAmount(total);
        //应付金额 + 运费金额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        //优惠信息
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(interation);
        orderEntity.setCouponAmount(coupon);

        //设置积分信息
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setIntegration(gift.intValue());
        //未删除
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 构建订单号 包含用户订单收货信息
     * @return
     */
    private OrderEntity buildOrder() {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        //创建订单号
        OrderEntity orderEntity = new OrderEntity();
        //获取收货信息 当前线程共享数据
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        //获取收货地址信息 调用远程服务
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        //设置运费信息
        orderEntity.setFreightAmount(fareResp.getFare());
        //设置收货人人信息
        orderEntity.setReceiverCity(fareResp.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        orderEntity.setReceiverPhone(fareResp.getAddress().getPhone());
        orderEntity.setReceiverProvince(fareResp.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareResp.getAddress().getRegion());
        orderEntity.setReceiverName(fareResp.getAddress().getName());
        orderEntity.setMemberId(memberRespVo.getId());
//        orderEntity.setOrderSn();

        //设置订单相关状态 默认待付款
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //确认收货7天
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }


    /**
     * 构建所有订单项数据
     *
     * @return
     */
    public List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确认每个购物项价格
        List<OrderItemVo> currentUserCartItems = cartFeginService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntites = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                //订单号
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntites;
        }
        return null;
    }

    /**
     * 构建莫一个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、订单信息:
        //2、商品的spu信息
        Long skuId = cartItem.getSkuId();
        //调用远程服务 根据skuid查询出spu信息
        R r = productFeginService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        //3、商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //4、优惠信息
        //5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        //6.订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        //减掉优惠金额
        BigDecimal subtract = origin.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getIntegrationAmount());

        itemEntity.setRealAmount(subtract);

        return itemEntity;
    }
}