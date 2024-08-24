#域名表
CREATE TABLE IF NOT EXISTS `domain_name` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `domain` VARCHAR(50) NOT NULL,
    `user_id` INTEGER NOT NULL,
    `jks` BLOB DEFAULT NULL,
    `key_store_password` varchar(255) DEFAULT NULL,
    `is_default` INTEGER(2) NOT NULL,
    `force_https` INTEGER(2) NOT NULL,
    `enable` INTEGER(2) NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL
    );
CREATE UNIQUE INDEX IF NOT EXISTS I_domain_name_domain ON domain_name (`domain` ASC);

#域映射中间表
CREATE TABLE IF NOT EXISTS `domain_port_mapping` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `subdomain` VARCHAR(50) NOT NULL,
    `domain_name_id` INTEGER NOT NULL,
    `port_mapping_id` INTEGER NOT NULL
    );
CREATE UNIQUE INDEX IF NOT EXISTS I_unique_domain_port_mapping ON domain_port_mapping (`domain_name_id` ASC, `subdomain` ASC);
CREATE INDEX IF NOT EXISTS I_domain_port_mapping_domain_name_id ON domain_port_mapping (`domain_name_id` ASC);
CREATE INDEX IF NOT EXISTS I_domain_port_mapping_port_mapping_id ON domain_port_mapping (`port_mapping_id` ASC);

# port_mapping表删除字段
ALTER TABLE `port_mapping` DROP COLUMN `subdomain`;
