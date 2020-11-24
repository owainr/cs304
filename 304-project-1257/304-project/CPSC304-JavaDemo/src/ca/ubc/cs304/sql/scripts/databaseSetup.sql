
CREATE TABLE picture (
	pictureTitle char(50) not null PRIMARY KEY,
	releaseDate date not null PRIMARY KEY,
	length real not null,
	director: char(40) not null,
	seriesID int
);


CREATE TABLE user (
	username char(20) not null PRIMARY KEY,
	email char(40) not null PRIMARY KEY,
	favGenreCategory char(20) not null,
	watchListID int UNIQUE not null,
	historyID int UNIQUE not null,
);

CREATE TABLE userWatches (
	username char(20) not null PRIMARY KEY,
	email char(40) not null PRIMARY KEY,
	pictureTitle char(50) not null PRIMARY KEY,
	releaseDate date not null PRIMARY KEY,
);



INSERT INTO picture VALUES ("Twilight", 2008-10-21, 126, "Catherine Hardwicke", 6, "Romance");
INSERT INTO picture VALUES ("Gone Girl", 2014-10-03, 149, "David Fincher", null, "Thriller");
INSERT INTO picture VALUES ("The Fault in Our Stars", 2014-06-06, 133, "Josh Boone", null, "Romance");
INSERT INTO picture VALUES ("Baby Driver", 2017-06-28, 115, "Edgar Wright", null, "Action");
INSERT INTO picture VALUES ("Shutter Island", 2010-02-19, 138, "Martin Scorsese", null, "Thriller");
INSERT INTO user VALUES("Jack", "a@b.ca", "Documentary", 1, 1);
INSERT INTO user VALUES("Dad", "b@c.ca", "Thriller", 2, 2);
INSERT INTO user VALUES ("Patrice", "c@d.ca", "Romance", 3, 3);