package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.repository.MatchRepository;
import edu.hei.school.evaluation.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MatchController {
    private final MatchService matchService;

    public MatchController() {
        this.matchService = new MatchService();
    }

    // 1. POST /matchMaker/{seasonYear}
    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<Void> generateMatches(@PathVariable int seasonYear) {
        matchService.generateMatchesForSeason(seasonYear);
        return ResponseEntity.ok().build();
    }

    // 2. GET /matches/{seasonYear}
    @GetMapping("/matches/{seasonYear}")
    public ResponseEntity<?> getMatchesForSeason(
            @PathVariable int seasonYear,
            @RequestParam(required = false) String matchStatus,
            @RequestParam(required = false) String clubPlayingName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchBeforeOrEquals
    ) {
        try {
            List<Match> matches = matchService.getMatchesForSeason(
                    seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
            if (matches.isEmpty()) {
                return ResponseEntity.status(404).body("Aucun match trouvé pour cette saison avec les filtres fournis.");
            }
            return ResponseEntity.ok(matches);
        } catch (MatchRepository.NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }
}
