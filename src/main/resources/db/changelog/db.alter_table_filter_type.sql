--liquibase formatted sql

--changeset a.khaustov:1
alter table filter_type
alter column asset_contract_id
drop not null;




