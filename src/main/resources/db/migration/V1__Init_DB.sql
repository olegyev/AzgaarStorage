create table if not exists users
(
    id         varchar(255) not null
        constraint users_pkey
            primary key,
    name       varchar(255) not null,
    email      varchar(255),
    location   varchar(255),
    last_visit timestamp    not null
);

alter table users
    owner to postgres;

create table if not exists maps
(
    id          bigserial    not null
        constraint maps_pkey
            primary key,
    user_id     varchar(255) not null
        constraint maps_users_id_fk
            references users,
    filename    varchar(255) not null,
    description varchar(255),
    created     timestamp    not null,
    updated     timestamp,
    deleted     timestamp
);

alter table maps
    owner to postgres;