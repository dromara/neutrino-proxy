ALTER TABLE port_mapping ADD `protocal` varchar(10) NOT NULL DEFAULT 'TCP' COMMENT '协议';
ALTER TABLE port_mapping ADD `subdomain` varchar(50) DEFAULT NULL COMMENT '子域名（仅HTTP时有效）';
ALTER TABLE port_mapping ADD `description` varchar(100) DEFAULT NULL COMMENT '描述';