package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Player;
import edu.hei.school.evaluation.service.PlayerService;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
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
}
