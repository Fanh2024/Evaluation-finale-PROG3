package edu.hei.school.evaluation.dto;

import edu.hei.school.evaluation.model.MatchStatus;

public class MatchStatusUpdateDto {
    private MatchStatus matchStatus;

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }
}