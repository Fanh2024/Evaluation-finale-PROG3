package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Player;
import edu.hei.school.evaluation.repository.PlayerRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<Player> getPlayers(String name, Integer ageMinimum, Integer ageMaximum, String clubName) {
        return repository.findAllByFilters(name, ageMinimum, ageMaximum, clubName);
    }

    public List<Player> saveOrUpdatePlayers(List<Player> players) {
        return repository.saveOrUpdateAll(players);
    }
}
