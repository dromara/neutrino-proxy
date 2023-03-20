#port_group
INSERT INTO `port_group`(`id`,`name`,`possessor_type`,`possessor_id`,`enable`,`create_time`,`update_time`) VALUES
(1, '全局(默认)', 0, -1, 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
