package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.Goal;
import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.model.Player;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoalRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();

    public void save(Goal goal) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO goal (id, match_id, player_id, minute, is_penalty, is_own_goal)
                VALUES (?, ?, ?, ?, ?, ?)
            """);
            stmt.setString(1, goal.getId());
            stmt.setString(2, goal.getMatch().getId());
            stmt.setString(3, goal.getPlayer().getId());
            stmt.setInt(4, goal.getMinute());
            stmt.setBoolean(5, goal.isPenalty());
            stmt.setBoolean(6, goal.isOwnGoal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion du but", e);
        }
    }

    public List<Goal> findByMatchId(String matchId) {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM goal WHERE match_id = ?
            """);
            stmt.setString(1, matchId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des buts", e);
        }
        return goals;
    }

    public void deleteByMatchId(String matchId) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM goal WHERE match_id = ?
            """);
            stmt.setString(1, matchId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression des buts du match " + matchId, e);
        }
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setId(rs.getString("id"));

        Match match = new Match();
        match.setId(rs.getString("match_id"));
        goal.setMatch(match);

        Player player = new Player();
        player.setId(rs.getString("player_id"));
        goal.setPlayer(player);

        goal.setMinute(rs.getInt("minute"));
        goal.setPenalty(rs.getBoolean("is_penalty"));
        goal.setOwnGoal(rs.getBoolean("is_own_goal"));

        return goal;
    }
}