create table if not exists "order"
(
    id      bigserial    not null
        constraint order_pk
            primary key,
    car_id  integer      not null,
    user_id bigint       not null,
    start   timestamp    not null,
    ending  timestamp,
    price   numeric(10, 2),
    status  varchar(25)  not null,
    uuid    varchar(255) not null
);
