-- SQL init queries for setting up database.

--use connectfour;
 use shapeapp_se;

set FOREIGN_KEY_CHECKS = 0;

select 'Drop tables' as '';
drop table if exists mamn01__users;

select 'Create users' as '';
create table mamn01__users (
	id			integer auto_increment,
	name 		varchar(30) NOT NULL,
	hugpoints   integer DEFAULT 0,
	lat         DECIMAL(10, 8) NOT NULL, 
	lng         DECIMAL(11, 8) NOT NULL
	isBusy      bool DEFAULT FALSE,    
	PRIMARY KEY (id)
);

select 'Create log' as '';
create table mamn01__log (
	id			integer auto_increment,
	user 		varchar(30) NOT NULL,
	message     varchar(255),
	PRIMARY KEY (id)
);


set FOREIGN_KEY_CHECKS = 1;