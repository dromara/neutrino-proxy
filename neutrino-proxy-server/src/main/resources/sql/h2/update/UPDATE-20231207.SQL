#安全组
CREATE TABLE IF NOT EXISTS `security_group` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(20) NOT NULL,
    `description` VARCHAR(255),
    `user_id` INTEGER NOT NULL,
    `enable` VARCHAR(20) NOT NULL,
    `default_pass_type` VARCHAR(20) NOT NULL,
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
    `pass_type` VARCHAR(20) NOT NULL,
    `priority` INTEGER NOT NULL,
    `user_id` INTEGER NOT NULL,
    `enable` VARCHAR(20) NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE INDEX IF NOT EXISTS I_security_rule_group_id ON security_rule(group_id);
ALTER TABLE `port_mapping` ADD COLUMN IF NOT EXISTS `security_group_id` INTEGER DEFAULT 0;
