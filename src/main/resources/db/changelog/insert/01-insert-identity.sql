--liquibase formatted sql

--changeset burgasvv:1
begin;

insert into identity(authority, username, password, email, enabled, firstname, lastname, patronymic)
values ('ADMIN', 'burgasvv', '$2a$10$Dr.AVWsPPjiGJLtPK3gEEOOa6LZ4BqTxFZYQC/0t106YawWMgpT6S',
        'burgasvv@gmail.com', true,'Бургас', 'Вячеслав', 'Васильевич');

commit;