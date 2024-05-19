--liquibase formatted sql

--changeset a.khaustov:1
alter table asset_contract
add column binance_contract_type varchar(255);




