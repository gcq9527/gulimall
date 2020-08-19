package com.atguigu.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.cart.fegin.ProductFeginService;
import com.atguigu.cart.interceptor.CartInterceptort;
import com.atguigu.cart.vo.Cart;
import com.atguigu.cart.vo.CartItem;
import com.atguigu.cart.vo.SkuInfoVo;
import com.atguigu.cart.vo.UserInfoTo;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author Guo
 * @Create 2020/8/3 17:22
 */
@Slf4j
@Service
public class CartServiceImpl implements com.atguigu.cart.service.CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeginService productFeginService;

    private final String CART_PREFIX = "gulimall:cart:";


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            CartItem cartItem = new CartItem();
            //多线程运行
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //1、远程查询要添加的商品信息
                R skuInfo = productFeginService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //设置对应属性
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValue = productFeginService.getSkuSaleAttrValue(skuId);
                //3、远程查询sku的组合信息
                cartItem.setSkuAttr(skuSaleAttrValue);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),s);

            return cartItem;
        } else {
            //购物车有此商品 修改数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }

    }


    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(str,CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptort.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //1、登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //2、如果临时购物车还有数据没有合并
            String tempCartKey = CART_PREFIX + userInfoTo.getUserkey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null) {
                //临时购物车还有数据 需要合并
                for (CartItem cartItem : tempCartItems) {
                    //添加到购物车
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                //清楚临时购物车数据
                clearCart(tempCartKey);
            }
            //3、获取登录后的购物车【包含合并过来的临时购物车的数据 和登陆后的购物车数据】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        } else {
            //2、没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserkey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }




    /**
     * 获取到我们要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptort.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId()!=null) {
            //登录用户
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            //临时用户
            cartKey = CART_PREFIX + userInfoTo.getUserkey();
        }
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(cartKey);
        return stringObjectObjectBoundHashOperations;
    }

    /**
     * 根据cartKey从reids中取出数据
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String)obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
          return collect;
        }
        return null;
    }

    @Override
    public void clearCart(String cartkey) {
        redisTemplate.delete(cartkey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        //根据skuid拿到购物车数据 然后 更改状态
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     *
     * @return
     */
    @Override
    public List<CartItem> getUserCartItems() {
        //从拦截中拿数据
        UserInfoTo userInfoTo = CartInterceptort.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            //拼装用户id 从redis中取出书
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            //过滤数据 调用远程服务 根据skuId查询 最新价格
            List<CartItem> collect = cartItems.stream()
                    .filter(item -> item.getCheck())
                    .map(item -> {
                        //更新为最新价格
                        R price = productFeginService.getPrice(item.getSkuId());
                        //TODO 1、更新为最新价格
                        String data = (String) price.get("data");
                        item.setPrice(new BigDecimal(data));
                        return item;
                    })
                    .collect(Collectors.toList());
            return collect;
        }
    }

}