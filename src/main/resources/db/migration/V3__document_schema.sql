create table if not exists document
(
    id         bigint default nextval('user_document_id_seq'::regclass) not null
        constraint user_document_pk
            primary key,
    img_link_1 varchar(255),
    img_link_2 varchar(255),
    type       varchar(10)                                              not null
);

alter table document
    owner to postgres;
