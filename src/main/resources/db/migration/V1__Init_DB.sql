create table if not exists users
(
    id           varchar(255) not null
        constraint users_pkey
            primary key,
    name         varchar(255) not null,
    email        varchar(255),
    memory_slots_num integer      not null,
    first_visit  timestamp    not null,
    last_visit   timestamp    not null
);

alter table users
    owner to postgres;

create table if not exists maps
(
    id       bigserial    not null
        constraint maps_pkey
            primary key,
    user_id  varchar(255) not null
        constraint maps_users_id_fk
            references users,
    file_id  varchar(255) not null,
    filename varchar(255) not null,
    updated  timestamp    not null,
    version  varchar(255) not null
);

alter table maps
    owner to postgres;