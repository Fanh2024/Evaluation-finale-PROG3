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
                        position VARCHAR(255) NOT NULL,
                        nationality VARCHAR(255) NOT NULL,
                        age INT NOT NULL,
                        club_id VARCHAR,
                        FOREIGN KEY (club_id) REFERENCES Club(id)
);

--ALTER TABLE player DROP CONSTRAINT player_position_check;

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

CREATE TABLE Transfert (
                           id SERIAL PRIMARY KEY,
                           player_id VARCHAR NOT NULL REFERENCES Player(id),
                           club_id VARCHAR REFERENCES Club(id),
                           type VARCHAR(3) CHECK (type IN ('IN', 'OUT')),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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



-- Inserts pour les données générales

-- Championnats
INSERT INTO Championship (id, name, country) VALUES
    ('ch1', 'Ligue 1', 'France');

-- Clubs
INSERT INTO Club (id, name, acronym, creation_year, stadium_name, championship_id) VALUES
                                                                                       ('club1', 'Club 1', 'C1', 1902, 'Stade 1', 'ch1'),
                                                                                       ('club2', 'Club 2', 'C2', 1905, 'Stade 2', 'ch1'),
                                                                                       ('club3', 'Club 3', 'C3', 1910, 'Stade 3', 'ch1');

-- Coaches
INSERT INTO Coach (id, name, nationality, club_id) VALUES
                                                       ('coach1', 'Entraîneur 1', 'Français', 'club1'),
                                                       ('coach2', 'Entraîneur 2', 'Italien', 'club2'),
                                                       ('coach3', 'Entraîneur 3', 'Allemand', 'club3');

-- Players
INSERT INTO Player (id, name, number, position, nationality, age, club_id) VALUES
                                                                               ('player1', 'Gardien 1', 1, 'GOAL_KEEPER', 'Espagnol', 30, 'club1'),
                                                                               ('player2', 'Défense 1', 2, 'DEFENSE', 'Espagnol', 25, 'club1'),
                                                                               ('player3', 'Milieu 1', 5, 'MIDFIELDER', 'Espagnol', 24, 'club1'),
                                                                               ('player4', 'Attaquant 1', 7, 'STRIKER', 'Espagnol', 17, 'club1'),
                                                                               ('player5', 'Gardien 2', 1, 'GOAL_KEEPER', 'Espagnol', 21, 'club2'),
                                                                               ('player6', 'Défense 2', 2, 'DEFENSE', 'Belge', 34, 'club2'),
                                                                               ('player7', 'Milieu 2', 5, 'MIDFIELDER', 'Français', 29, 'club2'),
                                                                               ('player8', 'Attaquant 2', 7, 'STRIKER', 'Allemand', 18, 'club2'),
                                                                               ('player9', 'Gardien 3', 1, 'GOAL_KEEPER', 'Brésilien', 28, 'club3'),
                                                                               ('player10', 'Défense 3', 2, 'DEFENSE', 'Brésilien', 21, 'club3'),
                                                                               ('player11', 'Milieu 3', 5, 'MIDFIELDER', 'Français', 29, 'club3'),
                                                                               ('player12', 'Attaquant 3', 7, 'STRIKER', 'Allemand', 23, 'club3');

-- Saisons
INSERT INTO Season (id, start_year, end_year, championship_id, season_status) VALUES
                                                                                  ('season2024', 2024, 2025, 'ch1', 'NOT_STARTED'),
                                                                                  ('season2025', 2025, 2026, 'ch1', 'NOT_STARTED');

-- Matchs
INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status) VALUES
                                                                                                                     ('match1', 'ch1', 'club1', 'club2', 'Stade 1', '2024-09-01 15:00:00', 'season2024', 'NOT_STARTED'),
                                                                                                                     ('match2', 'ch1', 'club2', 'club3', 'Stade 2', '2024-09-02 18:00:00', 'season2024', 'NOT_STARTED'),
                                                                                                                     ('match3', 'ch1', 'club1', 'club3', 'Stade 1', '2024-09-03 17:00:00', 'season2024', 'NOT_STARTED'),
                                                                                                                     ('match4', 'ch1', 'club3', 'club2', 'Stade 3', '2024-09-04 16:00:00', 'season2024', 'NOT_STARTED'),
                                                                                                                     ('match5', 'ch1', 'club2', 'club1', 'Stade 2', '2024-09-05 14:00:00', 'season2024', 'NOT_STARTED'),
                                                                                                                     ('match6', 'ch1', 'club3', 'club1', 'Stade 3', '2024-09-06 17:00:00', 'season2024', 'NOT_STARTED');

INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status) VALUES
                                                                                                                     ('match7', 'ch1', 'club1', 'club2', 'Stade 1', '2025-09-01 15:00:00', 'season2025', 'NOT_STARTED'),
                                                                                                                     ('match8', 'ch1', 'club2', 'club3', 'Stade 2', '2025-09-02 18:00:00', 'season2025', 'NOT_STARTED'),
                                                                                                                     ('match9', 'ch1', 'club1', 'club3', 'Stade 1', '2025-09-03 17:00:00', 'season2025', 'NOT_STARTED'),
                                                                                                                     ('match10', 'ch1', 'club3', 'club2', 'Stade 3', '2025-09-04 16:00:00', 'season2025', 'NOT_STARTED'),
                                                                                                                     ('match11', 'ch1', 'club2', 'club1', 'Stade 2', '2025-09-05 14:00:00', 'season2025', 'NOT_STARTED'),
                                                                                                                     ('match12', 'ch1', 'club3', 'club1', 'Stade 3', '2025-09-06 17:00:00', 'season2025', 'NOT_STARTED');
-- Buts
INSERT INTO Goal (id, player_id, match_id, minute, is_penalty, is_own_goal) VALUES
                                                                                ('goal1', 'player4', 'match1', 2, false, false),
                                                                                ('goal2', 'player4', 'match1', 8, false, false),
                                                                                ('goal3', 'player3', 'match1', 50, false, false),
                                                                                ('goal4', 'player2', 'match1', 60, false, false),
                                                                                ('goal5', 'player8', 'match2', 88, false, false),
                                                                                ('goal6', 'player4', 'match3', 69, false, false),
                                                                                ('goal7', 'player8', 'match5', 88, false, false),
                                                                                ('goal8', 'player1', 'match6', 88, false, true),
                                                                                ('goal9', 'player1', 'match6', 89, false, true),
                                                                                ('goal10', 'player1', 'match6', 90, false, true),
                                                                                ('goal11', 'player1', 'match1', 1, false, true),
                                                                                ('goal12', 'player12', 'match2', 21, false, false),
                                                                                ('goal13', 'player4', 'match6', 56, false, false),
                                                                                ('goal14', 'player3', 'match6', 90, false, false);

-- Player_Statistics ajustées selon les minutes des buts
INSERT INTO Player_Statistics (id, player_id, season_id, assists, yellow_cards, red_cards, minutes_played)
VALUES
    ('STAT_PLAYER1_24_25', 'player1', 'season2024', 4, 0, 0, 1800),
    ('STAT_PLAYER2_24_25', 'player2', 'season2024', 1, 0, 0, 2100),
    ('STAT_PLAYER3_24_25', 'player3', 'season2024', 2, 0, 0, 2300),
    ('STAT_PLAYER4_24_25', 'player4', 'season2024', 4, 0, 0, 2500),
    ('STAT_PLAYER5_24_25', 'player5', 'season2024', 0, 0, 0, 1500),
    ('STAT_PLAYER6_24_25', 'player6', 'season2024', 0, 0, 0, 2200),
    ('STAT_PLAYER7_24_25', 'player7', 'season2024', 0, 0, 0, 2000),
    ('STAT_PLAYER8_24_25', 'player8', 'season2024', 1, 0, 0, 2700),
    ('STAT_PLAYER9_24_25', 'player9', 'season2024', 0, 0, 0, 1300),
    ('STAT_PLAYER10_24_25', 'player10', 'season2024', 0, 0, 0, 1400),
    ('STAT_PLAYER11_24_25', 'player11', 'season2024', 0, 0, 0, 2000),
    ('STAT_PLAYER12_24_25', 'player12', 'season2024', 1, 0, 0, 2200);

-- Statistiques des clubs
INSERT INTO Club_Statistics (id, club_id, season_id, points, goals_scored, goals_conceded, goal_difference, clean_sheets) VALUES
                                                                                                                              ('stat1', 'club1', 'season2024', 6, 7, 2, 5, 2),
                                                                                                                              ('stat2', 'club2', 'season2024', 5, 1, 4, -3, 1),
                                                                                                                              ('stat3', 'club3', 'season2024', 5, 2, 5, -3, 1);

-- Classement des championnats
INSERT INTO Championship_Ranking (id, championship_id, season_id, club_id, rank) VALUES
                                                                                     ('rank1', 'ch1', 'season2024', 'club1', 1),
                                                                                     ('rank2', 'ch1', 'season2024', 'club3', 2),
                                                                                     ('rank3', 'ch1', 'season2024', 'club2', 3);
