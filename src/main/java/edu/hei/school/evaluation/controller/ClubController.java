package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Club;
import edu.hei.school.evaluation.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
public class ClubController {
    private final ClubService clubService;

    public ClubController() {
        this.clubService = new ClubService();
    }

    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @PutMapping
    public ResponseEntity<Void> upsertClub(@RequestBody Club club) {
        clubService.upsertClub(club);
        return ResponseEntity.ok().build();
    }
}
