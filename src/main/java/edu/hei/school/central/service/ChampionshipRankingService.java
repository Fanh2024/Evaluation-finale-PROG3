package edu.hei.school.central.service;

import edu.hei.school.central.dto.ChampionshipRankingDTO;
import edu.hei.school.central.model.Championship;
import edu.hei.school.central.model.ChampionshipStats;
import edu.hei.school.central.repository.ChampionshipRepository;
import edu.hei.school.central.repository.StatisticsClubRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChampionshipRankingService {

    private final StatisticsClubRepository statisticsClubRepository;
    private final ChampionshipRepository championshipRepository;

    public ChampionshipRankingService(StatisticsClubRepository statisticsClubRepository,
                                      ChampionshipRepository championshipRepository) {
        this.statisticsClubRepository = statisticsClubRepository;
        this.championshipRepository = championshipRepository;
    }

    public List<ChampionshipRankingDTO> getRankingByGoalDifferenceMedian(int seasonYear) throws SQLException {
        Map<String, List<Integer>> diffsByChampionship = statisticsClubRepository.getGoalDifferencesByChampionship(seasonYear);

        // Ajoute un log ici pour vérifier les données récupérées
        System.out.println("Diffs by Championship: " + diffsByChampionship);

        List<ChampionshipRankingDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : diffsByChampionship.entrySet()) {
            ChampionshipStats stats = new ChampionshipStats();
            stats.setChampionshipId(entry.getKey());
            stats.setGoalDifferences(entry.getValue());

            double median = stats.computeMedian();

            Championship champ = championshipRepository.findById(entry.getKey()); // Vérifie si ça trouve bien le championnat
            if (champ != null) {
                ChampionshipRankingDTO dto = new ChampionshipRankingDTO();
                dto.setChampionship(champ);
                dto.setDifferenceGoalsMedian(median);
                result.add(dto);
            }
        }

        System.out.println("Result: " + result);  // Log du résultat final

        // Trie par médiane décroissante
        result.sort(Comparator.comparingDouble(ChampionshipRankingDTO::getDifferenceGoalsMedian).reversed());

        // Ajout des rangs
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        return result;
    }

}