create table if not exists users
(
    id                 bigserial    not null constraint users_pkey primary key,
    oauth2_id          varchar(255),
    name               varchar(255) not null,
    email              varchar(255),
    s3_key             varchar(255) not null,
    memory_slots_num   integer      not null,
    first_visit        timestamp    not null,
    last_visit         timestamp    not null
);

alter table users owner to postgres;

create table if not exists maps
(
    id        bigserial    not null constraint maps_pkey primary key,
    user_id   bigserial    not null constraint maps_users_id_fk references users,
    file_id   varchar(255) not null,
    filename  varchar(255) not null,
    updated   timestamp    not null,
    version   varchar(255) not null,
    thumbnail text	  not null
);

alter table maps owner to postgres;