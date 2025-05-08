package edu.hei.school.evaluation.service;

import edu.hei.school.evaluation.model.Club;
import edu.hei.school.evaluation.model.Player;
import edu.hei.school.evaluation.repository.PlayerRepository;

import java.util.List;
import java.util.Optional;

import edu.hei.school.evaluation.repository.TransfertRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final TransfertRepository transfertRepository;

    public PlayerService(PlayerRepository repository, TransfertRepository transfertRepository) {
        this.repository = repository;
        this.transfertRepository = transfertRepository;
    }

    public List<Player> getPlayers(String name, Integer ageMinimum, Integer ageMaximum, String clubName) {
        return repository.findAllByFilters(name, ageMinimum, ageMaximum, clubName);
    }

    public List<Player> saveOrUpdatePlayers(List<Player> players) {
        for (Player player : players) {
            // Récupérer l'état précédent du joueur avant mise à jour (avant PUT)
            Optional<Player> beforeUpdate = repository.findByIdTransfert(player.getId());

            Club oldClub = beforeUpdate.map(Player::getClub).orElse(null);
            Club newClub = player.getClub();

            String oldClubId = (oldClub != null) ? oldClub.getId() : null;
            String newClubId = (newClub != null) ? newClub.getId() : null;

            // Vérifier si le joueur quitte un club (oldClub != null mais le club devient null)
            if (oldClubId != null && newClubId == null) {
                // Transfert OUT : Le joueur quitte son club
                transfertRepository.saveTransfert(player, oldClub, "OUT");
            }
            // Vérifier si le joueur rejoint un nouveau club (newClub != null et oldClubId != newClubId)
            if (newClubId != null && !newClubId.equals(oldClubId)) {
                // Transfert IN : Le joueur rejoint un nouveau club
                transfertRepository.saveTransfert(player, newClub, "IN");
            }
        }

        // Sauvegarde ou mise à jour des joueurs
        return repository.saveOrUpdateAll(players);
    }
}
