ALTER TABLE port_mapping ADD `protocal` varchar(10) NOT NULL DEFAULT 'TCP';
ALTER TABLE port_mapping ADD `subdomain` varchar(50) DEFAULT NULL;