ALTER TABLE port_pool ADD group_id INTEGER NOT NULL DEFAULT 1;

#端口分组
CREATE TABLE IF NOT EXISTS `port_group` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `possessor_type` INTEGER NOT NULL DEFAULT '0',
    `possessor_id` INTEGER NOT NULL DEFAULT '-1',
    `enable` INTEGER NOT NULL,
    `create_time` datetime(3) NOT NULL,
    `update_time` datetime(3) NOT NULL
);