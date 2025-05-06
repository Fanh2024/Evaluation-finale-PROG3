
package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.dto.GoalDto;
import edu.hei.school.evaluation.model.*;
import edu.hei.school.evaluation.repository.GoalRepository;
import edu.hei.school.evaluation.repository.MatchRepository;
import edu.hei.school.evaluation.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final GoalRepository goalRepository;

    public MatchService(MatchRepository matchRepository, PlayerRepository playerRepository, GoalRepository goalRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.goalRepository = goalRepository;
    }

    public List<Match> findAllBySeasonYear(int seasonYear) {
        return matchRepository.findAllBySeasonYear(seasonYear);
    }

    public List<Match> findAllBySeasonYear(
            int seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDate matchAfter,
            LocalDate matchBeforeOrEquals
    ) {
        return matchRepository.findByFilters(
                seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
    }

    public void updateMatchStatus(String matchId, String newStatus) {
        // On récupère le match depuis la base
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match introuvable avec ID: " + matchId));

        try {
            // Conversion vers l'enum MatchStatus si nécessaire
            MatchStatus matchStatusEnum = MatchStatus.valueOf(newStatus.toUpperCase());

            // Mise à jour de l'état
            match.setMatchStatus(matchStatusEnum);
            matchRepository.update(match);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut invalide: " + newStatus);
        }
    }



    /**
     * Ajoute un but à un match donné avec validation
     */
    /*public void addGoal(String matchId, Goal goal) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        if (match.getMatchStatus() != MatchStatus.STARTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match must be STARTED to add goals");
        }

        if (goal.getPlayer() == null || goal.getPlayer().getClub() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Goal must have a valid player with a club");
        }

        String playerClubId = goal.getPlayer().getClub().getId();
        if (!playerClubId.equals(match.getHomeClubId()) && !playerClubId.equals(match.getAwayClubId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player must belong to one of the match clubs");
        }

        goal.setMatch(match);

        if (match.getGoals() == null) {
            match.setGoals(new ArrayList<>());
        }

        match.getGoals().add(goal);
        matchRepository.save(match);
    }*/

    /**
     * Retourne le score actuel du match basé sur les buts marqués
     */
    public String getScore(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        List<Goal> goals = match.getGoals() != null ? match.getGoals() : new ArrayList<>();

        long homeGoals = goals.stream()
                .filter(g -> g.getPlayer().getClub().getId().equals(match.getHomeClubId()) && !g.isOwnGoal())
                .count();

        long awayGoals = goals.stream()
                .filter(g -> g.getPlayer().getClub().getId().equals(match.getAwayClubId()) && !g.isOwnGoal())
                .count();
        long homeOwnGoals = goals.stream()
                .filter(g -> g.getPlayer().getClub().getId().equals(match.getHomeClubId()) && g.isOwnGoal())
                .count();

        long awayOwnGoals = goals.stream()
                .filter(g -> g.getPlayer().getClub().getId().equals(match.getAwayClubId()) && g.isOwnGoal())
                .count();

        long finalHomeGoals = homeGoals + awayOwnGoals;
        long finalAwayGoals = awayGoals + homeOwnGoals;

        return finalHomeGoals + " - " + finalAwayGoals;
    }

    public void addGoal(GoalDto goalDto) {
        Match match = matchRepository.findById(goalDto.getMatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        if (match.getMatchStatus() != MatchStatus.STARTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match must be STARTED to add goals");
        }

        Player player = playerRepository.findById(goalDto.getPlayerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        Goal goal = new Goal();
        goal.setId(goalDto.getId());
        goal.setPlayer(player);
        goal.setMatch(match);
        goal.setMinute(goalDto.getMinute());
        goal.setOwnGoal(goalDto.isOwnGoal());
        goal.setPenalty(goalDto.isPenalty());

        // On enregistre directement le but dans la table goal
        goalRepository.save(goal);
    }
    private void updateClubStatistics(Match match) {
        // tODO: mise à jour des statistiques des clubs
    }
}
