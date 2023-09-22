ALTER TABLE port_mapping ADD `proxy_responses` int NOT NULL DEFAULT 0 COMMENT '代理响应数据包数量';
ALTER TABLE port_mapping ADD `proxy_timeout_ms` int NOT NULL DEFAULT 0 COMMENT '代理超时毫秒数';
