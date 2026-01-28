
--liquibase formatted sql

--changeset burgasvv:1
begin ;

insert into category(name, description) values ('Видеокарты','Описание категории Видеокарты');
insert into category(name, description) values ('Процессоры','Описание категории Процессоры');
insert into category(name, description) values ('Накопители','Описание категории Накопители');

commit ;