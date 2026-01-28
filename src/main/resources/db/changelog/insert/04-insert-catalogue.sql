
--liquibase formatted sql

--changeset burgasvv:1
begin ;

insert into catalogue(name, description) values ('Tech-Journal', 'Описание каталога Tech-Journal');
insert into catalogue(name, description) values ('Math In Tech', 'Описание каталога Math In Tech');
insert into catalogue(name, description) values ('Electro-Count', 'Описание каталога Electro-Count');

commit ;