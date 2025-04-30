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
        String[] parts = seasonYear.split("-");
        if (parts.length != 2) {
            return ResponseEntity.badRequest().build();
        }

        int startYear, endYear;
        try {
            startYear = Integer.parseInt(parts[0].trim());
            endYear = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        PlayerStatistics playerStatistics = playerRepository.getPlayerStatistics(id, startYear, endYear);
        if (playerStatistics != null) {
            return ResponseEntity.ok(playerStatistics);
        } else {
            return ResponseEntity.status(404).build(); // Statistiques non trouvées
        }
    }

}
