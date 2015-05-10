

source handicapbd-schema.sql;



insert into USERS (rol, name, username, email, password) values("admin", "Raul", "chiquirisi","raul.lorenzo.67@gmail.com",MD5('Raul'));
insert into USERS (rol, name, username, email, password) values("reg", "Pepe",  "pepe","pepe.lorenzo.67@gmail.com",MD5('pepe'));
insert into USERS (rol, name, username, email, password) values("reg", "Paco",  "paco","paco.lorenzo.67@gmail.com",MD5('paco'));
insert into USERS (rol, name, username, email, password) values("reg", "Pitu",  "pitu","pitu.lorenzo.67@gmail.com",MD5('pitu'));
insert into USERS (rol, name, username, email, password) values("reg", "Mire",  "mire","Mire.lorenzo.67@gmail.com",MD5('mire'));
insert into USERS (rol, name, username, email, password) values("reg", "Ivan",  "ivan","Ivan.lorenzo.67@gmail.com",MD5('ivan'));
insert into USERS (rol, name, username, email, password) values("reg", "Javi",  "javi","Javi.lorenzo.67@gmail.com",MD5('javi'));
insert into USERS (rol, name, username, email, password) values("reg", "Juto",  "juto","Juto.lorenzo.67@gmail.com",MD5('juto'));
insert into USERS (rol, name, username, email, password) values("reg", "Oscar", "oscar","Oscar.lorenzo.67@gmail.com",MD5('oscar'));
insert into USERS (rol, name, username, email, password) values("reg", "Tomas", "tomas","Tomas.lorenzo.67@gmail.com",MD5('tomas'));
insert into USERS (rol, name, username, email, password) values("reg", "Encarna", "encarna","Encarna.lorenzo.67@gmail.com",MD5('encarna'));
insert into USERS (rol, name, username, email, password) values("reg", "Marta", "marta","Marta.lorenzo.67@gmail.com",MD5('marta'));

select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("chiquirisi", "Bulls", "Suns", '2014-12-01', '2014-12-02');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("chiquirisi", "Lakers", "Celtics", '2014-12-11', '2014-12-02');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("chiquirisi", "Cleveland", "Spurs", '2014-12-08', '2014-12-02');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("Pepe", "Golden", "Atlanta", '2014-12-07', '2014-12-02');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("ivan", "Nets", "Knicks", '2015-01-01', '2015-01-02');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("ivan", "Grizzlis", "Miami", '2015-01-02', '2015-01-03');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("chiquirisi", "Utah", "Pelicans", '2015-01-02', '2015-01-03');
select sleep(2);insert into PARTIDOS (username, local, visitante, fechacierre, fechapartido) values("ivan", "Protland", "Clippers", '2015-01-03', '2015-01-04');

 
select sleep(2);insert into PICKS (iduser, text, resultado, seguidores, cuota, fechaedicion) values(1, "Para este partido veo una cómoda victoria del Barça", 1, 4, 2, '2014-12-02');
select sleep(2);insert into PICKS (iduser, text, resultado, seguidores, cuota, fechaedicion) values(1, "Para este partido veo una cómoda victoria del Sevilla", 0, 2, 3, '2014-12-12');
select sleep(2);insert into PICKS (iduser, text, resultado, seguidores, cuota, fechaedicion) values(2, "Para este partido veo una cómoda victoria del Malaga", 0, 1, 2, '2014-12-22');
select sleep(2);insert into PICKS (iduser, text, resultado, seguidores, cuota, fechaedicion) values(4, "Para este partido veo una cómoda victoria del Rayo", 1, 3, 4, '2014-12-20');

select sleep(2);insert into COMMENTS (iduser, text, fechaedicion) values(2, "Lo veo bastante factible", '2014-12-04');
select sleep(2);insert into COMMENTS (iduser, text, fechaedicion) values(3, "No lo tengo muy claro", '2014-12-14');
select sleep(2);insert into COMMENTS (iduser, text, fechaedicion) values(1, "Te sigo", '2014-12-23');
select sleep(2);insert into COMMENTS (iduser, text, fechaedicion) values(1, "Vamos con todo", '2014-12-21');

insert into REL_PICKCOMMENT (idpick, idcomment) values(1, 1);
insert into REL_PICKCOMMENT (idpick, idcomment) values(2, 2);
insert into REL_PICKCOMMENT (idpick, idcomment) values(3, 3);
insert into REL_PICKCOMMENT (idpick, idcomment) values(4, 4);

insert into REL_PARTIDOPICK (idpartido, idpick) values(1, 1);
insert into REL_PARTIDOPICK (idpartido, idpick) values(2, 2);
insert into REL_PARTIDOPICK (idpartido, idpick) values(3, 3);
insert into REL_PARTIDOPICK (idpartido, idpick) values(4, 4);

