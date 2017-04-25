-- SQL init queries for setting up database.

--use connectfour;
 use shapeapp_se;

set FOREIGN_KEY_CHECKS = 0;

select 'Drop tables' as '';
drop table if exists mamn01__users;
drop table if exists mamn01__log;

select 'Create users' as '';
create table mamn01__users (
	id			      integer auto_increment,
	name 		      varchar(30) NOT NULL,
	device  		  varchar(50) UNIQUE NOT NULL,
	hugrange  		integer DEFAULT 2000,
	hugpoints     integer DEFAULT 0,
	lat           DECIMAL(10, 8) NOT NULL, 
	lng           DECIMAL(11, 8) NOT NULL,
	isBusy        integer DEFAULT 0,    
	wantsHug      bool DEFAULT 0,    
	hugfailed     bool DEFAULT 0,    
	hugccess      bool DEFAULT 0,    
	lost          bool DEFAULT 0,    
	isAdmin       bool DEFAULT 0,    
	PRIMARY KEY (id)
);

select 'Create hugs' as '';
create table mamn01__hugs (
	id			      integer auto_increment,
	hugger 		    integer NOT NULL,
	hugged 		    integer NOT NULL,
	hug_date      DATE,
	PRIMARY KEY (id)
);

select 'Create log' as '';
create table mamn01__log (
	id			  integer auto_increment,
	device 		varchar(50) NOT NULL,
	message   TEXT, 
	logdate   DATETIME,
	PRIMARY KEY (id)
);


set FOREIGN_KEY_CHECKS = 1;
