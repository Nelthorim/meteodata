create table if not exists Usuario (
  Nombre varchar(255) primary key,
  Hash varchar(255),
  Nivel smallint default 0
);

create table if not exists Lluvia (
  Fecha datetime,
  Lluvia real
);

create table if not exists Temperatura (
  Fecha datetime,
  Maxima real,
  Minima real
);

create table if not exists Presion (
  Fecha datetime,
  Presion int
)