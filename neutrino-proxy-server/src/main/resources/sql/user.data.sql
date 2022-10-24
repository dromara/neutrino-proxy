#用户表 6613b92b77056faeb72068f184ed4c4f
INSERT INTO `user`(`id`, `name`,`login_name`,`login_password`,`enable`,`create_time`, `update_time`) VALUES
(1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
INSERT INTO `user`(`id`, `name`,`login_name`,`login_password`,`enable`,`create_time`, `update_time`) VALUES
(2, '游客', 'visitor', 'e10adc3949ba59abbe56e057f20f883e', 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
