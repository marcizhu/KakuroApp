package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.StatisticsScreen;
import src.presentation.utils.Dialogs;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsScreenCtrl extends AbstractScreenCtrl {

    private int easyGamesPlayed;
    private int mediumGamesPlayed;
    private int hardGamesPlayed;
    private int extremeGamesPlayed;

    private String easyAvgTime;
    private String mediumAvgTime;
    private String hardAvgTime;
    private String extremeAvgTime;

    public StatisticsScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        easyGamesPlayed = mediumGamesPlayed = hardGamesPlayed = extremeGamesPlayed = -1;
        easyAvgTime = "";
        mediumAvgTime = "";
        hardAvgTime = "";
        extremeAvgTime = "";
    }

    @Override
    public void build(int width, int height) { // Called at setScreen right after a call to onDestroy of the previous screen.
        screen = new StatisticsScreen(this);
        super.build(width, height);
    }

    public String interestPlayer() { return presentationCtrl.getUserSessionId(); }

    public ArrayList<Pair<Integer, Pair<String, String>>> getTopRanking() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByPoints();
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new ArrayList<>();
        }
        ArrayList<Pair<Integer, Pair<String, String>>> topRanks = new ArrayList<>();
        boolean interestFound = false;
        for (int i = 0; i < 3 && i < result.first.size(); i++) {
            if (result.first.get(i).get("name").equals(presentationCtrl.getUserSessionId())) interestFound = true;
            topRanks.add(new Pair( i, new Pair<>(
                    (String) result.first.get(i).get("name"),
                    pointsToString((Float) result.first.get(i).get("totalPts"))
            )));
        }
        if (!interestFound) {
            for (int i = 3; i < result.first.size(); i++) {
                if (result.first.get(i).get("name").equals(presentationCtrl.getUserSessionId())) {
                    topRanks.add(new Pair( i, new Pair<>(
                            (String) result.first.get(i).get("name"),
                            pointsToString((Float) result.first.get(i).get("totalPts"))
                    )));
                }
            }
        }
        return topRanks;
    }

    public ArrayList<Pair<Integer, Pair<String, String>>> getTopRankingByDifficulty(String difficulty) {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getRankingByTimeInDifficulty(difficulty);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new ArrayList<>();
        }
        ArrayList<Pair<Integer, Pair<String, String>>> topRanks = new ArrayList<>();
        boolean interestFound = false;
        for (int i = 0; i < 3 && i < result.first.size(); i++) {
            if (result.first.get(i).get("name").equals(presentationCtrl.getUserSessionId())) interestFound = true;
            String time = secondsToStringTime((float) result.first.get(i).get("avgTime"));
            topRanks.add(new Pair( i, new Pair<>(
                    (String) result.first.get(i).get("name"),
                    time
            )));
            setAvgTimeDiff(difficulty, time);
        }
        for (int i = 3; !interestFound && i < result.first.size(); i++) {
            if (result.first.get(i).get("name").equals(presentationCtrl.getUserSessionId())) {
                interestFound = true;
                String time = secondsToStringTime((float) result.first.get(i).get("avgTime"));
                topRanks.add(new Pair( i, new Pair<>(
                        (String) result.first.get(i).get("name"),
                        time
                )));
                setAvgTimeDiff(difficulty, time);
            }
        }
        return topRanks;
    }

    private void setAvgTimeDiff(String difficulty, String time) {
        if (difficulty.equals("EASY")) easyAvgTime = time;
        else if (difficulty.equals("MEDIUM")) mediumAvgTime = time;
        else if (difficulty.equals("HARD")) hardAvgTime = time;
        else if (difficulty.equals("EXTREME")) extremeAvgTime = time;
    }
    public String getAvgTime(String difficulty) {
        if (difficulty.equals("EASY")) return easyAvgTime;
        else if (difficulty.equals("MEDIUM"))return  mediumAvgTime;
        else if (difficulty.equals("HARD")) return hardAvgTime;
        else if (difficulty.equals("EXTREME")) return extremeAvgTime;
        return "";
    }

    private String pointsToString(float points) {
        String pointsStr;
        if(Math.floor(points) == Math.ceil(points)) {
            // Number is integer. Remove decimals or display "---" if zero
            int val = Math.round(points);
            pointsStr = (val == 0 ? "---" : "" + val);
        } else {
            // round to 2 decimal places
            pointsStr = (Math.round(points * 100.0f) / 100.0f) + " pts";
        }
        return pointsStr;
    }

    private String secondsToStringTime(float time) {
        if(Float.isNaN(time)) return "---";

        int hours = (int)time / 3600;
        int minutes = (int)time / 60 - hours * 60;
        int seconds = (int)time - minutes * 60 - hours * 3600;
        String timeStr = "";
        if (hours > 0) {
            timeStr += hours + ":";
            if (minutes < 10) timeStr += "0";
        }

        timeStr += minutes + String.format(":%02d", seconds);

        return timeStr;
    }

    public Map<String, Object> getTopPointer() {
        Pair<Map<String, Object>, String> result = domainCtrl.getTopPointer(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new HashMap<>();
        }
        if (result.first.size() != 0) result.first.put("score", pointsToString((float) result.first.get("score")));
        return result.first;
    }
    public Map<String, Object> getTopPointerInDifficulty(String difficulty) {
        Pair<Map<String, Object>, String> result = domainCtrl.getTopPointerInDifficulty(presentationCtrl.getUserSessionId(), difficulty.toUpperCase());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new HashMap<>();
        }
        if (result.first.size() != 0) result.first.put("score", pointsToString((float) result.first.get("score")));
        return result.first;
    }

    public Map<String, Integer> getGamesPlayed() {
        Pair<Map<String, Integer>, String> result = domainCtrl.getNumberOfGamesPlayed(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new HashMap<>();
        }
        easyGamesPlayed = result.first.get("easy");
        mediumGamesPlayed = result.first.get("medium");
        hardGamesPlayed = result.first.get("hard");
        extremeGamesPlayed = result.first.get("extreme");

        return result.first;
    }

    public int getEasyGamesPlayed() {
        if (easyGamesPlayed == -1) getGamesPlayed();
        return easyGamesPlayed;
    }
    public int getMediumGamesPlayed() {
        if (mediumGamesPlayed == -1) getGamesPlayed();
        return mediumGamesPlayed;
    }
    public int getHardGamesPlayed() {
        if (hardGamesPlayed == -1) getGamesPlayed();
        return hardGamesPlayed;
    }
    public int getExtremeGamesPlayed() {
        if (extremeGamesPlayed == -1) getGamesPlayed();
        return extremeGamesPlayed;
    }

    @Override
    public void onFocusRegained(int width, int height) {
        super.build(width, height);
    }
}