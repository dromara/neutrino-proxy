#############################系统管理相关表#############################
#用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(50) NOT NULL COMMENT '用户名',
    `login_name` varchar(50) NOT NULL COMMENT '登录名',
    `login_password` varchar(255) NOT NULL COMMENT '登录密码',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_user_login_name` (`login_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#用户token表
CREATE TABLE IF NOT EXISTS `user_token` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `token` varchar(50) NOT NULL COMMENT 'token',
    `user_id` int NOT NULL COMMENT '用户ID',
    `expiration_time` datetime(3) NOT NULL COMMENT '过期时间',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_user_token_user_id` (`user_id`),
    KEY `I_user_token_token` (`token`),
    KEY `I_user_token_expiration_time` (`expiration_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#端口池
CREATE TABLE IF NOT EXISTS `port_pool` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` int NOT NULL DEFAULT 1 COMMENT '分组ID',
    `port` int NOT NULL COMMENT '端口',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_port_pool_port` (`port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

#############################代理配置相关表#############################
#license表
CREATE TABLE IF NOT EXISTS `license` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(50) NOT NULL COMMENT 'license名称',
    `key` varchar(100) NOT NULL COMMENT 'license key',
    `user_id` int NOT NULL COMMENT '用户ID',
    `is_online` int NOT NULL COMMENT '是否在线（1、在线 2、离线）',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `I_license_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#端口映射表
CREATE TABLE IF NOT EXISTS `port_mapping` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `license_id` int NOT NULL COMMENT 'licenseID',
    `server_port` int NOT NULL COMMENT '服务端端口',
    `client_ip` varchar(20) NOT NULL COMMENT '客户端IP',
    `client_port` int NOT NULL COMMENT '客户端端口',
    `is_online` int NOT NULL COMMENT '是否在线（1、在线 2、离线）',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_port_mapping_server_port` (`server_port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
#############################日志管理相关表#############################
#用户登录记录表
CREATE TABLE IF NOT EXISTS `user_login_record` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `ip` varchar(50) NOT NULL COMMENT 'IP',
    `token` varchar(100) NOT NULL COMMENT 'token',
    `type` int NOT NULL COMMENT '类型（1、登录 2、登出）',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
#客户端连接记录表
CREATE TABLE IF NOT EXISTS `client_connect_record` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP',
    `license_id` int NOT NULL COMMENT 'licenseId',
    `type` int NOT NULL COMMENT '类型（1、连接 2、断开连接）',
    `msg` varchar(512) DEFAULT NULL COMMENT '消息',
    `code` int NOT NULL COMMENT '结果 （1、成功 2、失败）',
    `err` text DEFAULT NULL COMMENT '异常信息',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
#############################调度管理相关表#############################
#触发器信息表
CREATE TABLE IF NOT EXISTS `job_info` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `desc` varchar(255) NOT NULL COMMENT '描述',
    `handler` varchar(255) NOT NULL COMMENT '处理器',
    `cron` varchar(128) NOT NULL COMMENT 'cron',
    `param` varchar(512) DEFAULT NULL COMMENT '参数',
    `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮箱',
    `alarm_ding` varchar(255) DEFAULT NULL COMMENT '报警钉钉配置',
    `enable` int NOT NULL COMMENT '是否启用(1、启用 2、禁用)',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    `update_time` datetime(3) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `I_job_info_handler` (`handler`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#触发器日志表
CREATE TABLE IF NOT EXISTS `job_log` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `job_id` int NOT NULL COMMENT 'JobId',
    `handler` varchar(255) NOT NULL COMMENT '处理器',
    `param` varchar(512) DEFAULT NULL COMMENT '参数',
    `code` int NOT NULL COMMENT '结果（1、成功 2、失败）',
    `msg` text COMMENT '消息',
    `alarm_status` int NOT NULL COMMENT '报警状态（1、未报警 2、已报警）',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `I_job_log_create_time` (`create_time`) USING BTREE,
    KEY `I_job_log_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#############################报表管理相关表#############################
#流量统计报表-分钟(保留24小时)
CREATE TABLE IF NOT EXISTS `flow_report_minute` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `license_id` int NOT NULL COMMENT 'licenseId',
    `write_bytes` int NOT NULL COMMENT '写入流量',
    `read_bytes` int NOT NULL COMMENT '读取流量',
    `date` datetime(3) NOT NULL COMMENT '时间',
    `date_str` varchar(20) NOT NULL COMMENT '时间 yyyy-MM-dd HH:mm',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `I_flow_report_minute_create_time` (`create_time`) USING BTREE,
    KEY `I_flow_report_minute_date` (`date`) USING BTREE,
    KEY `I_flow_report_minute_user_id` (`user_id`),
    KEY `I_flow_report_minute_license_id` (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#流量统计报表-小时(保留60天)
CREATE TABLE IF NOT EXISTS `flow_report_hour` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `license_id` int NOT NULL COMMENT 'licenseId',
    `write_bytes` int NOT NULL COMMENT '写入流量',
    `read_bytes` int NOT NULL COMMENT '读取流量',
    `date` datetime(3) NOT NULL COMMENT '时间',
    `date_str` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间 yyyy-MM-dd HH',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `I_flow_report_hour_create_time` (`create_time`) USING BTREE,
    KEY `I_flow_report_hour_date` (`date`) USING BTREE,
    KEY `I_flow_report_hour_user_id` (`user_id`),
    KEY `I_flow_report_hour_license_id` (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#流量统计报表-天(保留1年)
CREATE TABLE IF NOT EXISTS `flow_report_day` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `license_id` int NOT NULL COMMENT 'licenseId',
    `write_bytes` int NOT NULL COMMENT '写入流量',
    `read_bytes` int NOT NULL COMMENT '读取流量',
    `date` datetime(3) NOT NULL COMMENT '时间',
    `date_str` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间 yyyy-MM-dd',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `I_flow_report_day_create_time` (`create_time`) USING BTREE,
    KEY `I_flow_report_day_date` (`date`) USING BTREE,
    KEY `I_flow_report_day_user_id` (`user_id`),
    KEY `I_flow_report_day_license_id` (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#流量统计报表-月(全量保留)
CREATE TABLE IF NOT EXISTS `flow_report_month` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `license_id` int NOT NULL COMMENT 'licenseId',
    `write_bytes` int NOT NULL COMMENT '写入流量',
    `read_bytes` int NOT NULL COMMENT '读取流量',
    `date` datetime(3) NOT NULL COMMENT '时间',
    `date_str` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间 yyyy-MM',
    `create_time` datetime(3) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `I_flow_report_month_create_time` (`create_time`) USING BTREE,
    KEY `I_flow_report_month_date` (`date`) USING BTREE,
    KEY `I_flow_report_month_user_id` (`user_id`),
    KEY `I_flow_report_month_license_id` (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;