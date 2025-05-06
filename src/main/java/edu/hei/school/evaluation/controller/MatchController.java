
package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.dto.GoalDto;
import edu.hei.school.evaluation.dto.MatchStatusUpdateDto;
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

    @PostMapping("/matchMaker/{seasonYear}")
    public ResponseEntity<Void> generateMatches(@PathVariable int seasonYear) {
        matchService.findAllBySeasonYear(seasonYear);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/matches/{seasonYear}")
    public ResponseEntity<List<Match>> getMatchesForSeason(
            @PathVariable int seasonYear,
            @RequestParam(required = false) String matchStatus,
            @RequestParam(required = false) String clubPlayingName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchBeforeOrEquals
    ) {
        List<Match> matches = matchService.findAllBySeasonYear(
                seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
        return ResponseEntity.ok(matches);
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
