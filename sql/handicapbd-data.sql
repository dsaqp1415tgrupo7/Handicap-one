

source handicapbd-schema.sql;

insert into USERS ( rol, username, email, password) values( "admin", "chiquirisi","raul.lorenzo.67@gmail.com","raul");
insert into USERS ( rol, username, email, password) values( "reg", "pepe","pepe.lorenzo.67@gmail.com","pepe");
insert into USERS ( rol, username, email, password) values( "reg", "paco","paco.lorenzo.67@gmail.com","paco");
insert into USERS ( rol, username, email, password) values( "guess", "pitu","pitu.lorenzo.67@gmail.com","pitu");

insert into PARTIDOS (idpartido, local, visitante, fechacierre, fechapartido) values(1, "Bulls", "Suns", '2014-12-01', '2014-12-02');
insert into PARTIDOS (idpartido, local, visitante, fechacierre, fechapartido) values(2, "Lakers", "Celtics", '2014-12-11', '2014-12-02');
insert into PARTIDOS (idpartido, local, visitante, fechacierre, fechapartido) values(3, "Cleveland", "Spurs", '2014-12-08', '2014-12-02');
insert into PARTIDOS (idpartido, local, visitante, fechacierre, fechapartido) values(4, "Golden", "Atlanta", '2014-12-07', '2014-12-02');
 
insert into PICKS (idpick, iduser, text, resultado, seguidores, cuota, fechaedicion) values(1, 1, "Para este partido veo una cómoda victoria del Barça", 1, 4, 2, '2014-12-02');
insert into PICKS (idpick, iduser, text, resultado, seguidores, cuota, fechaedicion) values(2, 1, "Para este partido veo una cómoda victoria del Sevilla", 0, 2, 3, '2014-12-12');
insert into PICKS (idpick, iduser, text, resultado, seguidores, cuota, fechaedicion) values(3, 2, "Para este partido veo una cómoda victoria del Malaga", 0, 1, 2, '2014-12-22');
insert into PICKS (idpick, iduser, text, resultado, seguidores, cuota, fechaedicion) values(4, 4, "Para este partido veo una cómoda victoria del Rayo", 1, 3, 4, '2014-12-20');

insert into COMMENTS (idcomment, iduser, text, fechaedicion) values(1, 2, "Lo veo bastante factible", '2014-12-04');
insert into COMMENTS (idcomment, iduser, text, fechaedicion) values(2, 3, "No lo tengo muy claro", '2014-12-14');
insert into COMMENTS (idcomment, iduser, text, fechaedicion) values(3, 1, "Te sigo", '2014-12-23');
insert into COMMENTS (idcomment, iduser, text, fechaedicion) values(4, 1, "Vamos con todo", '2014-12-21');

insert into REL_PICKCOMMENT (idrelacion, idpick, idcomment) values(1, 1, 1);
insert into REL_PICKCOMMENT (idrelacion, idpick, idcomment) values(2, 2, 2);
insert into REL_PICKCOMMENT (idrelacion, idpick, idcomment) values(3, 3, 3);
insert into REL_PICKCOMMENT (idrelacion, idpick, idcomment) values(4, 4, 4);

insert into REL_PARTIDOPICK (idrelacion, idpartido, idpick) values(1, 1, 1);
insert into REL_PARTIDOPICK (idrelacion, idpartido, idpick) values(2, 2, 2);
insert into REL_PARTIDOPICK (idrelacion, idpartido, idpick) values(3, 3, 3);
insert into REL_PARTIDOPICK (idrelacion, idpartido, idpick) values(4, 4, 4);