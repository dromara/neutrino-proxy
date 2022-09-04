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
CREATE UNIQUE INDEX IF NOT EXISTS I_login_name ON `user` (login_name ASC);

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
CREATE UNIQUE INDEX IF NOT EXISTS I_key ON `license` (`key` ASC);

#用户token表
CREATE TABLE IF NOT EXISTS `user_token` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `token` VARCHAR(50) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `expiration_time` INTEGER NOT NULL,
  `create_time` INTEGER NOT NULL,
  `update_time` INTEGER NOT NULL
);

#用户登录记录表
CREATE TABLE IF NOT EXISTS `user_login_record` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `user_id` INTEGER NOT NULL,
  `ip` VARCHAR(50) NOT NULL,
  `token` VARCHAR(100) NOT NULL,
  `type` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL
);

#端口池
CREATE TABLE IF NOT EXISTS `port_pool` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `port` INTEGER NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `update_time` INTEGER(20) NOT NULL,
  `create_time` INTEGER(20) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_port ON port_pool (port ASC);

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
CREATE UNIQUE INDEX IF NOT EXISTS I_server_port ON port_mapping (server_port ASC);

#客户端链接记录表
CREATE TABLE IF NOT EXISTS `client_connect_record` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `user_id` INTEGER NOT NULL,
  `ip` VARCHAR(50) NOT NULL,
  `license_id` INTEGER(20) NOT NULL,
  `license_key` VARCHAR(100) NOT NULL,
  `write_bytes` INTEGER(20),
  `read_bytes` INTEGER(20),
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
  `write_bytes` INTEGER(20),
  `read_bytes` INTEGER(20),
  `type` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL
);

#############################调度管理相关表#############################
#触发器信息表
CREATE TABLE IF NOT EXISTS `job_qrtz_trigger_info` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `job_cron` VARCHAR(128) NOT NULL ,
  `job_desc` VARCHAR(255) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL,
  `alarm_email` VARCHAR(255),
  `alarm_ding` VARCHAR(255),
  `executor_handler` VARCHAR(255) DEFAULT NULL,
  `executor_param` VARCHAR(512) DEFAULT NULL,
  `enable` INTEGER(2) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS I_executor_handler ON `job_qrtz_trigger_info` (`executor_handler` ASC);

#触发器日志表
CREATE TABLE IF NOT EXISTS `job_qrtz_trigger_log` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `job_id` INTEGER(20) NOT NULL,
  `executor_handler` VARCHAR(255) NOT NULL,
  `executor_param` VARCHAR(512) DEFAULT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `code` INTEGER(20) NOT NULL,
  `msg` TEXT,
  `alarm_status` INTEGER(2) NOT NULL DEFAULT '0'
);
CREATE INDEX IF NOT EXISTS I_create_time ON job_qrtz_trigger_log(create_time);
CREATE INDEX IF NOT EXISTS I_code ON job_qrtz_trigger_log(code);
