CREATE TABLE IF NOT EXISTS click_counter
(
	id                 bigserial    not null constraint click_counter_pkey primary key,
	click_timestamp    timestamp    not null
);

ALTER TABLE click_counter owner TO postgres;