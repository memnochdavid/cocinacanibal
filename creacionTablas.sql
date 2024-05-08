--delete from usuarios;
--delete from recetas;
--drop table usuarios;
--drop table recetas;

create table usuarios
(
	--cod 	number(3),
	usr 	varchar2(25),
	pass    varchar2(25) not null,
	mail    varchar2(50),
    lvl     number(1) check (lvl between 0 and 2),
    constraint pk_usuarios primary key (usr)
);



create table recetas
(
	cod	    number(3),
	owner 	varchar2(25),
    nombre  varchar2(25),
	descripcion    varchar2(100) not null,
	constraint pk_recetas primary key (cod),
	constraint fk_rec_usu foreign key (owner) references usuarios(usr)	
);


insert into usuarios values ('admin', 'admin', 'admin@admin.com', 2);
insert into usuarios values ('base', 'base', 'base', 0);