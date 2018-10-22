create table sequences          (name varchar(50) not null, value    bigint, primary key (name));
create sequence hibernate_sequence;
create table hibernate_sequence (name varchar(50) not null, next_val bigint, primary key (name));
insert into hibernate_sequence(name, next_val) ('default', 10000);

create table City       (id bigint not null, name varchar(255), state_id bigint, lorem varchar(255), primary key (id));
create table PostalCode (id bigint not null, code varchar(255), city_id bigint,  lorem varchar(255), primary key (id));
create table State      (id bigint not null, name varchar(255),                  lorem varchar(255), primary key (id));

alter table City       add constraint city_state     foreign key (state_id) references State (id);
alter table PostalCode add constraint postalcode_city foreign key (city_id) references City (id);

create table TestEntity   (id bigint not null, value varchar(255), version bigint, primary key (id));
create table NamedEntity  (id bigint not null, name  varchar(255),                 primary key (id));
create table CachedEntity (id bigint not null, name  varchar(255),                 primary key (id));
