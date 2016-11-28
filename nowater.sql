-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`name`)
);

-- ----------------------------
-- Records of admin
-- ----------------------------

-- ----------------------------
-- Table structure for `comment_product`
-- ----------------------------
DROP TABLE IF EXISTS `comment_product`;
CREATE TABLE `comment_product` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_content` varchar(1024) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(1024) NOT NULL,
  PRIMARY KEY (`comment_id`)
);

-- ----------------------------
-- Records of comment_product
-- ----------------------------

-- ----------------------------
-- Table structure for `products`
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `class_id` int(11) NOT NULL,
  `product_name` varchar(1024) NOT NULL,
  `price` int(11) NOT NULL,
  `photo_id` varchar(1024) NOT NULL,
  `quantity_stock` int(11) NOT NULL,
  PRIMARY KEY (`product_id`)
);

-- ----------------------------
-- Records of products
-- ----------------------------

-- ----------------------------
-- Table structure for `size_product`
-- ----------------------------
DROP TABLE IF EXISTS `size_product`;
CREATE TABLE `size_product` (
  `choice_id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(1024) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`choice_id`)
);

-- ----------------------------
-- Records of size_product
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  `password` varchar(1024) NOT NULL,
  `telephone` varchar(1024) DEFAULT NULL,
  `address1` varchar(1024) DEFAULT NULL,
  `address2` varchar(1024) DEFAULT NULL,
  `address3` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);


-- ----------------------------
-- Table structure for `cart`
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `products_id` int(11) NOT NULL,
  `num` int(11) NOT NULL,
  `size_id` int(11) NOT NULL,
  PRIMARY KEY (`cart_id`)
);

-- ----------------------------
-- Table structure for `shop`
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `shop_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_name` varchar(1024) DEFAULT NULL,
  `owner_id` int(11) NOT NULL,
  `email` varchar(1024) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `telephone` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`shop_id`)
) ;

