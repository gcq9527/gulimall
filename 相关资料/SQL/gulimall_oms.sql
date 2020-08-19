/*
SQLyog Ultimate v12.08 (32 bit)
MySQL - 5.7.31 : Database - gulimall_oms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gulimall_oms` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `gulimall_oms`;

/*Table structure for table `oms_order` */

DROP TABLE IF EXISTS `oms_order`;

CREATE TABLE `oms_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) DEFAULT NULL COMMENT 'member_id',
  `order_sn` char(64) DEFAULT NULL COMMENT '订单号',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '使用的优惠券',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `member_username` varchar(200) DEFAULT NULL COMMENT '用户名',
  `total_amount` decimal(18,4) DEFAULT NULL COMMENT '订单总额',
  `pay_amount` decimal(18,4) DEFAULT NULL COMMENT '应付总额',
  `freight_amount` decimal(18,4) DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(18,4) DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(18,4) DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(18,4) DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(18,4) DEFAULT NULL COMMENT '后台调整订单使用的折扣金额',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】',
  `source_type` tinyint(4) DEFAULT NULL COMMENT '订单来源[0->PC订单；1->app订单]',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `delivery_company` varchar(64) DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) DEFAULT NULL COMMENT '可以获得的成长值',
  `bill_type` tinyint(4) DEFAULT NULL COMMENT '发票类型[0->不开发票；1->电子发票；2->纸质发票]',
  `bill_header` varchar(255) DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(255) DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) DEFAULT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `confirm_status` tinyint(4) DEFAULT NULL COMMENT '确认收货状态[0->未确认；1->已确认]',
  `delete_status` tinyint(4) DEFAULT NULL COMMENT '删除状态【0->未删除；1->已删除】',
  `use_integration` int(11) DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COMMENT='订单';

/*Data for the table `oms_order` */

insert  into `oms_order`(`id`,`member_id`,`order_sn`,`coupon_id`,`create_time`,`member_username`,`total_amount`,`pay_amount`,`freight_amount`,`promotion_amount`,`integration_amount`,`coupon_amount`,`discount_amount`,`pay_type`,`source_type`,`status`,`delivery_company`,`delivery_sn`,`auto_confirm_day`,`integration`,`growth`,`bill_type`,`bill_header`,`bill_content`,`bill_receiver_phone`,`bill_receiver_email`,`receiver_name`,`receiver_phone`,`receiver_post_code`,`receiver_province`,`receiver_city`,`receiver_region`,`receiver_detail_address`,`note`,`confirm_status`,`delete_status`,`use_integration`,`payment_time`,`delivery_time`,`receive_time`,`comment_time`,`modify_time`) values (29,20,'202008081200585831291947486172254210',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 04:00:59'),(30,20,'202008082302512291292114053082230785',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:02:51'),(31,20,'202008082304176951292114415746809858',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:04:18'),(32,20,'202008082305587861292114839749001218',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:05:59'),(33,20,'202008082310312921292115982726320129',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:10:31'),(34,20,'202008082315112841292117157097873409',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:15:11'),(35,20,'202008082316562251292117597252231170',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:16:56'),(36,20,'202008082322005731292118873780355074',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:22:01'),(37,20,'202008082325446451292119813606379522',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:25:45'),(38,20,'202008082331004081292121138012405762',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:31:00'),(39,20,'202008082331193711292121217544798210',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:31:19'),(40,20,'202008082335119241292122192947032066',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:35:12'),(41,20,'202008082337266481292122758012055553',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-08 15:37:27'),(42,20,'202008091041513491292289962695200770',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 02:41:51'),(43,20,'202008091046575061292291246810755074',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 02:46:58'),(44,20,'202008091116482511292298757743841281',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 03:16:48'),(45,20,'202008091233051911292317954813202433',NULL,NULL,NULL,'11736.0000','11744.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,11736,11736,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 04:33:05'),(46,20,'202008091301370701292325134958432257',NULL,NULL,NULL,'11736.0000','11744.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,11736,11736,NULL,NULL,NULL,NULL,NULL,'郭承乾','13112716808',NULL,'上海市','上海市','松江区','大江大厦',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 05:01:37'),(47,20,'202008091303017061292325489943351298',NULL,NULL,NULL,'11736.0000','11744.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,11736,11736,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-09 05:03:02'),(48,16,'202008100909381811292629142772248578',NULL,NULL,NULL,'11736.0000','11744.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,11736,11736,NULL,NULL,NULL,NULL,NULL,'郭承乾','13112716808',NULL,'上海市','上海市','松江区','大江大厦',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:09:38'),(49,20,'202008100910481731292629436335779842',NULL,NULL,NULL,'11736.0000','11744.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,11736,11736,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:10:48'),(50,20,'202008100912138331292629795619860482',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:12:14'),(51,20,'202008100915128151292630546333057025',NULL,NULL,NULL,'6399.0000','6407.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,0,NULL,NULL,7,6399,6399,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:15:13'),(52,20,'202008100915396981292630659080142850',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:15:40'),(53,20,'202008100918195031292631329350893570',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:18:20'),(54,20,'202008100919055271292631522393751553',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:19:06'),(55,20,'202008100923597711292632756546711554',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:24:00'),(56,20,'202008100925229821292633105554837505',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:25:23'),(57,20,'202008100927163591292633581096587265',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:27:16'),(58,20,'202008100929162621292634084001972226',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:29:16'),(59,20,'202008100930142771292634327334641666',NULL,NULL,NULL,'5868.0000','5876.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,5868,5868,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:30:14'),(60,20,'202008100931570501292634758387458049',NULL,NULL,NULL,'29340.0000','29348.0000','8.0000','0.0000','0.0000','0.0000',NULL,NULL,NULL,4,NULL,NULL,7,29340,29340,NULL,NULL,NULL,NULL,NULL,'张三','13112716808',NULL,'广东省','佛山','南海区','佛山大道',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'2020-08-10 01:31:57'),(61,20,'202008111629446301293102287312420865',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(62,20,'202008111632153701293102919565996034',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(63,20,'202008111649467941293107329553694722',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(64,20,'202008111659270561293109763348938754',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(65,20,'202008111700065701293109929078472705',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(66,20,'202008111701405741293110323355631617',NULL,NULL,NULL,NULL,'2.0000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `oms_order_item` */

DROP TABLE IF EXISTS `oms_order_item`;

CREATE TABLE `oms_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
  `order_sn` char(64) DEFAULT NULL COMMENT 'order_sn',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) DEFAULT NULL COMMENT 'spu_name',
  `spu_pic` varchar(500) DEFAULT NULL COMMENT 'spu_pic',
  `spu_brand` varchar(200) DEFAULT NULL COMMENT '品牌',
  `category_id` bigint(20) DEFAULT NULL COMMENT '商品分类id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '商品sku编号',
  `sku_name` varchar(255) DEFAULT NULL COMMENT '商品sku名字',
  `sku_pic` varchar(500) DEFAULT NULL COMMENT '商品sku图片',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '商品sku价格',
  `sku_quantity` int(11) DEFAULT NULL COMMENT '商品购买的数量',
  `sku_attrs_vals` varchar(500) DEFAULT NULL COMMENT '商品销售属性组合（JSON）',
  `promotion_amount` decimal(18,4) DEFAULT NULL COMMENT '商品促销分解金额',
  `coupon_amount` decimal(18,4) DEFAULT NULL COMMENT '优惠券优惠分解金额',
  `integration_amount` decimal(18,4) DEFAULT NULL COMMENT '积分优惠分解金额',
  `real_amount` decimal(18,4) DEFAULT NULL COMMENT '该商品经过优惠后的分解金额',
  `gift_integration` int(11) DEFAULT NULL COMMENT '赠送积分',
  `gift_growth` int(11) DEFAULT NULL COMMENT '赠送成长值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COMMENT='订单项信息';

