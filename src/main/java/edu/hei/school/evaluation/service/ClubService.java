package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Club;
import edu.hei.school.evaluation.model.Player;
import edu.hei.school.evaluation.repository.ClubRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {
    private final ClubRepository clubRepository;

    public ClubService() {
        this.clubRepository = new ClubRepository();
    }

    public List<Club> getAllClubs() {
        return clubRepository.getAllClubs();
    }

    public void upsertClub(Club club) {
        clubRepository.upsertClub(club);
    }

    public List<Player> getPlayersByClubId(String clubId) {
        return clubRepository.getPlayersByClubId(clubId);
    }

    public void addPlayerToClub(String clubId, Player player) {
        player.setClub(new Club(clubId, null, null, 0, null, null)); // Associe uniquement l'id
        clubRepository.insertPlayer(player);
    }

    public void replacePlayersInClub(String clubId, List<Player> players) {
        clubRepository.deletePlayersByClubId(clubId);
        for (Player player : players) {
            player.setClub(new Club(clubId, null, null, 0, null, null));
            clubRepository.insertPlayer(player);
        }
    }
}
