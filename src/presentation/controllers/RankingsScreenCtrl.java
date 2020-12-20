package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.RankingsScreen;

import java.util.ArrayList;
import java.util.Map;

public class RankingsScreenCtrl extends AbstractScreenCtrl {

    public RankingsScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public String getUser() {
        return presentationCtrl.getUserSessionId();
    }

    public ArrayList<Map<String, Object>>  getRankingByPoints() {
        return domainCtrl.getRankingByPoints();
    }

    public ArrayList<Map<String, Object>>  getRankingByGames() {
        return domainCtrl.getRankingByGamesPlayed();
    }

    public ArrayList<Map<String, Object>>  getRankingByTimeEasy() {
        return domainCtrl.getRankingByTimeInDifficulty("EASY");
    }

    public ArrayList<Map<String, Object>>  getRankingByTimeMedium() {
        return domainCtrl.getRankingByTimeInDifficulty("MEDIUM");
    }

    public ArrayList<Map<String, Object>>  getRankingByTimeHard() {
        return domainCtrl.getRankingByTimeInDifficulty("HARD");
    }

    public ArrayList<Map<String, Object>>  getRankingByTimeExtreme() {
        return domainCtrl.getRankingByTimeInDifficulty("EXTREME");
    }


    @Override
    public void build(int width, int height) {
        screen = new RankingsScreen(this);
        super.build(width, height);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        screen.build(width, height);
    }
}
