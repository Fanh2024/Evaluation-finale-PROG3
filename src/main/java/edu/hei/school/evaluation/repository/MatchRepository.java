
package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.exception.BadRequestException;
import edu.hei.school.evaluation.exception.NotFoundException;
import edu.hei.school.evaluation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MatchRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();
    private final GoalRepository goalRepository;

    public MatchRepository(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
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
    public List<Match> getMatchesForSeason(int seasonYear, String status, String clubName, LocalDate after, LocalDate before) {
        List<Match> matches = new ArrayList<>();
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
                Match match = new Match();
                match.setId(rs.getString("id"));

                Championship champ = new Championship();
                champ.setId(rs.getString("championship_id"));
                match.setChampionship(champ);

                Club home = new Club(); home.setId(rs.getString("home_club_id"));
                Club away = new Club(); away.setId(rs.getString("away_club_id"));
                match.setHomeClubId(home);
                match.setAwayClubId(away);

                match.setStadium(rs.getString("stadium"));
                match.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
                match.setMatchStatus(MatchStatus.valueOf(rs.getString("match_status")));

                matches.add(match);
            }
            return matches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des matchs", e);
        }
    }

    public List<Match> generateMatchesForSeason(int seasonYear) {
        String seasonId = null;
        String championshipId = null;
        SeasonStatus seasonStatus = null;
        List<String> clubIds = new ArrayList<>();
        List<Match> createdMatches = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            // 1. Vérifier si la saison existe et son statut
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

            if (seasonStatus != SeasonStatus.STARTED) {
                throw new BadRequestException("La saison n'est pas au statut STARTED");
            }

            // 2. Vérifier si des matchs existent déjà pour cette saison
            PreparedStatement checkMatchStmt = conn.prepareStatement("""
                SELECT COUNT(*) FROM Match WHERE season_id = ?
            """);
            checkMatchStmt.setString(1, seasonId);
            ResultSet matchRs = checkMatchStmt.executeQuery();
            if (matchRs.next() && matchRs.getInt(1) > 0) {
                throw new BadRequestException("Les matchs ont déjà été générés pour cette saison");
            }

            // 3. Récupérer tous les clubs du championnat
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

            // 4. Créer les matchs aller-retour
            PreparedStatement matchStmt = conn.prepareStatement("""
                INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """);

            LocalDateTime matchTime = LocalDateTime.of(seasonYear, 8, 1, 20, 45);
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

                        matchTime = matchTime.plusDays(3);
                    }
                }
            }

            matchStmt.executeBatch();
            return createdMatches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la génération des matchs", e);
        }
    }

    public void save(Match match) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?::match_status)
            """);
            stmt.setString(1, match.getId());
            stmt.setString(2, match.getChampionship().getId());
            stmt.setString(3, match.getHomeClubId().getId());
            stmt.setString(4, match.getAwayClubId().getId());
            stmt.setString(5, match.getStadium());
            stmt.setTimestamp(6, Timestamp.valueOf(match.getDateTime()));
            stmt.setString(7, match.getSeason().getId());
            stmt.setString(8, match.getMatchStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du match", e);
        }
    }

    public Optional<Match> findById(String id) {
        String sql = "SELECT * FROM match WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Match match = mapResultSetToMatch(rs);
                match.setGoals(goalRepository.findByMatchId(match.getId()));
                return Optional.of(match);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du match avec id=" + id, e);
        }
    }

    public boolean existsMatchForSeason(String seasonId) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM match WHERE season_id = ?");
            stmt.setString(1, seasonId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification des matchs existants", e);
        }
    }

    public SeasonData getSeasonDataByYear(int seasonYear) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, championship_id, season_status FROM season WHERE start_year = ?
            """);
            stmt.setInt(1, seasonYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SeasonData(
                        rs.getString("id"),
                        rs.getString("championship_id"),
                        SeasonStatus.valueOf(rs.getString("season_status"))
                );
            }
            throw new RuntimeException("Saison introuvable pour l'année : " + seasonYear);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des données de saison", e);
        }
    }

    public Map<String, String> getClubsByChampionship(String championshipId) {
        try (Connection conn = db.getConnection()) {
            Map<String, String> clubs = new LinkedHashMap<>();
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT id, stadium_name FROM club WHERE championship_id = ?
            """);
            stmt.setString(1, championshipId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clubs.put(rs.getString("id"), rs.getString("stadium_name"));
            }
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des clubs", e);
        }
    }

    public List<Match> findAllBySeasonYear(int seasonYear) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM match WHERE season_id IN (
                    SELECT id FROM season WHERE start_year = ?
                )
            """);
            stmt.setInt(1, seasonYear);
            ResultSet rs = stmt.executeQuery();

            List<Match> matches = new ArrayList<>();
            while (rs.next()) {
                Match match = mapResultSetToMatch(rs);
                match.setGoals(goalRepository.findByMatchId(match.getId()));
                matches.add(match);
            }
            return matches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des matchs pour l'année " + seasonYear, e);
        }
    }


    public List<Match> findByFilters(int seasonYear, String matchStatus, String clubPlayingId, LocalDate after, LocalDate before) {
        List<Match> matches = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT * FROM match WHERE season_id IN (
            SELECT id FROM season WHERE start_year = ?)
        """);

        if (matchStatus != null) sql.append(" AND match_status = ?::match_status");
        if (clubPlayingId != null) sql.append(" AND (home_club_id = ? OR away_club_id = ?)");
        if (after != null) sql.append(" AND date_time > ?");
        if (before != null) sql.append(" AND date_time <= ?");

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            stmt.setInt(index++, seasonYear);
            if (matchStatus != null) stmt.setString(index++, matchStatus);
            if (clubPlayingId != null) {
                stmt.setString(index++, clubPlayingId);
                stmt.setString(index++, clubPlayingId);
            }
            if (after != null) stmt.setTimestamp(index++, Timestamp.valueOf(after.atStartOfDay()));
            if (before != null) stmt.setTimestamp(index++, Timestamp.valueOf(before.atTime(23, 59)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Match match = mapResultSetToMatch(rs);
                match.setGoals(goalRepository.findByMatchId(match.getId()));
                matches.add(match);
            }
            return matches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du filtrage des matchs", e);
        }
    }

    public void update(Match match) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                UPDATE match SET match_status = ?::match_status WHERE id = ?
            """);
            stmt.setString(1, match.getMatchStatus().name());
            stmt.setString(2, match.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut du match", e);
        }
    }

    // --- Méthodes d’aide privées ---
    private Match mapResultSetToMatch(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setId(rs.getString("id"));
        match.setChampionship(new Championship(rs.getString("championship_id")));
        match.setHomeClubId(new Club(rs.getString("home_club_id")));
        match.setAwayClubId(new Club(rs.getString("away_club_id")));
        match.setStadium(rs.getString("stadium"));
        match.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());

        Season season = new Season();
        season.setId(rs.getString("season_id"));
        match.setSeason(season);

        match.setMatchStatus(MatchStatus.valueOf(rs.getString("match_status")));

        return match;
    }

    public record SeasonData(String id, String championshipId, SeasonStatus status) {}
}
