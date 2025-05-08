package edu.hei.school.central.service;

import edu.hei.school.central.model.ChampionshipRankingResponse;
import edu.hei.school.central.repository.ChampionshipRankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChampionshipRankingService {
    private final ChampionshipRankingRepository repository;

    public ChampionshipRankingService(ChampionshipRankingRepository repository) {
        this.repository = repository;
    }

    public List<ChampionshipRankingResponse> getRankings() {
        return repository.getRankings();
    }
}
