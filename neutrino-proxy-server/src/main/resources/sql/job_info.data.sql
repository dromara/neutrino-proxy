#job_qrtz_trigger_info
INSERT INTO job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) VALUES
(1, '示例Job', 'DemoJob', '0/10 * * * * ?', '{"a":101}', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
INSERT INTO job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) VALUES
(2, '数据清理任务', 'DataCleanJob', '0 0 1 * * ?', '', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
INSERT INTO job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) VALUES
(3, '流量统计报表-分钟', 'FlowReportForMinuteJob', '0 */1 * * * ?', '', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
INSERT INTO job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) VALUES
(4, '流量统计报表-小时', 'FlowReportForHourJob', '0 0 */1 * * ?', '', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));