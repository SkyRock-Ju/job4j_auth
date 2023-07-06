create table person (
    id serial primary key not null,
    login varchar(2000) NOT NULL UNIQUE,
    password varchar(2000) NOT NULL
);