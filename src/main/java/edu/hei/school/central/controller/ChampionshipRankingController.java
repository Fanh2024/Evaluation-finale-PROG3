package edu.hei.school.central.controller;

import edu.hei.school.central.dto.ChampionshipRankingDTO;
import edu.hei.school.central.service.ChampionshipRankingService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
public class ChampionshipRankingController {

    private final ChampionshipRankingService rankingService;

    public ChampionshipRankingController(ChampionshipRankingService rankingService) {
        this.rankingService = rankingService;
    }

    // Ex: /championshipRankings?seasonYear=2024
    @GetMapping("/championshipRankings")
    public List<ChampionshipRankingDTO> getRanking(@RequestParam int seasonYear) throws SQLException {
        return rankingService.getRankingByGoalDifferenceMedian(seasonYear);
    }
}
