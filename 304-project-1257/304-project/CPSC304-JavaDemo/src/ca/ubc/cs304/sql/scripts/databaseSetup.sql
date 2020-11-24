
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



INSERT INTO picture VALUES ("Twilight", 126, "Catherine Hardwicke", 6, "Romance");
INSERT INTO picture VALUES ("Gone Girl", 149, "David Fincher", null, "Thriller");
INSERT INTO user VALUES("Jack", "a@b.ca", "Documentary", 1, 1);
INSERT INTO user VALUES("Dad", "b@c.ca", "Thriller", 2, 2);