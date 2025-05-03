package edu.hei.school.central.service;

import edu.hei.school.central.model.PlayerStatistics;
import edu.hei.school.central.repository.BestPlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BestPlayerService {
    private final BestPlayerRepository repository;

    public BestPlayerService(BestPlayerRepository repository) {
        this.repository = repository;
    }

    public List<PlayerStatistics> getBestPlayers(int top, String unit) {
        int minimumMinutes = switch (unit) {
            case "HOUR" -> 600;      // 10h
            case "SECOND" -> 30000;  // 8h30
            default -> 500;          // 500 minutes par défaut
        };

        List<PlayerStatistics> all = repository.getAllPlayerStatisticsForLastSeason();

        return all.stream()
                .filter(p -> p.getMinutesPlayed() >= minimumMinutes)
                .sorted(Comparator.comparingInt(PlayerStatistics::getGoals).reversed())
                .limit(top)
                .toList();
    }

    public String toJson(List<PlayerStatistics> players) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < players.size(); i++) {
            var p = players.get(i);
            json.append("""
                {
                  "playerId": "%s",
                  "playerName": "%s",
                  "goals": %d,
                  "minutesPlayed": %d
                }
                """.formatted(
                    p.getPlayer().getId(),
                    p.getPlayer().getName(),
                    p.getGoals(),
                    p.getMinutesPlayed()
            ));
            if (i < players.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}
