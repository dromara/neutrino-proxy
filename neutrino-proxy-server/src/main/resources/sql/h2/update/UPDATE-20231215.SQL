#安全组
CREATE TABLE IF NOT EXISTS `security_group` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(20) NOT NULL,
    `description` VARCHAR(255),
    `user_id` INTEGER NOT NULL,
    `enable` INTEGER NOT NULL,
    `default_pass_type` INTEGER NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
);

#安全组规则
CREATE TABLE IF NOT EXISTS `security_rule` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `group_id` INTEGER NOT NULL,
    `name` VARCHAR(20) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `rule` text NOT NULL,
    `pass_type` INTEGER NOT NULL,
    `priority` INTEGER NOT NULL,
    `user_id` INTEGER NOT NULL,
    `enable` INTEGER NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE INDEX IF NOT EXISTS I_security_rule_group_id ON security_rule(group_id);

# port_mapping表增加字段
ALTER TABLE `port_mapping` ADD COLUMN `security_group_id` INTEGER DEFAULT 0;
ALTER TABLE `port_mapping` ADD COLUMN `up_limit_rate` varchar(20) DEFAULT NULL;
ALTER TABLE `port_mapping` ADD COLUMN `down_limit_rate` varchar(20) DEFAULT NULL;

# license表增加字段
ALTER TABLE `license` ADD COLUMN `up_limit_rate` varchar(20) DEFAULT NULL;
ALTER TABLE `license` ADD COLUMN `down_limit_rate` varchar(20) DEFAULT NULL;
