#安全组
CREATE TABLE IF NOT EXISTS `security_group` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(20) NOT NULL COMMENT '安全组名称',
    `description` varchar(255) COMMENT '安全组描述',
    `user_id` int NOT NULL COMMENT '用户ID',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `default_pass_type` varchar(20) NOT NULL COMMENT '默认放行类型',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

#安全组规则
CREATE TABLE IF NOT EXISTS `security_rule` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` int NOT NULL COMMENT '关联安全组',
    `name` varchar(20) NOT NULL COMMENT '规则名称',
    `description` varchar(255) NOT NULL COMMENT '规则描述',
    `rule` text NOT NULL COMMENT '规则内容',
    `pass_type` varchar(20) NOT NULL COMMENT '放行类型',
    `priority` int(1) NOT NULL COMMENT '优先级',
    `user_id` int NOT NULL COMMENT '用户ID',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_security_rule_group_id_priority` (`group_id`, `priority`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# port_mapping表增加字段
ALTER TABLE `port_mapping` ADD COLUMN `security_group_id` int DEFAULT 0 COMMENT '安全组Id';
