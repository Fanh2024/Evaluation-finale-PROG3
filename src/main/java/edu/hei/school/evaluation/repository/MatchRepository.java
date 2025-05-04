package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.exception.BadRequestException;
import edu.hei.school.evaluation.exception.NotFoundException;
import edu.hei.school.evaluation.model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repository responsable des opérations SQL sur les matchs.
 */
public class MatchRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();

    /**
     * Génère les matchs aller-retour pour chaque paire de clubs dans un championnat donné,
     * si la saison est au statut STARTED et qu'aucun match n'a encore été généré.
     *
     * @param seasonYear Année de début de la saison
     * @return Liste des matchs créés et insérés en base
     */
    public List<Match> generateMatchesForSeason(int seasonYear) {
        String seasonId = null;
        String championshipId = null;
        SeasonStatus seasonStatus = null;
        List<String> clubIds = new ArrayList<>();
        List<Match> createdMatches = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            // Récupération de la saison
            PreparedStatement seasonStmt = conn.prepareStatement("""
            SELECT id, championship_id, season_status FROM Season WHERE start_year = ?
        """);
            seasonStmt.setInt(1, seasonYear);
            ResultSet seasonRs = seasonStmt.executeQuery();
            if (seasonRs.next()) {
                seasonId = seasonRs.getString("id");
                championshipId = seasonRs.getString("championship_id");
                seasonStatus = SeasonStatus.valueOf(seasonRs.getString("season_status"));
            } else {
                throw new NotFoundException("Saison introuvable pour " + seasonYear);
            }

            // Vérification du statut de la saison
            if (seasonStatus != SeasonStatus.STARTED) {
                throw new BadRequestException("La saison n'est pas au statut STARTED");
            }

            // Vérification de l'existence des matchs
            PreparedStatement checkMatchStmt = conn.prepareStatement("""
            SELECT COUNT(*) FROM Match WHERE season_id = ?
        """);
            checkMatchStmt.setString(1, seasonId);
            ResultSet matchRs = checkMatchStmt.executeQuery();
            if (matchRs.next() && matchRs.getInt(1) > 0) {
                throw new BadRequestException("Les matchs ont déjà été générés pour cette saison");
            }

            // Récupération des clubs participants
            PreparedStatement clubsStmt = conn.prepareStatement("""
            SELECT id, stadium_name FROM Club WHERE championship_id = ?
        """);
            clubsStmt.setString(1, championshipId);
            ResultSet clubsRs = clubsStmt.executeQuery();
            Map<String, String> clubStadiums = new HashMap<>();
            while (clubsRs.next()) {
                String clubId = clubsRs.getString("id");
                String stadium = clubsRs.getString("stadium_name");
                clubIds.add(clubId);
                clubStadiums.put(clubId, stadium);
            }

            if (clubIds.size() < 2) {
                throw new RuntimeException("Pas assez de clubs pour générer des matchs");
            }

            // Insertion des matchs
            PreparedStatement matchStmt = conn.prepareStatement("""
            INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """);

            LocalDateTime matchTime = LocalDateTime.of(seasonYear, 8, 1, 20, 45); // Date de départ pour le premier match
            for (int i = 0; i < clubIds.size(); i++) {
                for (int j = 0; j < clubIds.size(); j++) {
                    if (i != j) {
                        String homeClub = clubIds.get(i);
                        String awayClub = clubIds.get(j);
                        String matchId = "MATCH_" + UUID.randomUUID();

                        matchStmt.setString(1, matchId);
                        matchStmt.setString(2, championshipId);
                        matchStmt.setString(3, homeClub);
                        matchStmt.setString(4, awayClub);
                        matchStmt.setString(5, clubStadiums.get(homeClub));
                        matchStmt.setTimestamp(6, Timestamp.valueOf(matchTime));
                        matchStmt.setString(7, seasonId);
                        matchStmt.setString(8, MatchStatus.STARTED.name());

                        matchStmt.addBatch();

                        // Création des objets Match pour ajouter à la liste de réponse
                        Match match = new Match();
                        match.setId(matchId);
                        match.setChampionship(new Championship());
                        match.getChampionship().setId(championshipId);
                        Club home = new Club(); home.setId(homeClub);
                        Club away = new Club(); away.setId(awayClub);
                        match.setHomeClubId(home);
                        match.setAwayClubId(away);
                        match.setStadium(clubStadiums.get(homeClub));
                        match.setDateTime(matchTime);
                        match.setMatchStatus(MatchStatus.STARTED);

                        createdMatches.add(match);

                        // Incrementer la date pour le prochain match
                        matchTime = matchTime.plusDays(3);
                    }
                }
            }

            // Exécution de l'insertion des matchs
            matchStmt.executeBatch();
            return createdMatches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la génération des matchs", e);
        }
    }

    /**
     * Récupère les matchs d'une saison en appliquant des filtres dynamiques.
     *
     * @param seasonYear Année de début de la saison
     * @param status Filtre par statut du match (nullable)
     * @param clubName Filtre par nom de club (nullable, ignore la casse)
     * @param after Date après laquelle les matchs doivent avoir lieu (nullable)
     * @param before Date jusqu'à laquelle les matchs doivent avoir lieu (nullable)
     * @return Liste des matchs filtrés
     */
    public List<MatchLight> getMatchesForSeason(int seasonYear, String status, String clubName, LocalDate after, LocalDate before) {
        List<MatchLight> matches = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            PreparedStatement seasonStmt = conn.prepareStatement("SELECT id FROM Season WHERE start_year = ?");
            seasonStmt.setInt(1, seasonYear);
            ResultSet seasonRs = seasonStmt.executeQuery();
            if (!seasonRs.next()) {
                throw new NotFoundException("Saison introuvable pour " + seasonYear);
            }
            String seasonId = seasonRs.getString("id");

            StringBuilder query = new StringBuilder("""
            SELECT m.* FROM Match m
            JOIN Club h ON m.home_club_id = h.id
            JOIN Club a ON m.away_club_id = a.id
            WHERE m.season_id = ?
        """);

            List<Object> parameters = new ArrayList<>();
            parameters.add(seasonId);

            if (status != null) {
                query.append(" AND m.match_status = ?");
                parameters.add(status);
            }

            if (clubName != null) {
                query.append("""
                AND (
                    LOWER(h.name) LIKE LOWER(?)
                    OR LOWER(a.name) LIKE LOWER(?)
                    OR LOWER(h.acronym) LIKE LOWER(?)
                    OR LOWER(a.acronym) LIKE LOWER(?)
                )
            """);
                String likeClub = "%" + clubName + "%";
                parameters.add(likeClub);
                parameters.add(likeClub);
                parameters.add(likeClub);
                parameters.add(likeClub);
            }

            if (after != null) {
                query.append(" AND m.date_time > ?");
                parameters.add(Timestamp.valueOf(after.atStartOfDay()));
            }

            if (before != null) {
                query.append(" AND m.date_time <= ?");
                parameters.add(Timestamp.valueOf(before.atTime(23, 59, 59)));
            }

            PreparedStatement stmt = conn.prepareStatement(query.toString());
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MatchLight match = new MatchLight(
                        rs.getString("id"),
                        rs.getString("championship_id"),
                        rs.getString("home_club_id"),
                        rs.getString("away_club_id"),
                        rs.getString("stadium"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        MatchStatus.valueOf(rs.getString("match_status"))
                );
                matches.add(match);
            }
            return matches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des matchs", e);
        }
    }


}
