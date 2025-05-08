
package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.dto.GoalDto;
import edu.hei.school.evaluation.dto.MatchStatusUpdateDto;
import edu.hei.school.evaluation.exception.NotFoundException;
import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MatchController {
    private final MatchService matchService;
    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // 1. POST /matchMaker/{seasonYear}
    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<Void> generateMatches(@PathVariable int seasonYear) {
        matchService.generateMatchesForSeason(seasonYear);
        return ResponseEntity.ok().build();
    }

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
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }

    @PutMapping("/matches/{id}/status")
    public ResponseEntity<Void> updateMatchStatus(
            @PathVariable String id,
            @RequestBody MatchStatusUpdateDto statusUpdate) {

        matchService.updateMatchStatus(id, String.valueOf(statusUpdate.getMatchStatus()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/matches/{id}/goal")
    public ResponseEntity<Void> addGoalToMatch(
            @PathVariable("id") String id,
            @RequestBody GoalDto goalDto) {

        goalDto.setMatchId(id);

        matchService.addGoal(goalDto);

        return ResponseEntity.ok().build();
    }
}
