/*
package edu.hei.school.central.service;

import edu.hei.school.central.model.Club;
import edu.hei.school.central.model.ClubStatistics;
import edu.hei.school.central.model.Player;
import edu.hei.school.central.model.PlayerStatistics;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SyncService {

    private final RestTemplate restTemplate;

    public SyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void synchronizeData() {
        String baseUrl = "http://localhost:8080"; // port de ton autre projet

        // 1. Récupérer les clubs
        ResponseEntity<Club[]> clubResponse = restTemplate.getForEntity(baseUrl + "/clubs", Club[].class);
        Club[] clubs = clubResponse.getBody();

        // 2. Récupérer les joueurs
        ResponseEntity<Player[]> playerResponse = restTemplate.getForEntity(baseUrl + "/players", Player[].class);
        Player[] players = playerResponse.getBody();

        /*
        // 3. Pour chaque club, récupérer ses statistiques (saison fixe, ex : 2024)
        for (Club club : clubs) {
            String url = baseUrl + "/clubs/statistics/2024?clubId=" + club.getId();
            ClubStatistics stats = restTemplate.getForObject(url, ClubStatistics.class);
            // enregistrer dans la base centrale
        }
         */

/*// 4. Pour chaque joueur, récupérer ses statistiques

import edu.hei.school.central.model.Player;
import edu.hei.school.central.model.PlayerStatistics;for (Player player : players) {
String url = baseUrl + "/players/" + player.getId() + "/statistics/2024";
PlayerStatistics stats = restTemplate.getForObject(url, PlayerStatistics.class);
// enregistrer dans la base centrale
        }
                }
                }

 */