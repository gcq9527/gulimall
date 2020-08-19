package com.atguigu.common.constant;

/**
 * @author Guo
 * @Create 2020/7/20 10:50
 */
public class WareConstant {

    public enum PurchaseStautsEnum {
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),FINISH(3,"已完成"),
        HASERROR(4,"有异常");
        private int code;
        private String msg;

        PurchaseStautsEnum(int code,String msg) {
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return this.code;
        }
        public String getMsg() {
            return this.msg;
        }
    }
        public enum PurchaseDetailEnum  {
            CREATED(0,"新建"),ASSIGNED(1,"已分配"),
            BUYING(2,"正在采购"),FINISH(3,"已完成"),
            HASERROR(4,"采购失败");
        private int code;
        private String msg;

        PurchaseDetailEnum(int code,String msg) {
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return this.code;
        }
        public String getMsg() {
            return this.msg;
        }
    }
}