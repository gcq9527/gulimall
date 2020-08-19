package com.atguigu.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 *  需要计算的属性 重写get方法 每次获取属性都会计算
 * @author Guo
 * @Create 2020/8/3 16:48
 */
public class Cart {

    /**
     * 商品属性
     */
    List<CartItem> items;
    /**
     * 商品数量
     */
    private Integer countNum;
    /**
     * 商品类型数量
     */
    private Integer coutnType;
    /**
     * 商品总价
     */
    private BigDecimal totalAmout;

    /**
     * 减免价格
     */
    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCoutnType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }



    public BigDecimal getTotalAmout() {
        BigDecimal amount = new BigDecimal("0");
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                if (item.getCheck()) { //是否被选中
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }

            }
        }
        //减去优惠总价
        BigDecimal subtract = amount.subtract(getReduce());
        return subtract;
    }

    public void setTotalAmout(BigDecimal totalAmout) {
        this.totalAmout = totalAmout;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}