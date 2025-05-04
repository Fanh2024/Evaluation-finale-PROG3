-- ENUM Saison & Match
CREATE TYPE season_status AS ENUM ('NOT_STARTED', 'STARTED', 'FINISHED');
CREATE TYPE match_status AS ENUM ('NOT_STARTED', 'STARTED', 'FINISHED');

-- 1. Table Championnat
CREATE TABLE Championship (
                              id VARCHAR PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              country VARCHAR(255) NOT NULL
);

-- 2. Table Club
CREATE TABLE Club (
                      id VARCHAR PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      acronym VARCHAR(10) NOT NULL,
                      creation_year INT NOT NULL,
                      stadium_name VARCHAR(255) NOT NULL,
                      championship_id VARCHAR,
                      FOREIGN KEY (championship_id) REFERENCES Championship(id)
);

-- 3. Table Coach
CREATE TABLE Coach (
                       id VARCHAR PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       nationality VARCHAR(255) NOT NULL,
                       club_id VARCHAR,
                       FOREIGN KEY (club_id) REFERENCES Club(id)
);

-- 4. Table Player
CREATE TABLE Player (
                        id VARCHAR PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        number INT NOT NULL,
                        position VARCHAR(255) NOT NULL CHECK (position IN ('STRIKER', 'MIDFIELDER', 'DEFENSE', 'GOAL_KEEPER')),
                        nationality VARCHAR(255) NOT NULL,
                        age INT NOT NULL,
                        club_id VARCHAR,
                        FOREIGN KEY (club_id) REFERENCES Club(id)
);

/*
 UPDATE Player
SET position = 'MIDFIELDER'
WHERE position = 'Midfielder';

UPDATE Player
SET position = 'STRIKER'
WHERE position = 'Forward';

SELECT DISTINCT position FROM Player;
✅ Étape 2 : Ajouter la contrainte CHECK
Une fois les données nettoyées :

ALTER TABLE Player
ADD CONSTRAINT chk_position_valid
CHECK (position IN ('STRIKER', 'MIDFIELDER', 'DEFENSE', 'GOAL_KEEPER'));
 */

-- 5. Table Season
CREATE TABLE Season (
                        id VARCHAR PRIMARY KEY,
                        start_year INT NOT NULL,
                        end_year INT NOT NULL,
                        championship_id VARCHAR,
                        season_status VARCHAR NOT NULL,
                        FOREIGN KEY (championship_id) REFERENCES Championship(id)
);

-- 6. Table Match
CREATE TABLE Match (
                       id VARCHAR PRIMARY KEY,
                       championship_id VARCHAR,
                       home_club_id VARCHAR,
                       away_club_id VARCHAR,
                       stadium VARCHAR(255),
                       date_time TIMESTAMP,
                       season_id VARCHAR,
                       match_status VARCHAR NOT NULL,
                       FOREIGN KEY (championship_id) REFERENCES Championship(id),
                       FOREIGN KEY (home_club_id) REFERENCES Club(id),
                       FOREIGN KEY (away_club_id) REFERENCES Club(id),
                       FOREIGN KEY (season_id) REFERENCES Season(id)
);

-- 7. Table Club Statistics
CREATE TABLE Club_Statistics (
                                 id VARCHAR PRIMARY KEY,
                                 club_id VARCHAR,
                                 season_id VARCHAR,
                                 points INT DEFAULT 0,
                                 goals_scored INT DEFAULT 0,
                                 goals_conceded INT DEFAULT 0,
                                 goal_difference INT DEFAULT 0,
                                 clean_sheets INT DEFAULT 0,
                                 FOREIGN KEY (club_id) REFERENCES Club(id),
                                 FOREIGN KEY (season_id) REFERENCES Season(id)
);

-- 8. Table Player Statistics
CREATE TABLE Player_Statistics (
                                   id VARCHAR PRIMARY KEY,
                                   player_id VARCHAR,
                                   season_id VARCHAR,
                                   assists INT DEFAULT 0,
                                   yellow_cards INT DEFAULT 0,
                                   red_cards INT DEFAULT 0,
                                   minutes_played INT DEFAULT 0,
                                   FOREIGN KEY (player_id) REFERENCES Player(id),
                                   FOREIGN KEY (season_id) REFERENCES Season(id)
);

-- 9. Table Goal
CREATE TABLE Goal (
                      id VARCHAR PRIMARY KEY,
                      player_id VARCHAR NOT NULL,
                      match_id VARCHAR NOT NULL,
                      minute INT NOT NULL,
                      is_penalty BOOLEAN DEFAULT FALSE,
                      is_own_goal BOOLEAN DEFAULT FALSE,
                      FOREIGN KEY (player_id) REFERENCES Player(id),
                      FOREIGN KEY (match_id) REFERENCES Match(id)
);

-- 10. Table Championship Ranking
CREATE TABLE Championship_Ranking (
                                      id VARCHAR PRIMARY KEY,
                                      championship_id VARCHAR,
                                      season_id VARCHAR,
                                      club_id VARCHAR,
                                      rank INT,
                                      FOREIGN KEY (championship_id) REFERENCES Championship(id),
                                      FOREIGN KEY (season_id) REFERENCES Season(id),
                                      FOREIGN KEY (club_id) REFERENCES Club(id)
);

