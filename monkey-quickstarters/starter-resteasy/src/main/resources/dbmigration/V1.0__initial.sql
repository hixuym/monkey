-- apply changes
create table tb_user (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  age                           integer,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_tb_user primary key (id)
);

