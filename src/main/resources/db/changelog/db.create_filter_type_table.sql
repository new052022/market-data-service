--liquibase formatted sql

--changeset a.khaustov:1
create table filter_type
(
    id       bigint primary key generated by default as identity,
    asset_contract_id bigint not null,
    constraint fk_asset_contract_id
        foreign key (asset_contract_id)
            references asset_contract(id),
    filter_type varchar(255),
    max_price double precision,
    min_price double precision,
    tick_size double precision,
    max_qty double precision,
    min_qty double precision,
    step_size double precision,
    limit_size double precision,
    notional double precision,
    multiplier_up double precision,
    multiplier_down double precision,
    multiplier_decimal double precision
);


