create table sequences          (name varchar(50) not null, value    bigint, primary key (name));
create sequence hibernate_sequence;
create table hibernate_sequence (name varchar(50) not null, next_val bigint, primary key (name));
insert into hibernate_sequence(name, next_val) ('default', 10000);

create table city       (id bigint not null, name varchar(255), state_id bigint, lorem varchar(255), primary key (id));
create table postalcode (id bigint not null, code varchar(255), city_id bigint,  lorem varchar(255), primary key (id));
create table state      (id bigint not null, name varchar(255),                  lorem varchar(255), primary key (id));

alter table city       add constraint city_state     foreign key (state_id) references state (id);
alter table postalcode add constraint postalcode_city foreign key (city_id) references city (id);

create table TestEntity   (id bigint not null, value varchar(255), version bigint, primary key (id));
create table NamedEntity  (id bigint not null, name  varchar(255),                 primary key (id));
create table CachedEntity (id bigint not null, name  varchar(255),                 primary key (id));
create table CachedChild  (id bigint not null, parent_id bigint not null,          primary key (id));
