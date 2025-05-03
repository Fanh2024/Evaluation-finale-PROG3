package edu.hei.school.central.controller;

import edu.hei.school.central.service.BestPlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
public class BestPlayerController {
    private final BestPlayerService service;

    public BestPlayerController(BestPlayerService service) {
        this.service = service;
    }

    @GetMapping("/bestPlayers")
    public String getBestPlayers(
            @RequestParam(defaultValue = "3") int top,
            @RequestParam(defaultValue = "MINUTE") String playingTimeUnit
    ) {
        // return service.getBestPlayers(top, playingTimeUnit);
        // ou
        var best = service.getBestPlayers(top, playingTimeUnit);
        return service.toJson(best);
    }
}
