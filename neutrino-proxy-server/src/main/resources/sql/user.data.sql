#用户表
insert into `user`(`id`, `name`,`login_name`,`login_password`,`enable`,`create_time`, `update_time`) values
(1, '管理员', 'admin', '6613b92b77056faeb72068f184ed4c4f', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
insert into `user`(`id`, `name`,`login_name`,`login_password`,`enable`,`create_time`, `update_time`) values
(2, '游客', 'visitor', 'e10adc3949ba59abbe56e057f20f883e', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
