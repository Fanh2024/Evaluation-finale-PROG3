package edu.hei.school.central.model;

import java.util.List;

public class SynchronizationResult {
    public List<Club> clubs;
    public List<Player> players;
    public List<StatisticsClub> statisticsClubs;
    public List<PlayerStatistics> playerStatistics;

    public SynchronizationResult(List<Player> players, List<Club> clubs, List<StatisticsClub> statisticsClubs, List<PlayerStatistics> playerStats) {
        this.players = players;
        this.clubs = clubs;
        this.statisticsClubs = statisticsClubs;
        this.playerStatistics = playerStats;
    }


    // Getter

    public List<Club> getClubs() {
        return clubs;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<StatisticsClub> getStatisticsClubs() {
        return statisticsClubs;
    }

    public List<PlayerStatistics> getPlayerStatistics() {
        return playerStatistics;
    }
}