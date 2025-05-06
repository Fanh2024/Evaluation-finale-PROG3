
package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Repository
public class MatchRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();
    private final GoalRepository goalRepository;

    public MatchRepository(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
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
