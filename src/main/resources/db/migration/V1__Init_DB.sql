create table if not exists users
(
	id bigserial not null
		constraint users_pkey
			primary key,
    username varchar(255) not null
        constraint users_username_uindex
            unique,
    email varchar(255) not null
        constraint users_email_uindex
            unique,
    password varchar(255) not null,
    role varchar(255) not null,
	created timestamp not null
);

alter table users owner to postgres;

create table if not exists maps
(
	id bigserial not null
		constraint maps_pkey
			primary key,
    user_id bigint not null
        constraint maps_users_id_fk
            references users,
    filename varchar(255) not null,
    description varchar(255),
	created timestamp not null,
    updated timestamp,
	deleted timestamp
);

alter table maps owner to postgres;