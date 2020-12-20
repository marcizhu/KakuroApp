package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.RankingsScreen;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class RankingsScreenCtrl extends AbstractScreenCtrl {

    public RankingsScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public String getUser() {
        return presentationCtrl.getUserSessionId();
    }

    public ArrayList<Map<String, Object>> getRankingByPoints() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByPoints();
        return result.first;
    }

    public ArrayList<Map<String, Object>> getRankingByGames() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByGamesPlayed();
        return result.first;
    }

    public ArrayList<Map<String, Object>> getRankingByTimeEasy() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByTimeInDifficulty("EASY");
        return result.first;
    }

    public ArrayList<Map<String, Object>> getRankingByTimeMedium() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByTimeInDifficulty("MEDIUM");
        return result.first;
    }

    public ArrayList<Map<String, Object>> getRankingByTimeHard() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByTimeInDifficulty("HARD");
        return result.first;
    }

    public ArrayList<Map<String, Object>> getRankingByTimeExtreme() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByTimeInDifficulty("EXTREME");
        return result.first;
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
