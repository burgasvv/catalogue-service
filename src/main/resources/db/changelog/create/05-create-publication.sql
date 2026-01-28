
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists publication (
    id uuid default gen_random_uuid() unique not null ,
    name varchar not null ,
    description text not null ,
    date date not null ,
    catalogue_id uuid references catalogue(id) on delete cascade on update cascade
)