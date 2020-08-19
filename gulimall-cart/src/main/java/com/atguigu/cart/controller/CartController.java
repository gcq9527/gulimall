package com.atguigu.cart.controller;

import com.atguigu.cart.interceptor.CartInterceptort;
import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.Cart;
import com.atguigu.cart.vo.CartItem;
import com.atguigu.cart.vo.UserInfoTo;
import com.atguigu.common.constant.AuthServerConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Guo
 * @Create 2020/8/3 17:25
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;


    /**
     * 拿到最新数据
     * @return
     */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems() {
        return cartService.getUserCartItems();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";

    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check) {

      cartService.checkItem(skuId,check);

      //重定向会购物车页面
      return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 浏览器有一个cookie use-key 标识用户身份 一个月后过期
     * 如果第一次使用jd购物车功能 都会给一个临时用户身份
     * 浏览器以后保存 每次访问都带上这个cookie
     * @param model
     * @return
     */
    @GetMapping("/cart.html")
    public String cartlistPage(Model model) throws ExecutionException, InterruptedException {
//        UserInfoTo userInfoTo = CartInterceptort.threadLocal.get();
//        System.out.println(userInfoTo);
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }


    /**
     *  RedirectAttributes res
     *      res.addFlashAttribute() 将数据放在session里面可以在页面取出 但是只能取出一次
     *      res.addAttributes("url",url) 将数据放在url后面
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes res) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId,num);

        //添加成功后 重定向到index.html
//        model.addAttribute("skuId",skuId);
        res.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId
            ,Model model) {
        //重定向到成功页面 再次查询购物车数据即可
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item",item);
        return "index";
    }
}