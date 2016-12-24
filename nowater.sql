DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`name`)
);

DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `shop_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_name` varchar(1024) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `email` varchar(1024) NOT NULL,
  `status` int(11) NOT NULL,
  `telephone` VARCHAR(1024) NOT NULL,
  `is_del` int(11) DEFAULT 0,
  PRIMARY KEY (`shop_id`)
);

DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `product_name` varchar(1024) NOT NULL,
  `price` double(15, 6) NOT NULL,
  `quantity_stock` int(11) NOT NULL,
  `is_del` int(11) DEFAULT 0,
  PRIMARY KEY (`product_id`)
);

DROP TABLE IF EXISTS `photo`;
CREATE TABLE  `photo` (
  `photo_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(1024) NOT NULL,
  `belong_id` int(11) NOT NULL,
  `url` varchar(1024) NOT NULL,
  `photo_type` int(11) NOT NULL,
  `is_del` int(11) DEFAULT 0,
  PRIMARY KEY (`photo_id`)
);

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_type` int(11) NOT NULL,
  `product_id` int(11) DEFAULT 0,

  `time` varchar(1024) NOT NULL,

  `initiator_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,

  `address` varchar(1024) DEFAULT NULL,
  `express` varchar(1024) DEFAULT NULL,
  `express_code` varchar(1024) DEFAULT NULL,

  `num` int(11) NOT NULL,
  `price` float(15, 6) NOT NULL,
  `sum_price` float(15, 6) NOT NULL,

  `payment_id` int(11) DEFAULT -1,
  `photo` varchar(1024) DEFAULT 0,
  `show_time` varchar(1024) DEFAULT NULL,

  `status` int(11) DEFAULT -3,
  PRIMARY KEY (`order_id`)
);

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT,
  `aliPay_account` varchar(1024) NOT NULL,
  `price` float(15, 6) NOT NULL,
  `time` varchar(1024) NOT NULL,
  `status` int(11) DEFAULT 0,
  PRIMARY KEY (`payment_id`)
);

DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `favorite_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `favorite_type` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`favorite_id`)
);

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `num` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `is_del` int(11) DEFAULT 0,
  PRIMARY KEY (`cart_id`)
);

DROP TABLE IF EXISTS `comment_product`;
CREATE TABLE `comment_product` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_content` varchar(1024) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(1024) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`comment_id`)
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `telephone` varchar(1024) DEFAULT NULL,
  `address1` varchar(1024) DEFAULT NULL,
  `address2` varchar(1024) DEFAULT NULL,
  `address3` varchar(1024) DEFAULT NULL,
  `post_code` varchar(1024) DEFAULT NULL,
  `first_name` varchar(1024) DEFAULT NULL,
  `last_name` varchar(1024) DEFAULT NULL,
  `status` int(11) DEFAULT 1,
  PRIMARY KEY (`user_id`)
);

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(1024) NOT NULL,
  `user_id` int(11) NOT NULL,
  `default` int(11) DEFAULT 0,
  PRIMARY KEY (`address_id`)
);
