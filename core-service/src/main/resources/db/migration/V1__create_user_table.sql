create table if not exists "user"
(
    "id"                   bigserial
        constraint user_pk primary key,
    "first_name"           varchar(50)                                       not null,
    "last_name"            varchar(50)                                       not null,
    "phone"                varchar(40)                                       not null,
    "email"                varchar(100)                                      not null,
    "password"             varchar(255)                                      not null,
    "role"                 varchar(10)   default 'USER'::character varying   not null,
    "balance"              numeric(7, 2) default 0.0                         not null,
    "verified"             boolean       default false                       not null,
    "status"               varchar(15)   default 'ACTIVE'::character varying not null,
    "passport_img_url"       varchar(255),
    "driving_license_img_url" varchar(255)
);

create unique index if not exists user_email_uindex
    on "user" ("email");
