#域名表
CREATE TABLE IF NOT EXISTS `domain_name` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `domain` varchar(50) NOT NULL COMMENT '域名',
    `user_id` int NOT NULL COMMENT '用户Id',
    `jks` BLOB DEFAULT NULL COMMENT 'jks文件',
    `key_store_password` varchar(255) DEFAULT NULL COMMENT 'jks密码',
    `is_default` int(2) NOT NULL COMMENT '是否开启默认域名（1、开启 2、关闭）',
    `force_https` int(2) NOT NULL COMMENT '是否启用强制https（1、开启 2、关闭）',
    `enable` int(2) NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `I_domain_name_domain` (`domain` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#域映射中间表
CREATE TABLE IF NOT EXISTS `domain_port_mapping` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '创建时间',
    `subdomain` varchar(50) NOT NULL COMMENT '子域名',
    `domain_name_id` int NOT NULL COMMENT '域名Id',
    `port_mapping_id` int NOT NULL COMMENT '端口映射Id',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `I_unique_domain_port_mapping` (`domain_name_id` ASC, `subdomain` ASC),
    INDEX `I_domain_port_mapping_domain_name_id` (`domain_name_id` ASC),
    INDEX `I_domain_port_mapping_port_mapping_id` (`port_mapping_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# port_mapping表删除字段
ALTER TABLE `port_mapping` DROP COLUMN `subdomain`;