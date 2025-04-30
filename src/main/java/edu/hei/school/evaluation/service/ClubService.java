package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Club;
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
}
