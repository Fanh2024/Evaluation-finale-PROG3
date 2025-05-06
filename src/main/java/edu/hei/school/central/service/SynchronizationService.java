package edu.hei.school.central.service;

import edu.hei.school.central.model.*;
import edu.hei.school.central.repository.SynchronizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SynchronizationService {

    private final RestTemplate restTemplate;
    private final SynchronizationRepository repository;

    public SynchronizationService(RestTemplate restTemplate, SynchronizationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public SynchronizationResult synchronize() {
        System.out.println("Début de la synchronisation...");

        String baseUrl = "http://localhost:8080";
        int currentSeason = 2024;

        Club[] clubsArray = restTemplate.getForObject(baseUrl + "/clubs", Club[].class);
        List<Club> clubs = Arrays.asList(Objects.requireNonNull(clubsArray));
        repository.saveClubs(clubs);

        Player[] playersArray = restTemplate.getForObject(baseUrl + "/players", Player[].class);
        List<Player> players = Arrays.asList(Objects.requireNonNull(playersArray));
        repository.savePlayers(players);

        List<StatisticsClub> statisticsClubs = new ArrayList<>();
        for (Club club : clubs) {
            StatisticsClub[] statsArray = restTemplate.getForObject(
                    baseUrl + "/clubs/statistics/" + currentSeason + "?clubId=" + club.getId(),
                    StatisticsClub[].class);
            if (statsArray != null) {
                statisticsClubs.addAll(Arrays.asList(statsArray));
            }
        }
        repository.saveStatisticsClubs(statisticsClubs, currentSeason);

        List<PlayerStatistics> playerStats = new ArrayList<>();
        for (Player player : players) {
            PlayerStatistics stats = restTemplate.getForObject(
                    baseUrl + "/players/" + player.getId() + "/statistics/" + currentSeason,
                    PlayerStatistics.class);
            playerStats.add(stats);
        }
        repository.savePlayerStatistics(playerStats);

        System.out.println("Synchronisation terminée !");
        return new SynchronizationResult(players, clubs, statisticsClubs, playerStats);
    }
}
