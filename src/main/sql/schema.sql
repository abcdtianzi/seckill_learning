-- 数据库初始化脚本
-- 常见数据库
CREATE database seckill;
-- 使用数据库
USE seckill;
-- 常见秒杀库存表
CREATE  TABLE  seckill(
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`name` VARCHAR (200) NOT NULL,
`number` INT NOT  NULL,
`start_time` TIMESTAMP NOT  NULL,
`end_time` TIMESTAMP NOT  NULL DEFAULT  CURRENT_TIMESTAMP,
`create_time` TIMESTAMP  NOT NULL DEFAULT  CURRENT_TIMESTAMP,
PRIMARY  KEY  (id),
KEY  idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀数据库';

-- 初始化数据
insert INTO seckill(name,number, start_time, end_time)
VALUES
('1000元秒杀ip6',100,'2016-11-01 00:00:00','2016-11-01 00:00:00'),
('1000元秒杀ip6',100,'2016-11-01 00:00:00','2016-11-01 00:00:00'),
('1000元秒杀ip6',100,'2016-11-01 00:00:00','2016-11-01 00:00:00'),
('1000元秒杀ip6',100,'2016-11-01 00:00:00','2016-11-01 00:00:00');

-- 秒杀成功明细表
CREATE TABLE success_killed(
`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
`user_phone` bigint NOT NULL,
`state` tinyint NOT  NULL DEFAULT -1 COMMENT '状态：-1-无效 0-成功 1 已付款 2 已发货',
`create_time` TIMESTAMP NOT NULL,
PRIMARY KEY (id,user_phone),/*联合主键*/
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 链接数据库控制台
mysql -uroot -p

