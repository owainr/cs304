CREATE TABLE branch ( 
	branch_id integer not null PRIMARY KEY,
	branch_name varchar2(20) not null,
	branch_addr varchar2(50),
	branch_city varchar2(20) not null,
	branch_phone integer 
);

CREATE TABLE user (
	username char(20) not null PRIMARY KEY,
	email char(40) not null PRIMARY KEY,
	favGenreCategory char(20) not null,
	watchListID int UNIQUE not null,
	historyID int UNIQUE not null,
);


INSERT INTO branch VALUES (1, "ABC", "123 Charming Ave", "Vancouver", "6041234567");
INSERT INTO branch VALUES (2, "DEF", "123 Coco Ave", "Vancouver", "6044567890");
INSERT INTO user VALUES("Jack", "a@b.ca", "Documentary", 1, 1);
INSERT INTO user VALUES("Dad", "b@c.ca", "Thriller", 2, 2);