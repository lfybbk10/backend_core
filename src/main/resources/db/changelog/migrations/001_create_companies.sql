--liquibase formatted sql
--changeset Daniil:BCORE-32-1

create table companies(
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100)
)