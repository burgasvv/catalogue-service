
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists product (
    id uuid default gen_random_uuid() unique not null ,
    name varchar unique not null ,
    category_id uuid references category(id) on delete set null on update cascade ,
    description text unique not null ,
    price decimal not null default 0 check ( price >= 0 )
)