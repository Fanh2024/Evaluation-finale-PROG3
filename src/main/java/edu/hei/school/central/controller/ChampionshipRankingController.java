package edu.hei.school.central.controller;

import edu.hei.school.central.model.ChampionshipRankingResponse;
import edu.hei.school.central.service.ChampionshipRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChampionshipRankingController {
    private final ChampionshipRankingService service;

    public ChampionshipRankingController(ChampionshipRankingService service) {
        this.service = service;
    }

    @GetMapping("/championshipRankings")
    public List<ChampionshipRankingResponse> getRankings() {
        return service.getRankings();
    }
}
