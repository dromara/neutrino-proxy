#用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` varchar(50) NOT NULL,
  `login_name` varchar(50) NOT NULL,
  `login_password` varchar(255) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);

#license表
CREATE TABLE IF NOT EXISTS `license` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` varchar(50) NOT NULL,
  `key` varchar(100) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `is_online` INTEGER(2) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);

#用户token表
CREATE TABLE IF NOT EXISTS `user_token` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `token` varchar(50) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `expiration_time` INTEGER NOT NULL,
  `create_time` INTEGER NOT NULL,
  `update_time` INTEGER NOT NULL
);

#用户登录记录表
CREATE TABLE IF NOT EXISTS `user_login_record` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `user_id` INTEGER NOT NULL,
  `ip` varchar(50) NOT NULL,
  `token` varchar(100) NOT NULL,
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

#端口映射表
CREATE TABLE IF NOT EXISTS `port_mapping` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `license_id` INTEGER(20) NOT NULL,
  `server_port` INTEGER NOT NULL,
  `client_ip` varchar(20) NOT NULL,
  `client_port` INTEGER NOT NULL,
  `is_online` INTEGER(2) NOT NULL,
  `enable` INTEGER(2) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);
