package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Season;
import edu.hei.school.evaluation.service.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seasons")
public class SeasonController {
    private final SeasonService seasonService;

    public SeasonController() {
        this.seasonService = new SeasonService();
    }

    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @PostMapping
    public ResponseEntity<Void> createSeason(@RequestBody Season season) {
        seasonService.createSeason(season);
        return ResponseEntity.ok().build();
    }
}