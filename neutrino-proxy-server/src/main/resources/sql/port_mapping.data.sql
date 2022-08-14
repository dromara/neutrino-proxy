#port_mapping
insert into port_mapping(`id`, `license_id`, `server_port`, `client_ip`, `client_port`, `is_online`, `enable`, `create_time`, `update_time`) values
(1, 1, 9101, '127.0.0.1', 8080, 2, 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
insert into port_mapping(`id`, `license_id`, `server_port`, `client_ip`, `client_port`, `is_online`, `enable`, `create_time`, `update_time`) values
(2, 1, 9102, '127.0.0.1', 3306, 2, 1, STRFTIME('%s000', 'NOW'), STRFTIME('%s000', 'NOW'));
