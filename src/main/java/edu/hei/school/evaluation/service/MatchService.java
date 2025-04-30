package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.repository.MatchRepository;

import java.util.List;

public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService() {
        this.matchRepository = new MatchRepository();
    }

    public List<Match> generateMatchesForSeason(int seasonYear) {
        return matchRepository.generateMatchesForSeason(seasonYear);
    }
}
