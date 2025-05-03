package edu.hei.school.central.service;

import edu.hei.school.central.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SynchronizationService {

    private final RestTemplate restTemplate;

    public SynchronizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SynchronizationResult synchronize() {
        System.out.println("Début de la synchronisation...");

        String baseUrl = "http://localhost:8080";
        int currentSeason = 2024;

        // 1. Récupérer tous les clubs
        Club[] clubsArray = restTemplate.getForObject(baseUrl + "/clubs", Club[].class);
        List<Club> clubs = Arrays.asList(Objects.requireNonNull(clubsArray));
        System.out.println("Clubs récupérés: " + clubs.size());

        // 2. Récupérer tous les joueurs
        Player[] playersArray = restTemplate.getForObject(baseUrl + "/players", Player[].class);
        List<Player> players = Arrays.asList(Objects.requireNonNull(playersArray));
        System.out.println("Joueurs récupérés: " + players.size());

        /*
        // 3. Statistiques des clubs par saison
        List<ClubStatistics> clubStats = new ArrayList<>();
        for (Club club : clubs) {
            ClubStatistics stats = restTemplate.getForObject(
                    baseUrl + "/clubs/statistics/" + currentSeason + "?clubId=" + club.getId(),
                    ClubStatistics.class);
            System.out.println("Stats club " + club.getName() + " : " + stats);
            clubStats.add(stats);
        }
         */

        // 4. Statistiques des joueurs par saison
        List<PlayerStatistics> playerStats = new ArrayList<>();
        for (Player player : players) {
            PlayerStatistics stats = restTemplate.getForObject(
                    baseUrl + "/players/" + player.getId() + "/statistics/" + currentSeason,
                    PlayerStatistics.class);
            System.out.println("Stats joueur " + player.getName() + " : " + stats);
            playerStats.add(stats);
        }

        System.out.println("Synchronisation terminée !");
        return new SynchronizationResult(clubs, players, /*clubStats,*/ playerStats);
    }


}