/*Data for the table `oms_order_item` */

insert  into `oms_order_item`(`id`,`order_id`,`order_sn`,`spu_id`,`spu_name`,`spu_pic`,`spu_brand`,`category_id`,`sku_id`,`sku_name`,`sku_pic`,`sku_price`,`sku_quantity`,`sku_attrs_vals`,`promotion_amount`,`coupon_amount`,`integration_amount`,`real_amount`,`gift_integration`,`gift_growth`) values (27,NULL,'202008081200585831291947486172254210',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(28,NULL,'202008082302512291292114053082230785',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(29,NULL,'202008082304176951292114415746809858',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(30,NULL,'202008082305587861292114839749001218',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(31,NULL,'202008082310312921292115982726320129',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(32,NULL,'202008082315112841292117157097873409',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(33,NULL,'202008082316562251292117597252231170',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(34,NULL,'202008082322005731292118873780355074',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(35,NULL,'202008082325446451292119813606379522',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(36,NULL,'202008082331004081292121138012405762',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(37,NULL,'202008082331193711292121217544798210',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(38,NULL,'202008082335119241292122192947032066',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(39,NULL,'202008082337266481292122758012055553',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(40,NULL,'202008091041513491292289962695200770',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(41,NULL,'202008091046575061292291246810755074',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(42,NULL,'202008091116482511292298757743841281',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(43,NULL,'202008091233051911292317954813202433',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',2,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','11736.0000',11736,11736),(44,NULL,'202008091301370701292325134958432257',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',2,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','11736.0000',11736,11736),(45,NULL,'202008091303017061292325489943351298',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',2,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','11736.0000',11736,11736),(46,NULL,'202008100909381811292629142772248578',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',2,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','11736.0000',11736,11736),(47,NULL,'202008100910481731292629436335779842',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',2,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','11736.0000',11736,11736),(48,NULL,'202008100912138331292629795619860482',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(49,NULL,'202008100915128151292630546333057025',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,2,'华为 HUAWEI Mate 30 Pro  星河银 8GB+256GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','6399.0000',1,'颜色:星河银;版本:8GB+256GB','0.0000','0.0000','0.0000','6399.0000',6399,6399),(50,NULL,'202008100915396981292630659080142850',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(51,NULL,'202008100918195031292631329350893570',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(52,NULL,'202008100919055271292631522393751553',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(53,NULL,'202008100923597711292632756546711554',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(54,NULL,'202008100925229821292633105554837505',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(55,NULL,'202008100927163591292633581096587265',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(56,NULL,'202008100929162621292634084001972226',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(57,NULL,'202008100930142771292634327334641666',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',1,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','5868.0000',5868,5868),(58,NULL,'202008100931570501292634758387458049',8,'华为 HUAWEI Mate 30 Pro ',NULL,'8',225,1,'华为 HUAWEI Mate 30 Pro  星河银 8GB+128GB麒麟990 OLED环幕屏双4000万徕卡电影四摄5G全网通','https://gulimall-oss01.oss-cn-shenzhen.aliyuncs.com/2020-07-19//46bfbeeb-50ad-483d-8764-40422189e4ac_f753fc5c13707e7e.jpg','5868.0000',5,'颜色:星河银;版本:8GB+128GB','0.0000','0.0000','0.0000','29340.0000',29340,29340),(59,NULL,'202008111629446301293102287312420865',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL),(60,NULL,'202008111632153701293102919565996034',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL),(61,NULL,'202008111649467941293107329553694722',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL),(62,NULL,'202008111659270561293109763348938754',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL),(63,NULL,'202008111700065701293109929078472705',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL),(64,NULL,'202008111701405741293110323355631617',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,'2.0000',NULL,NULL);

/*Table structure for table `oms_order_operate_history` */

DROP TABLE IF EXISTS `oms_order_operate_history`;

CREATE TABLE `oms_order_operate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `operate_man` varchar(100) DEFAULT NULL COMMENT '操作人[用户；系统；后台管理员]',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  `order_status` tinyint(4) DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作历史记录';

/*Data for the table `oms_order_operate_history` */

/*Table structure for table `oms_order_return_apply` */

DROP TABLE IF EXISTS `oms_order_return_apply`;

CREATE TABLE `oms_order_return_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '退货商品id',
  `order_sn` char(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '申请时间',
  `member_username` varchar(64) DEFAULT NULL COMMENT '会员用户名',
  `return_amount` decimal(18,4) DEFAULT NULL COMMENT '退款金额',
  `return_name` varchar(100) DEFAULT NULL COMMENT '退货人姓名',
  `return_phone` varchar(20) DEFAULT NULL COMMENT '退货人电话',
  `status` tinyint(1) DEFAULT NULL COMMENT '申请状态[0->待处理；1->退货中；2->已完成；3->已拒绝]',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `sku_img` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `sku_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `sku_brand` varchar(200) DEFAULT NULL COMMENT '商品品牌',
  `sku_attrs_vals` varchar(500) DEFAULT NULL COMMENT '商品销售属性(JSON)',
  `sku_count` int(11) DEFAULT NULL COMMENT '退货数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '商品单价',
  `sku_real_price` decimal(18,4) DEFAULT NULL COMMENT '商品实际支付单价',
  `reason` varchar(200) DEFAULT NULL COMMENT '原因',
  `description述` varchar(500) DEFAULT NULL COMMENT '描述',
  `desc_pics` varchar(2000) DEFAULT NULL COMMENT '凭证图片，以逗号隔开',
  `handle_note` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `handle_man` varchar(200) DEFAULT NULL COMMENT '处理人员',
  `receive_man` varchar(100) DEFAULT NULL COMMENT '收货人',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `receive_note` varchar(500) DEFAULT NULL COMMENT '收货备注',
  `receive_phone` varchar(20) DEFAULT NULL COMMENT '收货电话',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司收货地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退货申请';

/*Data for the table `oms_order_return_apply` */

/*Table structure for table `oms_order_return_reason` */

DROP TABLE IF EXISTS `oms_order_return_reason`;

CREATE TABLE `oms_order_return_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) DEFAULT NULL COMMENT '退货原因名',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `status` tinyint(1) DEFAULT NULL COMMENT '启用状态',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货原因';

/*Data for the table `oms_order_return_reason` */

/*Table structure for table `oms_order_setting` */

DROP TABLE IF EXISTS `oms_order_setting`;

CREATE TABLE `oms_order_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flash_order_overtime` int(11) DEFAULT NULL COMMENT '秒杀订单超时关闭时间(分)',
  `normal_order_overtime` int(11) DEFAULT NULL COMMENT '正常订单超时时间(分)',
  `confirm_overtime` int(11) DEFAULT NULL COMMENT '发货后自动确认收货时间（天）',
  `finish_overtime` int(11) DEFAULT NULL COMMENT '自动完成交易时间，不能申请退货（天）',
  `comment_overtime` int(11) DEFAULT NULL COMMENT '订单完成后自动好评时间（天）',
  `member_level` tinyint(2) DEFAULT NULL COMMENT '会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单配置信息';

/*Data for the table `oms_order_setting` */

/*Table structure for table `oms_payment_info` */

DROP TABLE IF EXISTS `oms_payment_info`;

CREATE TABLE `oms_payment_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_sn` char(64) DEFAULT NULL COMMENT '订单号（对外业务号）',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `alipay_trade_no` varchar(50) DEFAULT NULL COMMENT '支付宝交易流水号',
  `total_amount` decimal(18,4) DEFAULT NULL COMMENT '支付总金额',
  `subject` varchar(200) DEFAULT NULL COMMENT '交易内容',
  `payment_status` varchar(20) DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `callback_content` varchar(4000) DEFAULT NULL COMMENT '回调内容',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`),
  UNIQUE KEY `alipay_trade_no` (`alipay_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付信息表';

/*Data for the table `oms_payment_info` */

/*Table structure for table `oms_refund_info` */

DROP TABLE IF EXISTS `oms_refund_info`;

CREATE TABLE `oms_refund_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_return_id` bigint(20) DEFAULT NULL COMMENT '退款的订单',
  `refund` decimal(18,4) DEFAULT NULL COMMENT '退款金额',
  `refund_sn` varchar(64) DEFAULT NULL COMMENT '退款交易流水号',
  `refund_status` tinyint(1) DEFAULT NULL COMMENT '退款状态',
  `refund_channel` tinyint(4) DEFAULT NULL COMMENT '退款渠道[1-支付宝，2-微信，3-银联，4-汇款]',
  `refund_content` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款信息';

/*Data for the table `oms_refund_info` */

/*Table structure for table `undo_log` */

DROP TABLE IF EXISTS `undo_log`;

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `undo_log` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
