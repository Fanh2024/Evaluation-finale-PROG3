package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Match;
import edu.hei.school.evaluation.model.MatchLight;
import edu.hei.school.evaluation.repository.MatchRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de gestion des matchs d'une saison.
 */
public class MatchService {
    private final MatchRepository matchRepository;

    /**
     * Constructeur du service de match.
     */
    public MatchService() {
        this.matchRepository = new MatchRepository();
    }

    /**
     * Génère tous les matchs aller-retour d'une saison donnée si la saison est au statut STARTED
     * et qu'aucun match n'a encore été généré.
     *
     * @param seasonYear Année de début de la saison
     * @return Liste des matchs générés
     */
    public List<Match> generateMatchesForSeason(int seasonYear) {
        return matchRepository.generateMatchesForSeason(seasonYear);
    }

    /**
     * Récupère les matchs d'une saison en fonction de plusieurs filtres : statut, club, date.
     *
     * @param seasonYear          Année de début de la saison
     * @param matchStatus         Statut du match (ex: SCHEDULED, FINISHED)
     * @param clubPlayingName     Nom du club jouant à domicile ou extérieur
     * @param matchAfter          Date après laquelle le match doit être programmé (exclusif)
     * @param matchBeforeOrEquals Date jusqu'à laquelle le match doit être programmé (inclusif)
     * @return Liste des matchs correspondant aux critères
     */
    public List<MatchLight> getMatchesForSeason(
            int seasonYear,
            String matchStatus,
            String clubPlayingName,
            LocalDate matchAfter,
            LocalDate matchBeforeOrEquals
    ) {
        return matchRepository.getMatchesForSeason(
                seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
    }
}