package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.StatisticsClub;
import edu.hei.school.evaluation.service.StatisticsClubService;
import edu.hei.school.evaluation.repository.MatchRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs/statistics")
public class StatisticsClubController {

    private final StatisticsClubService statisticsClubService;

    public StatisticsClubController(StatisticsClubService statisticsClubService) {
        this.statisticsClubService = statisticsClubService;
    }

    @GetMapping("/{seasonYear}")
    public List<StatisticsClub> getStatisticsBySeason(
            @PathVariable int seasonYear,
            @RequestParam(defaultValue = "false") boolean hasToBeClassified) {

        return statisticsClubService.computeAndSaveStatistics(seasonYear, hasToBeClassified);
    }
}