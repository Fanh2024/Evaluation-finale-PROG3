package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Season;
import edu.hei.school.evaluation.repository.SeasonRepository;

import java.util.List;

public class SeasonService {
    private final SeasonRepository seasonRepository;

    public SeasonService() {
        this.seasonRepository = new SeasonRepository();
    }

    public List<Season> getAllSeasons() {
        return seasonRepository.getAllSeasons();
    }

    public void createSeason(Season season) {
        seasonRepository.createSeason(season);
    }
}
