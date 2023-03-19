ALTER TABLE port_pool ADD group_id INT NOT NULL DEFAULT 1 COMMENT "分组ID";

#端口分组
CREATE TABLE IF NOT EXISTS `port_group` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(255) NOT NULL COMMENT '分组名称',
    `possessor_type` int NOT NULL DEFAULT '0' COMMENT '所有者类型 (0、全局共享 1、用户所有 2License所有) ',
    `possessor_id` int NOT NULL DEFAULT '-1' COMMENT '所有者id(当type为0时 固定为-1、当type为1时为用户id 、当type为2时为licenseid)',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='端口分组';