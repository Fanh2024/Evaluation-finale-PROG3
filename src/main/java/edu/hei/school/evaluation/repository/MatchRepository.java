package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MatchRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();

    public List<Match> generateMatchesForSeason(int seasonYear) {
        String seasonId = null;
        String championshipId = null;
        SeasonStatus seasonStatus = null;
        List<String> clubIds = new ArrayList<>();
        List<Match> createdMatches = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            // 1. Vérifier si la saison existe et son statut
            PreparedStatement seasonStmt = conn.prepareStatement("""
                SELECT id, championship_id, season_status FROM Season WHERE start_year = ?
            """);
            seasonStmt.setInt(1, seasonYear);
            ResultSet seasonRs = seasonStmt.executeQuery();
            if (seasonRs.next()) {
                seasonId = seasonRs.getString("id");
                championshipId = seasonRs.getString("championship_id");
                seasonStatus = SeasonStatus.valueOf(seasonRs.getString("season_status"));
            } else {
                throw new NotFoundException("Saison introuvable pour " + seasonYear);
            }

            if (seasonStatus != SeasonStatus.STARTED) {
                throw new BadRequestException("La saison n'est pas au statut STARTED");
            }

            // 2. Vérifier si des matchs existent déjà pour cette saison
            PreparedStatement checkMatchStmt = conn.prepareStatement("""
                SELECT COUNT(*) FROM Match WHERE season_id = ?
            """);
            checkMatchStmt.setString(1, seasonId);
            ResultSet matchRs = checkMatchStmt.executeQuery();
            if (matchRs.next() && matchRs.getInt(1) > 0) {
                throw new BadRequestException("Les matchs ont déjà été générés pour cette saison");
            }

            // 3. Récupérer tous les clubs du championnat
            PreparedStatement clubsStmt = conn.prepareStatement("""
                SELECT id, stadium_name FROM Club WHERE championship_id = ?
            """);
            clubsStmt.setString(1, championshipId);
            ResultSet clubsRs = clubsStmt.executeQuery();
            Map<String, String> clubStadiums = new HashMap<>();
            while (clubsRs.next()) {
                String clubId = clubsRs.getString("id");
                String stadium = clubsRs.getString("stadium_name");
                clubIds.add(clubId);
                clubStadiums.put(clubId, stadium);
            }

            if (clubIds.size() < 2) {
                throw new RuntimeException("Pas assez de clubs pour générer des matchs");
            }

            // 4. Créer les matchs aller-retour
            PreparedStatement matchStmt = conn.prepareStatement("""
                INSERT INTO Match (id, championship_id, home_club_id, away_club_id, stadium, date_time, season_id, match_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """);

            LocalDateTime matchTime = LocalDateTime.of(seasonYear, 8, 1, 20, 45);
            for (int i = 0; i < clubIds.size(); i++) {
                for (int j = 0; j < clubIds.size(); j++) {
                    if (i != j) {
                        String homeClub = clubIds.get(i);
                        String awayClub = clubIds.get(j);
                        String matchId = "MATCH_" + UUID.randomUUID();

                        matchStmt.setString(1, matchId);
                        matchStmt.setString(2, championshipId);
                        matchStmt.setString(3, homeClub);
                        matchStmt.setString(4, awayClub);
                        matchStmt.setString(5, clubStadiums.get(homeClub));
                        matchStmt.setTimestamp(6, Timestamp.valueOf(matchTime));
                        matchStmt.setString(7, seasonId);
                        matchStmt.setString(8, MatchStatus.SCHEDULED.name());

                        matchStmt.addBatch();

                        Match match = new Match();
                        match.setId(matchId);
                        match.setChampionship(new Championship());
                        match.getChampionship().setId(championshipId);
                        Club home = new Club(); home.setId(homeClub);
                        Club away = new Club(); away.setId(awayClub);
                        match.setHomeClubId(home);
                        match.setAwayClubId(away);
                        match.setStadium(clubStadiums.get(homeClub));
                        match.setDateTime(matchTime);
                        match.setMatchStatus(MatchStatus.SCHEDULED);

                        createdMatches.add(match);

                        matchTime = matchTime.plusDays(3);
                    }
                }
            }

            matchStmt.executeBatch();
            return createdMatches;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la génération des matchs", e);
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) {
            super(msg);
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) {
            super(msg);
        }
    }
}

/*
Parfait, tu peux maintenant tester la route `/matchMaker/{seasonYear}` [POST] avec tes données existantes. Voici ce que tu dois savoir et faire :

---

### ✅ **Cas possible 1 — Succès (200 OK)**

Si :
- la saison `SEASON_24_25_LIGA` existe,
- son `season_status` est `STARTED`,
- **aucun match n'existe déjà** dans la table `Match` pour cette saison,

👉 alors la route génèrera automatiquement tous les matchs aller-retour entre les clubs du championnat concerné (comme dans ton exemple : RMA, FCB...).

---

### ❌ **Cas possible 2 — Saison introuvable (404 NOT_FOUND)**

Si tu appelles `/matchMaker/2026` mais qu’il n’y a **aucune saison avec `start_year = 2026`** dans la table `Season`, tu obtiendras :

```json
{
  "message": "Saison introuvable pour 2026"
}
```

---

### ❌ **Cas possible 3 — Mauvais statut (400 BAD_REQUEST)**

Si la saison `SEASON_24_25_LIGA` a un `season_status` qui est **`NOT_STARTED` ou `FINISHED`**, tu obtiendras :

```json
{
  "message": "La saison n'est pas au statut STARTED"
}
```

---

### ❌ **Cas possible 4 — Matchs déjà générés (400 BAD_REQUEST)**

Si des lignes `Match` existent déjà pour la `season_id = 'SEASON_24_25_LIGA'`, tu obtiendras :

```json
{
  "message": "Les matchs ont déjà été générés pour cette saison"
}
```

⚠️ Dans ton cas actuel, puisque tu as déjà inséré 2 matchs dans la table pour la saison `SEASON_24_25_LIGA`, **l’appel retournera 400 BAD_REQUEST** car la condition suivante est vraie :

```sql
SELECT COUNT(*) FROM Match WHERE season_id = 'SEASON_24_25_LIGA';
```

renverra `2`.

---

### ✅ Pour tester un cas succès, tu peux faire l’un des deux :

1. **Changer le `season_id` des deux matchs insérés** pour une autre saison fictive (ex: `SEASON_OLD`).
2. **Supprimer les deux matchs** avec une requête :

```sql
DELETE FROM Match WHERE season_id = 'SEASON_24_25_LIGA';
```

---

Souhaites-tu que je t’aide à ajouter le contrôleur REST `/matchMaker/{seasonYear}` maintenant pour déclencher cette logique via HTTP ?
 */
