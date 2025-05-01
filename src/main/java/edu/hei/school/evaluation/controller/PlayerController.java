package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Player;
import edu.hei.school.evaluation.model.PlayerStatistics;
import edu.hei.school.evaluation.repository.PlayerRepository;
import edu.hei.school.evaluation.service.PlayerService;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    public PlayerController(PlayerService playerService, PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Player> getPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer ageMinimum,
            @RequestParam(required = false) Integer ageMaximum,
            @RequestParam(required = false) String clubName
    ) {
        return playerService.getPlayers(name, ageMinimum, ageMaximum, clubName);
    }

    @PutMapping
    public List<Player> createOrUpdatePlayers(@RequestBody List<Player> players) {
        return playerService.saveOrUpdatePlayers(players);
    }

    @GetMapping("/{id}/statistics/{seasonYear}")
    public ResponseEntity<PlayerStatistics> getPlayerStatistics(@PathVariable String id, @PathVariable String seasonYear) {
        PlayerStatistics playerStatistics = playerRepository.getPlayerStatistics(id, seasonYear);
        if (playerStatistics != null) {
            return ResponseEntity.ok(playerStatistics);
        } else {
            return ResponseEntity.status(404).body(null); // Statistiques non trouvées
        }
    }

    @PutMapping("/{id}/statistics/{seasonId}")
    public ResponseEntity<?> createOrUpdatePlayerStatistics(
            @PathVariable String id,
            @PathVariable String seasonId,
            @RequestBody PlayerStatistics statisticsInput
    ) {
        try {
            PlayerStatistics saved = playerRepository.saveOrUpdatePlayerStatistics(id, seasonId, statisticsInput);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }

}
