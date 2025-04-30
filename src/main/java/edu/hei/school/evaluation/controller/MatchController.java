package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/matches")
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

    @GetMapping("/{seasonYear}")
    public ResponseEntity<List<Match>> getMatchesForSeason(
            @PathVariable int seasonYear,
            @RequestParam(required = false) String matchStatus,
            @RequestParam(required = false) String clubPlayingName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchBeforeOrEquals
    ) {
        List<Match> matches = matchService.getMatchesForSeason(
                seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
        return ResponseEntity.ok(matches);
    }
}
