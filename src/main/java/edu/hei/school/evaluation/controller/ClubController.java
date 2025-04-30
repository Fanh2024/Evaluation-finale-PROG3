package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Club;
import edu.hei.school.evaluation.model.Player;
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

    // GET /clubs/{id}/players
    @GetMapping("/{id}/players")
    public ResponseEntity<List<Player>> getPlayersByClubId(@PathVariable String id) {
        return ResponseEntity.ok(clubService.getPlayersByClubId(id));
    }

    // POST /clubs/{id}/players : ajouter un joueur dans le club
    @PostMapping("/{id}/players")
    public ResponseEntity<Void> addPlayerToClub(@PathVariable String id, @RequestBody Player player) {
        clubService.addPlayerToClub(id, player);
        return ResponseEntity.ok().build();
    }

    // PUT /clubs/{id}/players : remplacer tous les joueurs du club
    @PutMapping("/{id}/players")
    public ResponseEntity<Void> replacePlayersInClub(@PathVariable String id, @RequestBody List<Player> players) {
        clubService.replacePlayersInClub(id, players);
        return ResponseEntity.ok().build();
    }
}
