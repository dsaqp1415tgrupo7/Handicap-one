DROP DATABASE IF EXISTS handicapbd ;
CREATE DATABASE handicapbd;
USE handicapbd;

CREATE TABLE USERS (
iduser INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
rol varchar(40),
name varchar(40),
username varchar(40) UNIQUE,
email varchar(40),
password varchar(40)
)ENGINE=InnoDB;

CREATE TABLE PARTIDOS (
idpartido INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
/*equipos varchar(40),*/
local varchar(40),
visitante varchar(40),
fechacierre datetime,
fechapartido datetime
)ENGINE=InnoDB;

CREATE TABLE PICKS (
idpick INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
iduser INTEGER NOT NULL,
text varchar(1000),
resultado INTEGER,
seguidores INTEGER,
cuota INTEGER NOT NULL,
fechaedicion datetime,
FOREIGN KEY (iduser) REFERENCES USERS(iduser)
)ENGINE=InnoDB;

CREATE TABLE COMMENTS (
idcomment INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
iduser INTEGER NOT NULL,
text varchar(500),
fechaedicion datetime,
FOREIGN KEY (iduser) REFERENCES USERS(iduser)
)ENGINE=InnoDB;

CREATE TABLE REL_PICKCOMMENT (
idrelacion INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
idpick INTEGER NOT NULL,
idcomment INTEGER NOT NULL,
FOREIGN KEY (idpick) REFERENCES PICKS(idpick),
FOREIGN KEY (idcomment) REFERENCES COMMENTS(idcomment)
)ENGINE=InnoDB;

CREATE TABLE REL_PARTIDOPICK (
idrelacion INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
idpartido INTEGER NOT NULL,
idpick INTEGER NOT NULL,
FOREIGN KEY (idpartido) REFERENCES PARTIDOS(idpartido),
FOREIGN KEY (idpick) REFERENCES PICKS(idpick)
)ENGINE=InnoDB;