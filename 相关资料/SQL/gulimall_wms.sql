/*
SQLyog Ultimate v12.08 (32 bit)
MySQL - 5.7.31 : Database - gulimall_wms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gulimall_wms` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `gulimall_wms`;

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

/*Table structure for table `wms_purchase` */

DROP TABLE IF EXISTS `wms_purchase`;

CREATE TABLE `wms_purchase` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购单id',
  `assignee_id` bigint(20) DEFAULT NULL COMMENT '采购人id',
  `assignee_name` varchar(255) DEFAULT NULL COMMENT '采购人名',
  `phone` char(13) DEFAULT NULL COMMENT '联系方式',
  `priority` int(4) DEFAULT NULL COMMENT '优先级',
  `status` int(4) DEFAULT NULL COMMENT '状态',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '总金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='采购信息';

/*Data for the table `wms_purchase` */

insert  into `wms_purchase`(`id`,`assignee_id`,`assignee_name`,`phone`,`priority`,`status`,`ware_id`,`amount`,`create_time`,`update_time`) values (1,2,'jack','13112716808',1,1,NULL,NULL,NULL,NULL),(2,1,'admin','13612345678',NULL,4,NULL,NULL,'2020-07-20 04:13:28','2020-07-20 06:47:03'),(3,1,'admin','13612345678',NULL,3,NULL,NULL,'2020-07-20 06:48:32','2020-07-20 07:06:42');

/*Table structure for table `wms_purchase_detail` */

DROP TABLE IF EXISTS `wms_purchase_detail`;

CREATE TABLE `wms_purchase_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `status` int(11) DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

/*Data for the table `wms_purchase_detail` */

insert  into `wms_purchase_detail`(`id`,`purchase_id`,`sku_id`,`sku_num`,`sku_price`,`ware_id`,`status`) values (1,2,1,10,NULL,3,3),(2,2,2,2,NULL,3,4),(3,3,3,10,NULL,3,3),(4,3,4,15,NULL,3,3),(5,3,5,15,NULL,3,3);

/*Table structure for table `wms_ware_info` */

DROP TABLE IF EXISTS `wms_ware_info`;

CREATE TABLE `wms_ware_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息';

/*Data for the table `wms_ware_info` */

insert  into `wms_ware_info`(`id`,`name`,`address`,`areacode`) values (1,'1','1','1'),(2,'2','2','2'),(3,'佛山仓库','佛山市','520001');

/*Table structure for table `wms_ware_order_task` */

DROP TABLE IF EXISTS `wms_ware_order_task`;

CREATE TABLE `wms_ware_order_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单';

/*Data for the table `wms_ware_order_task` */

insert  into `wms_ware_order_task`(`id`,`order_id`,`order_sn`,`consignee`,`consignee_tel`,`delivery_address`,`order_comment`,`payment_way`,`task_status`,`order_body`,`tracking_no`,`create_time`,`ware_id`,`task_comment`) values (20,NULL,'202008081200585831291947486172254210',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,NULL,'202008082302512291292114053082230785',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,NULL,'202008082304176951292114415746809858',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,NULL,'202008082305587861292114839749001218',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,NULL,'202008082310312921292115982726320129',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,NULL,'202008082315112841292117157097873409',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,NULL,'202008082316562251292117597252231170',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,NULL,'202008082322005731292118873780355074',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(28,NULL,'202008082325446451292119813606379522',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(29,NULL,'202008082331004081292121138012405762',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(30,NULL,'202008082331193711292121217544798210',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(31,NULL,'202008082335119241292122192947032066',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(32,NULL,'202008082337266481292122758012055553',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(33,NULL,'202008091041513491292289962695200770',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(34,NULL,'202008091046575061292291246810755074',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(35,NULL,'202008091116482511292298757743841281',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(36,NULL,'202008091233051911292317954813202433',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(37,NULL,'202008091301370701292325134958432257',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(38,NULL,'202008091303017061292325489943351298',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(39,NULL,'202008100909381811292629142772248578',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(40,NULL,'202008100910481731292629436335779842',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(41,NULL,'202008100912138331292629795619860482',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(43,NULL,'202008100915396981292630659080142850',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(44,NULL,'202008100918195031292631329350893570',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(45,NULL,'202008100919055271292631522393751553',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(46,NULL,'202008100923597711292632756546711554',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(47,NULL,'202008100925229821292633105554837505',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(48,NULL,'202008100927163591292633581096587265',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(49,NULL,'202008100929162621292634084001972226',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(50,NULL,'202008100930142771292634327334641666',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(51,NULL,'202008100931570501292634758387458049',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `wms_ware_order_task_detail` */

DROP TABLE IF EXISTS `wms_ware_order_task_detail`;

CREATE TABLE `wms_ware_order_task_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `lock_status` int(11) DEFAULT NULL COMMENT '1-已锁定 2-已解锁 3-扣减',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单';

/*Data for the table `wms_ware_order_task_detail` */

insert  into `wms_ware_order_task_detail`(`id`,`sku_id`,`sku_name`,`sku_num`,`task_id`,`ware_id`,`lock_status`) values (20,1,'',1,20,3,2),(21,1,'',1,21,3,2),(22,1,'',1,22,3,2),(23,1,'',1,23,3,2),(24,1,'',1,24,3,2),(25,1,'',1,25,3,2),(26,1,'',1,26,3,2),(27,1,'',1,27,3,2),(28,1,'',1,28,3,2),(29,1,'',1,29,3,2),(30,1,'',1,30,3,2),(31,1,'',1,31,3,2),(32,1,'',1,32,3,2),(33,1,'',1,33,3,2),(34,1,'',1,34,3,2),(35,1,'',1,35,3,2),(36,1,'',2,36,3,2),(37,1,'',2,37,3,2),(38,1,'',2,38,3,2),(39,1,'',2,39,3,2),(40,1,'',2,40,3,2),(41,1,'',1,41,3,2),(42,1,'',1,43,3,2),(43,1,'',1,44,3,2),(44,1,'',1,45,3,2),(45,1,'',1,46,3,2),(46,1,'',1,47,3,2),(47,1,'',1,48,3,2),(48,1,'',1,49,3,2),(49,1,'',1,50,3,2),(50,1,'',5,51,3,2);

/*Table structure for table `wms_ware_sku` */

DROP TABLE IF EXISTS `wms_ware_sku`;

CREATE TABLE `wms_ware_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) DEFAULT '0' COMMENT '锁定库存',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='商品库存';

/*Data for the table `wms_ware_sku` */

insert  into `wms_ware_sku`(`id`,`sku_id`,`ware_id`,`stock`,`sku_name`,`stock_locked`) values (1,1,3,70,'华为',0),(2,3,3,10,NULL,0),(3,4,3,15,NULL,0),(4,5,3,15,NULL,0),(5,6,3,0,'华为',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
