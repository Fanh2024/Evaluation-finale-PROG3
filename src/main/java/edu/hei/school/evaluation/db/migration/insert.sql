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
                                                                                                                     ('MATCH_1', 'CHAMP_LIGA', 'CLUB_RMA', 'CLUB_FCB', 'Santiago Bernabeu', '2025-05-01 21:00:00', 'SEASON_24_25_LIGA', 'SCHEDULED'),
                                                                                                                     ('MATCH_2', 'CHAMP_LIGA', 'CLUB_FCB', 'CLUB_RMA', 'Lluís Companys', '2025-05-08 18:00:00', 'SEASON_24_25_LIGA', 'SCHEDULED');

INSERT INTO Championship_Ranking (id, championship_id, season_id, club_id, rank) VALUES
                                                                                     ('RANK1', 'CHAMP_LIGA', 'SEASON_24_25_LIGA', 'CLUB_RMA', 1),
                                                                                     ('RANK2', 'CHAMP_LIGA', 'SEASON_24_25_LIGA', 'CLUB_FCB', 2);

INSERT INTO Club_Statistics (id, club_id, season_id, points, goals_scored, goals_conceded, goal_difference, clean_sheets) VALUES
                                                                                                                              ('STAT_RMA', 'CLUB_RMA', 'SEASON_24_25_LIGA', 0, 0, 0, 0, 0),
                                                                                                                              ('STAT_FCB', 'CLUB_FCB', 'SEASON_24_25_LIGA', 0, 0, 0, 0, 0);
