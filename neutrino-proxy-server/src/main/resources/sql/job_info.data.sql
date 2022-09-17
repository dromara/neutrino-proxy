#job_qrtz_trigger_info
insert into job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) values
(1, '示例Job', 'DemoJob', '0/10 * * * * ?', '{"a":101}', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
insert into job_info(`id`, `desc`, `handler`, `cron`, `param`, `enable`, `create_time`, `update_time`) values
(2, '数据清理任务', 'DataCleanJob', '0 0 1 * * ?', '', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));