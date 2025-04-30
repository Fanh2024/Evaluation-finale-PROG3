package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matchMaker")
public class MatchController {
    private final MatchService matchService;

    public MatchController() {
        this.matchService = new MatchService();
    }

    @PostMapping("/{seasonYear}")
    public ResponseEntity<Void> generateMatches(@PathVariable int seasonYear) {
        matchService.generateMatchesForSeason(seasonYear);
        return ResponseEntity.ok().build();
    }
}
