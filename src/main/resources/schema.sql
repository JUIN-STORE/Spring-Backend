CREATE SCHEMA `shop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`account`;

CREATE TABLE IF NOT EXISTS `shop`.`account`
(
    `account_id`    BIGINT       NOT NULL AUTO_INCREMENT,
    `email`         VARCHAR(50)  NOT NULL UNIQUE,
    `password_hash` VARCHAR(120) NOT NULL,
    `first_name`    VARCHAR(50)  NOT NULL,
    `last_name`     VARCHAR(50)  NOT NULL,
    `birthday`      DATE        DEFAULT NULL,
    `gender`        ENUM ('MALE','FEMALE') DEFAULT NULL,
    `phone_number`  VARCHAR(15) DEFAULT NULL,
    `account_type`  ENUM ('USER', 'SELLER', 'ADMIN') NOT NULL,
    `registered_at` DATETIME     NOT NULL,
    `last_login`    DATETIME    DEFAULT NULL,

    PRIMARY KEY (`account_id`),
    UNIQUE INDEX `uq_phone` (`phone_number` ASC),
    UNIQUE INDEX `uq_email` (`email` ASC)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`product`;

CREATE TABLE `shop`.`product`
(
    `product_id`     BIGINT      NOT NULL AUTO_INCREMENT,
    `seller_id`      BIGINT      NOT NULL,
    `title`          VARCHAR(50) NOT NULL,
    `meta_title`     VARCHAR(50) NOT NULL,
    `slug`           VARCHAR(50)          DEFAULT NULL,
    `sku`            VARCHAR(50) NOT NULL,
    `price`          INT         NOT NULL DEFAULT 0,
    `discount_price` INT         NOT NULL DEFAULT 0,
    `quantity`       INT         NOT NULL DEFAULT 0,
    `sold_count`     INT         NOT NULL DEFAULT 0,
    `thumbnail_path` VARCHAR(200)         DEFAULT NULL,
    `image_path`     VARCHAR(200)         DEFAULT NULL,
    `content`        TEXT                 DEFAULT NULL,
    `created_at`     DATETIME    NOT NULL,
    `updated_at`     DATETIME             DEFAULT NULL,
    `published_at`   DATETIME             DEFAULT NULL,
    `starts_at`      DATETIME             DEFAULT NULL,
    `ends_at`        DATETIME             DEFAULT NULL,
    PRIMARY KEY (`product_id`),
    UNIQUE INDEX `uq_product_slug` (`slug` ASC),
    CONSTRAINT `fk_product_account`
        FOREIGN KEY (`seller_id`) REFERENCES `shop`.`account` (`account_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `shop`.`cart`
