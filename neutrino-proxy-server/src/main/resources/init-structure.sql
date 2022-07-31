#用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` varchar(50) NOT NULL,
  `login_name` varchar(50) NOT NULL,
  `login_password` varchar(255) NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);

#license表
CREATE TABLE IF NOT EXISTS `license` (
  `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `name` varchar(50) NOT NULL,
  `key` varchar(100) NOT NULL,
  `user_id` INTEGER NOT NULL,
  `create_time` INTEGER(20) NOT NULL,
  `update_time` INTEGER(20) NOT NULL
);
