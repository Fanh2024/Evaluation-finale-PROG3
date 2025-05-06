package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.*;
import edu.hei.school.evaluation.repository.StatisticsClubRepository;
import edu.hei.school.evaluation.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class StatisticsClubService {

    private final MatchRepository matchRepository;
    private final StatisticsClubRepository statisticsClubRepository;

    public StatisticsClubService(MatchRepository matchRepository, StatisticsClubRepository statisticsClubRepository) {
        this.matchRepository = matchRepository;
        this.statisticsClubRepository = statisticsClubRepository;
    }


    public List<StatisticsClub> computeAndSaveStatistics(int seasonYear, boolean hasToBeClassified) {
        List<Match> matches = matchRepository.findAllBySeasonYear(seasonYear);
        Map<String, StatisticsClub> statsMap = new HashMap<>();

        for (Match match : matches) {
            Club home = match.getHomeClubId();
            Club away = match.getAwayClubId();
            Season season = match.getSeason();

            statsMap.putIfAbsent(home.getId(), createEmptyStats(home, season));
            statsMap.putIfAbsent(away.getId(), createEmptyStats(away, season));

            StatisticsClub homeStats = statsMap.get(home.getId());
            StatisticsClub awayStats = statsMap.get(away.getId());

            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();

            homeStats.setGoalsScored(homeStats.getGoalsScored() + homeGoals);
            homeStats.setGoalsConceded(homeStats.getGoalsConceded() + awayGoals);

            awayStats.setGoalsScored(awayStats.getGoalsScored() + awayGoals);
            awayStats.setGoalsConceded(awayStats.getGoalsConceded() + homeGoals);

            if (homeGoals > awayGoals) {
                homeStats.setPoints(homeStats.getPoints() + 3);
            } else if (awayGoals > homeGoals) {
                awayStats.setPoints(awayStats.getPoints() + 3);
            } else {
                homeStats.setPoints(homeStats.getPoints() + 1);
                awayStats.setPoints(awayStats.getPoints() + 1);
            }

            if (awayGoals == 0) homeStats.setCleanSheets(homeStats.getCleanSheets() + 1);
            if (homeGoals == 0) awayStats.setCleanSheets(awayStats.getCleanSheets() + 1);
        }

        for (StatisticsClub s : statsMap.values()) {
            s.setGoalDifference(s.getGoalsScored() - s.getGoalsConceded());
        }

        List<StatisticsClub> finalStats = new ArrayList<>(statsMap.values());

        try {
            statisticsClubRepository.deleteAllBySeasonYear(seasonYear);
            statisticsClubRepository.saveAll(finalStats);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde des statistiques", e);
        }

        if (hasToBeClassified) {
            finalStats.sort(Comparator
                    .comparing(StatisticsClub::getPoints, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(StatisticsClub::getGoalDifference, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(StatisticsClub::getCleanSheets, Comparator.nullsLast(Comparator.reverseOrder())));
        } else {
            finalStats.sort(Comparator.comparing(c -> c.getClub().getName(), Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return finalStats;
    }


    private StatisticsClub createEmptyStats(Club club, Season season) {
        StatisticsClub stats = new StatisticsClub();
        stats.setId(UUID.randomUUID().toString());
        stats.setClub(club);
        stats.setSeason(season);
        stats.setPoints(0);
        stats.setGoalsScored(0);
        stats.setGoalsConceded(0);
        stats.setGoalDifference(0);
        stats.setCleanSheets(0);
        return stats;
    }
}