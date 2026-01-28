
--liquibase formatted sql

--changeset burgasvv:1
create table if not exists publication_product (
    publication_id uuid references publication(id) on delete cascade on update cascade ,
    product_id uuid references product(id) on delete cascade on update cascade ,
    primary key (publication_id, product_id)
)