--liquibase formatted sql

--changeset a.khaustov:1
insert into exchange(id, name)
values (1, 'Bingx'),
       (2, 'Binance');




