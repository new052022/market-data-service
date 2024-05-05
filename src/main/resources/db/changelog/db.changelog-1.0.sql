--liquibase formatted sql

--changeset a.khaustov:1
create table user_info
(
    id         bigint primary key AUTO_INCREMENT,
    api_key    varchar(255),
    secret_key varchar(255),
    username varchar(255)
);




