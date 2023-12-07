#############################系统管理相关表#############################
#用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `login_name` VARCHAR(50) NOT NULL,
  `login_password` VARCHAR(255) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_user_login_name ON `user` (login_name ASC);

#用户token表
CREATE TABLE IF NOT EXISTS `user_token` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `token` VARCHAR(50) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `expiration_time` TIMESTAMP NOT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_user_token_user_id ON user_token(user_id);
CREATE INDEX IF NOT EXISTS I_user_token_token ON user_token(token);
CREATE INDEX IF NOT EXISTS I_user_token_expiration_time ON user_token(expiration_time);

#端口池
CREATE TABLE IF NOT EXISTS `port_pool` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `group_id` INTEGER NOT NULL DEFAULT 1,
  `port` INTEGER NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `update_time` TIMESTAMP NOT NULL,
  `create_time` TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_port_pool_port ON port_pool (port ASC);

#端口分组
CREATE TABLE IF NOT EXISTS `port_group` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `possessor_type` INTEGER NOT NULL DEFAULT '0',
    `possessor_id` INTEGER NOT NULL DEFAULT '-1',
    `enable` INTEGER NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL
);

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

#############################代理配置相关表#############################
#license表
CREATE TABLE IF NOT EXISTS `license` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `key` VARCHAR(100) NOT NULL,
    `user_id` INTEGER NOT NULL,
    `is_online` INTEGER(2) NOT NULL,
    `enable` INTEGER(2) NOT NULL,
    `create_time` TIMESTAMP NOT NULL,
    `update_time` TIMESTAMP NOT NULL
    );
CREATE UNIQUE INDEX IF NOT EXISTS I_license_key ON `license` (`key` ASC);

#端口映射表
CREATE TABLE IF NOT EXISTS `port_mapping` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `license_id` INTEGER(20) NOT NULL,
  `protocal` VARCHAR(10) NOT NULL DEFAULT 'TCP',
  `subdomain` VARCHAR(50) DEFAULT NULL,
  `server_port` INTEGER NOT NULL,
  `client_ip` VARCHAR(20) NOT NULL,
  `client_port` INTEGER NOT NULL,
  `is_online` INTEGER(2) NOT NULL,
  `description` VARCHAR(100) DEFAULT NULL,
  `proxy_responses` INTEGER(20) NOT NULL DEFAULT 0,
  `proxy_timeout_ms` INTEGER(20) NOT NULL DEFAULT 0,
  `enable` INTEGER(2) NOT NULL,
  `security_group_id` INTEGER(20) NOT NULL DEFAULT 0,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_port_mapping_server_port ON port_mapping (server_port ASC);

#############################日志管理相关表#############################
#用户登录记录表
CREATE TABLE IF NOT EXISTS `user_login_record` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` INTEGER NOT NULL,
    `ip` VARCHAR(50) NOT NULL,
    `token` VARCHAR(100) NOT NULL,
    `type` INTEGER(2) NOT NULL,
    `create_time` TIMESTAMP NOT NULL
);
#客户端连接记录表
CREATE TABLE IF NOT EXISTS `client_connect_record` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `ip` VARCHAR(50) NOT NULL,
    `license_id` INTEGER(20) DEFAULT NULL,
    `type` INTEGER(2) NOT NULL,
    `msg` VARCHAR(512) DEFAULT NULL,
    `code` INTEGER(2) NOT NULL,
    `err` VARCHAR(512) DEFAULT NULL,
    `create_time` TIMESTAMP NOT NULL
);
#############################调度管理相关表#############################
#触发器信息表
CREATE TABLE IF NOT EXISTS `job_info` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `desc` VARCHAR(255) NOT NULL,
  `handler` VARCHAR(255) NOT NULL,
  `cron` VARCHAR(128) NOT NULL ,
  `param` VARCHAR(512) DEFAULT NULL,
  `alarm_email` VARCHAR(255) DEFAULT NULL,
  `alarm_ding` VARCHAR(255) DEFAULT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_job_info_handler ON `job_info` (`handler` ASC);

#触发器日志表
CREATE TABLE IF NOT EXISTS `job_log` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `job_id` INTEGER(20) NOT NULL,
  `handler` VARCHAR(255) NOT NULL,
  `param` VARCHAR(512) DEFAULT NULL,
  `code` INTEGER(20) NOT NULL,
  `msg` TEXT DEFAULT NULL,
  `alarm_status` INTEGER(2) NOT NULL DEFAULT '0',
  `create_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_job_log_create_time ON job_log(create_time);
CREATE INDEX IF NOT EXISTS I_job_log_code ON job_log(code);

#############################报表管理相关表#############################
#流量统计报表-分钟(保留24小时)
CREATE TABLE IF NOT EXISTS `flow_report_minute` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` TIMESTAMP NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_create_time ON flow_report_minute(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_date ON flow_report_minute(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_user_id ON flow_report_minute(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_license_id ON flow_report_minute(`license_id`);

#流量统计报表-小时(保留60天)
CREATE TABLE IF NOT EXISTS `flow_report_hour` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` TIMESTAMP NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_create_time ON flow_report_hour(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_date ON flow_report_hour(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_user_id ON flow_report_hour(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_license_id ON flow_report_hour(`license_id`);

#流量统计报表-天(保留1年)
CREATE TABLE IF NOT EXISTS `flow_report_day` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` TIMESTAMP NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_flow_report_day_create_time ON flow_report_day(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_day_date ON flow_report_day(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_day_user_id ON flow_report_day(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_day_license_id ON flow_report_day(`license_id`);

#流量统计报表-月(全量保留)
CREATE TABLE IF NOT EXISTS `flow_report_month` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` TIMESTAMP NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS I_flow_report_month_create_time ON flow_report_month(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_month_date ON flow_report_month(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_month_user_id ON flow_report_month(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_month_license_id ON flow_report_month(`license_id`);
