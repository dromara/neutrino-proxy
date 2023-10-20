#port_mapping
INSERT INTO port_mapping(`id`, `license_id`, `server_port`,  `protocal`, `subdomain`,`client_ip`, `client_port`, `is_online`, `enable`,`description`,`create_time`, `update_time`) VALUES
(1, 1, 9101, 'HTTP', 'test1', '127.0.0.1', 8080, 2, 1,'test1', now(), now());
INSERT INTO port_mapping(`id`, `license_id`, `server_port`,  `protocal`, `subdomain`, `client_ip`, `client_port`, `is_online`, `enable`,`description`, `create_time`, `update_time`) VALUES
(2, 1, 9102, 'TCP', '', '127.0.0.1', 3306, 2, 1,'test2', now(), now());
INSERT INTO port_mapping(`id`, `license_id`, `server_port`,  `protocal`, `subdomain`, `client_ip`, `client_port`, `is_online`, `enable`,`description`, `create_time`, `update_time`) VALUES
(3, 1, 9103, 'HTTP', 'test2', '127.0.0.1', 8081, 2, 1,'test3', now(), now());