package edu.hei.school.central.model;

import java.util.List;

public class SynchronizationResult {
    public List<Club> clubs;
    public List<Player> players;
    public List<ClubStatistics> clubStatistics;
    public List<PlayerStatistics> playerStatistics;

    public SynchronizationResult(List<Club> clubs, List<Player> players,
                                 List<PlayerStatistics> playerStatistics) {
        this.clubs = clubs;
        this.players = players;
        this.clubStatistics = clubStatistics;
        this.playerStatistics = playerStatistics;
    }

    // Getter

    public List<Club> getClubs() {
        return clubs;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<ClubStatistics> getClubStatistics() {
        return clubStatistics;
    }

    public List<PlayerStatistics> getPlayerStatistics() {
        return playerStatistics;
    }
}
