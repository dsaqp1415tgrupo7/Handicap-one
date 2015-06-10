drop database if exists handicapbd;
create database handicapbd;
 
use handicapbd;

CREATE TABLE USERS (
username varchar(40) not null primary key,
rol varchar(40),
name varchar(40),
email varchar(40),
password varchar(40),
last_modified timestamp default current_timestamp on update current_timestamp,
creation_timestamp datetime NOT NULL default current_timestamp
)ENGINE=InnoDB;

CREATE TABLE PARTIDOS (
idpartido INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
username varchar(40),
local varchar(40),
visitante varchar(40),
fechacierre varchar(40),
fechapartido varchar(40),
last_modified timestamp default current_timestamp on update current_timestamp,
creation_timestamp datetime not null default current_timestamp,
validado INTEGER,
resultado INTEGER,
FOREIGN KEY (username) REFERENCES USERS(username) ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE PICKS (
idpick INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
username varchar(40),
text varchar(1000),
resultado INTEGER,
seguidores INTEGER,
cuota INTEGER NOT NULL,
fechaedicion datetime,
last_modified timestamp default current_timestamp on update current_timestamp,
creation_timestamp datetime not null default current_timestamp,
FOREIGN KEY (username) REFERENCES USERS(username) ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE COMMENTS (
idcomment INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
username varchar(40),
text varchar(500),
fechaedicion datetime,
last_modified timestamp default current_timestamp on update current_timestamp,
creation_timestamp datetime not null default current_timestamp,
FOREIGN KEY (username) REFERENCES USERS(username) ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE REL_PARTIDOPICK (
idrelacion INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
idpartido INTEGER NOT NULL,
idpick INTEGER NOT NULL,
FOREIGN KEY (idpartido) REFERENCES PARTIDOS(idpartido) ON DELETE CASCADE,
FOREIGN KEY (idpick) REFERENCES PICKS(idpick) ON DELETE CASCADE
)ENGINE=InnoDB;

CREATE TABLE REL_PICKCOMMENT (
idrelacion INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
idpick INTEGER NOT NULL,
idcomment INTEGER NOT NULL,
FOREIGN KEY (idpick) REFERENCES PICKS(idpick) ON DELETE CASCADE,
FOREIGN KEY (idcomment) REFERENCES COMMENTS(idcomment) ON DELETE CASCADE
)ENGINE=InnoDB;
