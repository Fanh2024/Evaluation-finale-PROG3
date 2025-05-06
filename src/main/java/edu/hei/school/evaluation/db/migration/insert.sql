INSERT INTO Championship (id, name, country) VALUES
                                                 ('CHAMP_LIGA', 'La Liga', 'Spain'),
                                                 ('CHAMP_PL', 'Premier League', 'England'),
                                                 ('CHAMP_L1', 'Ligue 1', 'France'),
                                                 ('CHAMP_BUND', 'Bundesliga', 'Germany'),
                                                 ('CHAMP_SA', 'Serie A', 'Italy');

INSERT INTO Club (id, name, acronym, creation_year, stadium_name, championship_id) VALUES
                                                                                       ('CLUB_RMA', 'Real Madrid FC', 'RMA', 1902, 'Santiago Bernabeu', 'CHAMP_LIGA'),
                                                                                       ('CLUB_FCB', 'FC Barcelona', 'FCB', 1899, 'Lluís Companys', 'CHAMP_LIGA'),
                                                                                       ('CLUB_MCI', 'Manchester City', 'MCI', 1880, 'Etihad Stadium', 'CHAMP_PL'),
                                                                                       ('CLUB_PSG', 'Paris Saint Germain', 'PSG', 1970, 'Parc des Princes', 'CHAMP_L1'),
                                                                                       ('CLUB_FCBM', 'FC Bayern Munich', 'FCB', 1900, 'Allianz Arena', 'CHAMP_BUND'),
                                                                                       ('CLUB_JUV', 'Juventus FC', 'JUV', 1897, 'Juventus Stadium', 'CHAMP_SA'),
                                                                                       ('CLUB_ATM', 'Atletico Madrid', 'ATM', 1880, 'Metropolitano', 'CHAMP_LIGA'),
                                                                                       ('CLUB_OM', 'Olympique de Marseille', 'OM', 1899, 'Orange Vélodrome', 'CHAMP_L1');

INSERT INTO Coach (id, name, nationality, club_id) VALUES
                                                       ('COACH_ANCE', 'Carlo Ancelotti', 'Italian', 'CLUB_RMA'),
                                                       ('COACH_FLICK', 'Hansi Flick', 'German', 'CLUB_FCB');

INSERT INTO Player (id, name, number, position, nationality, age, club_id) VALUES
                                                                               ('PLAYER_VINI', 'Vinicius Jr', 7, 'Forward', 'Brazil', 24, 'CLUB_RMA'),
                                                                               ('PLAYER_KYLIAN', 'Kylian Mbappé', 9, 'Forward', 'France', 26, 'CLUB_RMA'),
                                                                               ('PLAYER_YAMAL', 'Lamine Yamal', 19, 'Forward', 'Spain', 17, 'CLUB_FCB'),
                                                                               ('PLAYER_TORRES', 'Ferran Torres', 7, 'Forward', 'Spain', 25, 'CLUB_FCB');

INSERT INTO Season (id, start_year, end_year, championship_id, season_status) VALUES
                                                                                  ('SEASON_24_25_LIGA', 2024, 2025, 'CHAMP_LIGA', 'STARTED'),
                                                                                  ('SEASON_24_25_L1', 2024, 2025, 'CHAMP_L1', 'STARTED');

INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status) VALUES
                                                                                                                     ('MATCH_1', 'CHAMP_LIGA', 'CLUB_RMA', 'CLUB_FCB', 'Santiago Bernabeu', '2025-05-01 21:00:00', 'SEASON_24_25_LIGA', 'STARTED'),
                                                                                                                     ('MATCH_2', 'CHAMP_LIGA', 'CLUB_FCB', 'CLUB_RMA', 'Lluís Companys', '2025-05-08 18:00:00', 'SEASON_24_25_LIGA', 'STARTED');

INSERT INTO Championship_Ranking (id, championship_id, season_id, club_id, rank) VALUES
                                                                                     ('RANK1', 'CHAMP_LIGA', 'SEASON_24_25_LIGA', 'CLUB_RMA', 1),
                                                                                     ('RANK2', 'CHAMP_LIGA', 'SEASON_24_25_LIGA', 'CLUB_FCB', 2);

INSERT INTO Club_Statistics (id, club_id, season_id, points, goals_scored, goals_conceded, goal_difference, clean_sheets) VALUES
                                                                                                                              ('STAT_RMA', 'CLUB_RMA', 'SEASON_24_25_LIGA', 0, 0, 0, 0, 0),
                                                                                                                              ('STAT_FCB', 'CLUB_FCB', 'SEASON_24_25_LIGA', 0, 0, 0, 0, 0);

INSERT INTO Player_Statistics (id, player_id, season_id, assists, yellow_cards, red_cards, minutes_played)
VALUES
    ('STAT_VINI_24_25', 'PLAYER_VINI', 'SEASON_24_25_LIGA', 7, 3, 0, 2200),
    ('STAT_KYLIAN_24_25', 'PLAYER_KYLIAN', 'SEASON_24_25_LIGA', 6, 2, 0, 2400),
    ('STAT_YAMAL_24_25', 'PLAYER_YAMAL', 'SEASON_24_25_LIGA', 9, 1, 0, 1800),
    ('STAT_TORRES_24_25', 'PLAYER_TORRES', 'SEASON_24_25_LIGA', 4, 2, 1, 2000);


-- Buts de Vinicius Jr dans MATCH_1
INSERT INTO Goal (id, player_id, match_id, minute, is_penalty, is_own_goal) VALUES
                                                                                ('GOAL_1', 'PLAYER_VINI', 'MATCH_1', 23, FALSE, FALSE),
                                                                                ('GOAL_2', 'PLAYER_VINI', 'MATCH_1', 67, FALSE, FALSE);

-- Buts de Mbappé dans MATCH_1
INSERT INTO Goal (id, player_id, match_id, minute, is_penalty, is_own_goal) VALUES
                                                                                ('GOAL_3', 'PLAYER_KYLIAN', 'MATCH_1', 10, TRUE, FALSE),
                                                                                ('GOAL_4', 'PLAYER_KYLIAN', 'MATCH_1', 89, FALSE, FALSE);

-- But de Lamine Yamal dans MATCH_2
INSERT INTO Goal (id, player_id, match_id, minute, is_penalty, is_own_goal) VALUES
    ('GOAL_5', 'PLAYER_YAMAL', 'MATCH_2', 31, FALSE, FALSE);

-- But contre son camp de Ferran Torres dans MATCH_2
INSERT INTO Goal (id, player_id, match_id, minute, is_penalty, is_own_goal) VALUES
    ('GOAL_6', 'PLAYER_TORRES', 'MATCH_2', 76, FALSE, TRUE);

-- [GET, PUT] /clubs/{id}/players : récupérer ou créer/mettre à jour les joueurs affectés à un club. (remplacer tous les joueurs du club)
INSERT INTO Player_Statistics (id, player_id, season_id, assists, yellow_cards, red_cards, minutes_played)
VALUES
    ('STAT_GAVI_24_25', 'PLAYER_GAVI', 'SEASON_24_25_LIGA', 5, 2, 0, 2100),
    ('STAT_PEDRI_24_25', 'PLAYER_PEDRI', 'SEASON_24_25_LIGA', 8, 1, 0, 2300),
    ('STAT_LEWA_24_25', 'PLAYER_LEWANDOWSKI', 'SEASON_24_25_LIGA', 12, 3, 0, 2500);


