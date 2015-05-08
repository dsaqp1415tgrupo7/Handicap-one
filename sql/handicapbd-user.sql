drop user 'handicap'@'localhost';
create user 'handicap'@'localhost' identified by 'handicap';
grant all privileges on handicapbd.* to 'handicap'@'localhost';
flush privileges;