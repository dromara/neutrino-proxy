ALTER TABLE `flow_report_minute` MODIFY COLUMN `read_bytes` BIGINT NOT NULL COMMENT '读取流量';
ALTER TABLE `flow_report_minute` MODIFY COLUMN `write_bytes` BIGINT NOT NULL COMMENT '写入流量';
ALTER TABLE `flow_report_hour` MODIFY COLUMN `read_bytes` BIGINT NOT NULL COMMENT '读取流量';
ALTER TABLE `flow_report_hour` MODIFY COLUMN `write_bytes` BIGINT NOT NULL COMMENT '写入流量';
ALTER TABLE `flow_report_day` MODIFY COLUMN `read_bytes` BIGINT NOT NULL COMMENT '读取流量';
ALTER TABLE `flow_report_day` MODIFY COLUMN `write_bytes` BIGINT NOT NULL COMMENT '写入流量';
ALTER TABLE `flow_report_month` MODIFY COLUMN `read_bytes` BIGINT NOT NULL COMMENT '读取流量';
ALTER TABLE `flow_report_month` MODIFY COLUMN `write_bytes` BIGINT NOT NULL COMMENT '写入流量';
