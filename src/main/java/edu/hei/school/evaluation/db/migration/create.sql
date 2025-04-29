-- ENUM Saison & Match
CREATE TYPE saison_status AS ENUM ('NOT_STARTED', 'STARTED', 'FINISHED');
CREATE TYPE match_status AS ENUM ('SCHEDULED', 'FINISHED');

-- 1. Table Championnat
CREATE TABLE Championnat (
                             id VARCHAR PRIMARY KEY,
                             nom VARCHAR(255) NOT NULL,
                             pays VARCHAR(255) NOT NULL
);

-- 2. Table Club
CREATE TABLE Club (
                      id VARCHAR PRIMARY KEY,
                      nom VARCHAR(255) NOT NULL,
                      acronyme VARCHAR(10) NOT NULL,
                      annee_creation INT NOT NULL,
                      nom_stade VARCHAR(255) NOT NULL,
                      championnat_id VARCHAR,
                      FOREIGN KEY (championnat_id) REFERENCES Championnat(id)
);

-- 3. Table Entraineur
CREATE TABLE Entraineur (
                            id VARCHAR PRIMARY KEY,
                            nom VARCHAR(255) NOT NULL,
                            nationalite VARCHAR(255) NOT NULL,
                            club_id VARCHAR,
                            FOREIGN KEY (club_id) REFERENCES Club(id)
);

-- 4. Table Joueur
CREATE TABLE Joueur (
                        id VARCHAR PRIMARY KEY,
                        nom VARCHAR(255) NOT NULL,
                        numero INT NOT NULL,
                        poste VARCHAR(255) NOT NULL,
                        nationalite VARCHAR(255) NOT NULL,
                        age INT NOT NULL,
                        club_id VARCHAR,
                        FOREIGN KEY (club_id) REFERENCES Club(id)
);

-- 5. Table Saison
CREATE TABLE Saison (
                        id VARCHAR PRIMARY KEY,
                        annee_debut INT NOT NULL,
                        annee_fin INT NOT NULL,
                        championnat_id VARCHAR,
                        status saison_status NOT NULL,
                        FOREIGN KEY (championnat_id) REFERENCES Championnat(id)
);

-- 6. Table Match
CREATE TABLE Match (
                       id VARCHAR PRIMARY KEY,
                       championnat_id VARCHAR,
                       club_domicile_id VARCHAR,
                       club_exterieur_id VARCHAR,
                       stade VARCHAR(255),
                       date_heure TIMESTAMP,
                       saison_id VARCHAR,
                       status match_status NOT NULL,
                       FOREIGN KEY (championnat_id) REFERENCES Championnat(id),
                       FOREIGN KEY (club_domicile_id) REFERENCES Club(id),
                       FOREIGN KEY (club_exterieur_id) REFERENCES Club(id),
                       FOREIGN KEY (saison_id) REFERENCES Saison(id)
);

-- 7. Table Statistiques_Club
CREATE TABLE Statistiques_Club (
                                   id VARCHAR PRIMARY KEY,
                                   club_id VARCHAR,
                                   saison_id VARCHAR,
                                   points INT DEFAULT 0,
                                   buts_marqués INT DEFAULT 0,
                                   buts_encaissés INT DEFAULT 0,
                                   différence_buts INT DEFAULT 0,
                                   clean_sheets INT DEFAULT 0,
                                   FOREIGN KEY (club_id) REFERENCES Club(id),
                                   FOREIGN KEY (saison_id) REFERENCES Saison(id)
);

-- 8. Table Statistiques_Joueur
CREATE TABLE Statistiques_Joueur (
                                     id VARCHAR PRIMARY KEY,
                                     joueur_id VARCHAR,
                                     saison_id VARCHAR,
                                     buts INT DEFAULT 0,
                                     passes INT DEFAULT 0,
                                     cartons_jaunes INT DEFAULT 0,
                                     cartons_rouges INT DEFAULT 0,
                                     minutes_jouees INT DEFAULT 0,
                                     FOREIGN KEY (joueur_id) REFERENCES Joueur(id),
                                     FOREIGN KEY (saison_id) REFERENCES Saison(id)
);

-- 9. Table Goal
CREATE TABLE Goal (
                      id VARCHAR PRIMARY KEY,
                      joueur_id VARCHAR NOT NULL,
                      match_id VARCHAR NOT NULL,
                      minute INT NOT NULL,
                      est_penalty BOOLEAN DEFAULT FALSE,
                      est_contre_son_camp BOOLEAN DEFAULT FALSE,
                      FOREIGN KEY (joueur_id) REFERENCES Joueur(id),
                      FOREIGN KEY (match_id) REFERENCES Match(id)
);

-- 10. Table Championnat_Classement
CREATE TABLE Championnat_Classement (
                                        id VARCHAR PRIMARY KEY,
                                        championnat_id VARCHAR,
                                        saison_id VARCHAR,
                                        club_id VARCHAR,
                                        rang INT,
                                        FOREIGN KEY (championnat_id) REFERENCES Championnat(id),
                                        FOREIGN KEY (saison_id) REFERENCES Saison(id),
                                        FOREIGN KEY (club_id) REFERENCES Club(id)
);