(
    `cart_id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `account_id`       BIGINT                DEFAULT NULL,
    `session_id`       VARCHAR(100) NOT NULL,
    `token`            VARCHAR(100) NOT NULL,
    `status`           ENUM ('READY','cart','CANCEL') NOT NULL,
    `item_price_total` INT          NOT NULL DEFAULT 0,
    `item_discount`    INT                   DEFAULT 0,
    `tax`              INT                   DEFAULT 0,
    `shipping`         INT                   DEFAULT 2500,
    `user_discount`    INT                   DEFAULT 0,
    `grand_total`      INT          NOT NULL DEFAULT 0,
    `road_address`     VARCHAR(50),
    `address`          VARCHAR(50),
    `city`             VARCHAR(50),
    `province`         VARCHAR(50),
    `country`          VARCHAR(50),
    `zip_code`         INT,
    `content`          TEXT                  DEFAULT NULL,
    `created_at`       DATETIME     NOT NULL,
    `updated_at`       DATETIME              DEFAULT NULL,

    PRIMARY KEY (`cart_id`),

    CONSTRAINT `fk_carts_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`cart_item`;

CREATE TABLE `shop`.`cart_item`
(
    `cart_item_id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `cart_id`        BIGINT      NOT NULL,
    `product_id`     BIGINT      NOT NULL,
    `sku`            VARCHAR(50) NOT NULL,
    `price`          INT         NOT NULL DEFAULT 0,
    `discount_price` INT         NOT NULL DEFAULT 0,
    `quantity`       INT         NOT NULL DEFAULT 0,
    `active`         INT         NOT NULL DEFAULT 0,
    `content`        TEXT                 DEFAULT NULL,
    `created_at`     DATETIME    NOT NULL,
    `updated_at`     DATETIME             DEFAULT NULL,


    PRIMARY KEY (`cart_item_id`),

    CONSTRAINT `fk_cart_item_cart`
        FOREIGN KEY (`cart_id`) REFERENCES `shop`.`cart` (`cart_id`),

    CONSTRAINT `fk_cart_item_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
CREATE TABLE `shop`.`orders`
(
    `orders_id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `account_id`       BIGINT                DEFAULT NULL,
    `session_id`       VARCHAR(100) NOT NULL,
    `token`            VARCHAR(100) NOT NULL,
    `status`           ENUM ('READY','ORDER','CANCEL') NOT NULL,
    `item_price_total` INT          NOT NULL DEFAULT 0,
    `item_discount`    INT          NOT NULL DEFAULT 0,
    `tax`              INT          NOT NULL DEFAULT 0,
    `shipping`         INT          NOT NULL DEFAULT 0,
    `user_discount`    INT          NOT NULL DEFAULT 0,
    `grand_total`      INT          NOT NULL DEFAULT 0,
    `first_name`       VARCHAR(45)  NOT NULL,
    `last_name`        VARCHAR(45)  NOT NULL,
    `email`            VARCHAR(50) NULL,
    `phone_number`     VARCHAR(25)  NOT NULL,
    `road_address`     VARCHAR(50)  NOT NULL,
    `address`          VARCHAR(50)  NOT NULL,
    `city`             VARCHAR(50)  NOT NULL,
    `province`         VARCHAR(50)  NOT NULL,
    `country`          VARCHAR(50)  NOT NULL,
    `zip_code`         INT          NOT NULL,
    `content`          TEXT                  DEFAULT NULL,
    `created_at`       DATETIME     NOT NULL,
    `updated_at`       DATETIME              DEFAULT NULL,

    PRIMARY KEY (`orders_id`),

    CONSTRAINT `fk_orders_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`order_item`;

CREATE TABLE `shop`.`order_item`
(
    `order_item_id`  BIGINT      NOT NULL AUTO_INCREMENT,
    `product_id`     BIGINT      NOT NULL,
    `orders_id`      BIGINT      NOT NULL,
    `sku`            VARCHAR(50) NOT NULL,
    `price`          INT         NOT NULL DEFAULT 0,
    `discount_price` INT         NOT NULL DEFAULT 0,
    `quantity`       INT         NOT NULL DEFAULT 0,
    `content`        TEXT                 DEFAULT NULL,
    `created_at`     DATETIME    NOT NULL,
    `updated_at`     DATETIME             DEFAULT NULL,

    PRIMARY KEY (`order_item_id`),

    CONSTRAINT `fk_order_item_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`),

    CONSTRAINT `fk_order_item_orders`
        FOREIGN KEY (`orders_id`) REFERENCES `shop`.`orders` (`orders_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`payment`;

CREATE TABLE `shop`.`payment`
(
    `payment_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `account_id` BIGINT       NOT NULL,
    `orders_id`  BIGINT       NOT NULL,
    `code`       VARCHAR(100) NOT NULL,
    `type`       ENUM ('CASH','CARD') NOT NULL,
    `status`     ENUM ('OK','FAIL') NOT NULL,
    `content`    TEXT     DEFAULT NULL,
    `created_at` DATETIME     NOT NULL,
    `updated_at` DATETIME DEFAULT NULL,


    PRIMARY KEY (`payment_id`),

    CONSTRAINT `fk_payment_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`),

    CONSTRAINT `fk_payment_orders`
        FOREIGN KEY (`orders_id`) REFERENCES `shop`.`orders` (`orders_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`review`;

CREATE TABLE `shop`.`review`
(
    `review_id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`     BIGINT NULL REFERENCES review (review_id),
    `order_item_id` BIGINT       NOT NULL,
    `account_id`    BIGINT       NOT NULL,
    `rate`          INT          NOT NULL DEFAULT 1,
    `title`         VARCHAR(100) NOT NULL,
    `content`       TEXT                  DEFAULT NULL,
    `created_at`    DATETIME     NOT NULL,

    PRIMARY KEY (`review_id`),

    CONSTRAINT `fk_review_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`),

    CONSTRAINT `fk_review_product`
        FOREIGN KEY (`order_item_id`) REFERENCES `shop`.`order_item` (`order_item_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`category`;

CREATE TABLE `shop`.`category`
(
    `category_id` BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`   BIGINT NULL REFERENCES category (category_id),
    `left_bound`  INTEGER      NOT NULL,
    `right_bound` INTEGER      NOT NULL,
    `title`       VARCHAR(100) NOT NULL,
    `meta_title`  VARCHAR(100) NOT NULL,
    `slug`        VARCHAR(100) DEFAULT NULL,
    `content`     TEXT         DEFAULT NULL,

    PRIMARY KEY (`category_id`),
    UNIQUE INDEX `uq_category_slug` (`slug` ASC)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`product_category`;

CREATE TABLE `shop`.`product_category`
(
    `product_id`  BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,

    PRIMARY KEY (`product_id`, `category_id`),

    CONSTRAINT `fk_pc_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`),

    CONSTRAINT `fk_pc_category`
        FOREIGN KEY (`category_id`) REFERENCES `shop`.`category` (`category_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`tag`;

CREATE TABLE `shop`.`tag`
(
    `tag_id`     BIGINT       NOT NULL AUTO_INCREMENT,
    `title`      VARCHAR(100) NOT NULL,
    `meta_title` VARCHAR(100) NOT NULL,
    `slug`       VARCHAR(100) NOT NULL,
    `content`    TEXT DEFAULT NULL,

    PRIMARY KEY (`tag_id`),
    UNIQUE INDEX `uq_tag_slug` (`slug` ASC)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`product_tag`;

CREATE TABLE `shop`.`product_tag`
(
    `product_id` BIGINT NOT NULL,
    `tag_id`     BIGINT NOT NULL,

    CONSTRAINT `fk_pt_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`),

    CONSTRAINT `fk_pt_tag`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`tag` (`tag_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`watch`;

CREATE TABLE `shop`.`watch`
(
    `watch_id`     BIGINT   NOT NULL AUTO_INCREMENT,
    `account_id`   BIGINT   NOT NULL,
    `product_id`   BIGINT   NOT NULL,
    `recent_watch` DATETIME NOT NULL,
    `watch_count`  INT DEFAULT 1,

    PRIMARY KEY (`watch_id`),

    CONSTRAINT `fk_watch_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`),

    CONSTRAINT `fk_watch_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `shop`.`address`
(
    `address_id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `account_id`   BIGINT NULL REFERENCES `account` (`account_id`),
    `address`      VARCHAR(100) DEFAULT NULL,
    `road_address` VARCHAR(100) NOT NULL,
    `city`         VARCHAR(50)  DEFAULT NULL,
    `province`     VARCHAR(50)  DEFAULT NULL,
    `country`      VARCHAR(50)  DEFAULT NULL,
    `zip_code`     INT          NOT NULL,

    PRIMARY KEY (`address_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`account_default_address`;

CREATE TABLE `shop`.`default_address`
(
    `account_id` BIGINT NOT NULL,
    `address_id` BIGINT NOT NULL,

    PRIMARY KEY (`account_id`, `address_id`),

    CONSTRAINT `fk_aa_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`),

    CONSTRAINT `fk_aa_default_address`
        FOREIGN KEY (`address_id`) REFERENCES `shop`.`address` (`address_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `shop`.`suggestion`;

CREATE TABLE `shop`.`suggestion`
(
    `suggestion_id` BIGINT NOT NULL AUTO_INCREMENT,
    `account_id`    BIGINT NOT NULL,
    `product_id`    BIGINT DEFAULT NULL,
    `content`       TEXT   DEFAULT NULL,

    PRIMARY KEY (`suggestion_id`),

    CONSTRAINT `fk_sg_account`
        FOREIGN KEY (`account_id`) REFERENCES `shop`.`account` (`account_id`),

    CONSTRAINT `fk_sg_product`
        FOREIGN KEY (`product_id`) REFERENCES `shop`.`product` (`product_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

