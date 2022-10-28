#############################系统管理相关表#############################
#用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `login_name` VARCHAR(50) NOT NULL,
  `login_password` VARCHAR(255) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_user_login_name ON `user` (login_name ASC);

#用户token表
CREATE TABLE IF NOT EXISTS `user_token` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `token` VARCHAR(50) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `expiration_time` INTEGER NOT NULL,
  `create_time` INTEGER NOT NULL,
  `update_time` INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS I_user_token_user_id ON user_token(user_id);
CREATE INDEX IF NOT EXISTS I_user_token_token ON user_token(token);
CREATE INDEX IF NOT EXISTS I_user_token_expiration_time ON user_token(expiration_time);

#端口池
CREATE TABLE IF NOT EXISTS `port_pool` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `port` INTEGER NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `update_time` INTEGER(20) NOT NULL,
  `create_time` INTEGER(20) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_port_pool_port ON port_pool (port ASC);

#############################代理配置相关表#############################
#license表
CREATE TABLE IF NOT EXISTS `license` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `key` VARCHAR(100) NOT NULL,
    `user_id` INTEGER NOT NULL,
    `is_online` INTEGER(2) NOT NULL,
    `enable` INTEGER(2) NOT NULL,
    `create_time` INTEGER(20) NOT NULL,
    `update_time` INTEGER(20) NOT NULL
    );
CREATE UNIQUE INDEX IF NOT EXISTS I_license_key ON `license` (`key` ASC);

#端口映射表
CREATE TABLE IF NOT EXISTS `port_mapping` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `license_id` INTEGER(20) NOT NULL,
  `server_port` INTEGER NOT NULL,
  `client_ip` VARCHAR(20) NOT NULL,
  `client_port` INTEGER NOT NULL,
  `is_online` INTEGER(2) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_port_mapping_server_port ON port_mapping (server_port ASC);

#############################日志管理相关表#############################
#用户登录记录表
CREATE TABLE IF NOT EXISTS `user_login_record` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER NOT NULL,
    `ip` VARCHAR(50) NOT NULL,
    `token` VARCHAR(100) NOT NULL,
    `type` INTEGER(2) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
);
#客户端链接记录表
CREATE TABLE IF NOT EXISTS `client_connect_record` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER NOT NULL,
    `ip` VARCHAR(50) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `license_key` VARCHAR(100) NOT NULL,
    `write_bytes` INTEGER(20) DEFAULT NULL,
    `read_bytes` INTEGER(20) DEFAULT NULL,
    `type` INTEGER(2) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
);

#用户连接记录表
CREATE TABLE IF NOT EXISTS `user_connect_record` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `server_port` INTEGER NOT NULL,
    `user_ip` VARCHAR(50) NOT NULL,
    `client_ip` VARCHAR(50) NOT NULL,
    `client_lan_info` VARCHAR(50) NOT NULL,
    `user_id` INTEGER NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `license_key` VARCHAR(100) NOT NULL,
    `write_bytes` INTEGER(20) DEFAULT NULL,
    `read_bytes` INTEGER(20) DEFAULT NULL,
    `type` INTEGER(2) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
    );

#############################调度管理相关表#############################
#触发器信息表
CREATE TABLE IF NOT EXISTS `job_info` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `desc` VARCHAR(255) NOT NULL,
  `handler` VARCHAR(255) NOT NULL,
  `cron` VARCHAR(128) NOT NULL ,
  `param` VARCHAR(512) DEFAULT NULL,
  `alarm_email` VARCHAR(255) DEFAULT NULL,
  `alarm_ding` VARCHAR(255) DEFAULT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_job_info_handler ON `job_info` (`handler` ASC);

#触发器日志表
CREATE TABLE IF NOT EXISTS `job_log` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `job_id` INTEGER(20) NOT NULL,
  `handler` VARCHAR(255) NOT NULL,
  `param` VARCHAR(512) DEFAULT NULL,
  `code` INTEGER(20) NOT NULL,
  `msg` TEXT DEFAULT NULL,
  `alarm_status` INTEGER(2) NOT NULL DEFAULT '0',
  `create_time` INTEGER(20) NOT NULL
);
CREATE INDEX IF NOT EXISTS I_job_log_create_time ON job_log(create_time);
CREATE INDEX IF NOT EXISTS I_job_log_code ON job_log(code);

#############################报表管理相关表#############################
#流量统计报表-分钟(保留24小时)
CREATE TABLE IF NOT EXISTS `flow_report_minute` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` INTEGER(20) NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_create_time ON flow_report_minute(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_date ON flow_report_minute(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_user_id ON flow_report_minute(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_minute_license_id ON flow_report_minute(`license_id`);

#流量统计报表-小时(保留60天)
CREATE TABLE IF NOT EXISTS `flow_report_hour` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` INTEGER(20) NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
    );
CREATE INDEX IF NOT EXISTS I_flow_report_hour_create_time ON flow_report_hour(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_date ON flow_report_hour(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_user_id ON flow_report_hour(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_hour_license_id ON flow_report_hour(`license_id`);

#流量统计报表-天(保留1年)
CREATE TABLE IF NOT EXISTS `flow_report_day` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` INTEGER(20) NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
    );
CREATE INDEX IF NOT EXISTS I_flow_report_day_create_time ON flow_report_day(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_day_date ON flow_report_day(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_day_user_id ON flow_report_day(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_day_license_id ON flow_report_day(`license_id`);

#流量统计报表-月(全量保留)
CREATE TABLE IF NOT EXISTS `flow_report_month` (
    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `user_id` INTEGER(20) NOT NULL,
    `license_id` INTEGER(20) NOT NULL,
    `write_bytes` INTEGER(20) NOT NULL,
    `read_bytes` INTEGER(20) NOT NULL,
    `date` INTEGER(20) NOT NULL,
    `date_str` VARCHAR(20) NOT NULL,
    `create_time` INTEGER(20) NOT NULL
    );
CREATE INDEX IF NOT EXISTS I_flow_report_month_create_time ON flow_report_month(create_time);
CREATE INDEX IF NOT EXISTS I_flow_report_month_date ON flow_report_month(`date`);
CREATE INDEX IF NOT EXISTS I_flow_report_month_user_id ON flow_report_month(`user_id`);
CREATE INDEX IF NOT EXISTS I_flow_report_month_license_id ON flow_report_month(`license_id`);