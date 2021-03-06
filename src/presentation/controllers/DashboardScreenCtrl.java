package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.DashboardScreen;
import src.presentation.utils.Dialogs;
import src.utils.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardScreenCtrl extends AbstractScreenCtrl{

    public DashboardScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    @Override
    public void build(int width, int height) { // Called at setScreen right after a call to onDestroy of the previous screen.
        screen = new DashboardScreen(this);
        super.build(width, height);
    }

    public void onNewEasyGameClicked() {
        presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST));
        ((KakuroListScreenCtrl)presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST)).setSelectedTab("EASY");
    }

    public void onNewMediumGameClicked() {
        presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST));
        ((KakuroListScreenCtrl)presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST)).setSelectedTab("MEDIUM");
    }

    public void onNewHardGameClicked() {
        presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST));
        ((KakuroListScreenCtrl)presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST)).setSelectedTab("HARD");
    }

    public void onNewExtremeGameClicked() {
        presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST));
        ((KakuroListScreenCtrl)presentationCtrl.getScreenCtrl(PresentationCtrl.KAKURO_LIST)).setSelectedTab("EXTREME");
    }

    public void onImportGameClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import kakuro for new game");

        int userSelection = fileChooser.showOpenDialog(getContents());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            String name = Dialogs.showStringInputDialog("Please enter a name for the kakuro that you are importing.");
            if (name == null) return;
            if (name.equals("")) Dialogs.showErrorDialog("A kakuro can not be nameless", "Invalid name");
            else presentationCtrl.importNewGame(name, file);
        }
    }

    public void onGenerateByParameters(int rows, int columns, String difficulty, boolean unique) {
        if (rows > 25 || columns > 25) {
            if (!Dialogs.showYesNoOptionDialog("Warning: Kakuros of large dimensions might not be rendered properly on screen.", "Continue?"))
                return;
        }
        String name = Dialogs.showStringInputDialog("Please enter a name for the kakuro that is about to be generated.");
        if (name == null) return;
        if (name.equals("")) Dialogs.showErrorDialog("A kakuro can not be nameless", "Invalid name");
        else presentationCtrl.generateKakuroFromParameters(name, rows, columns, difficulty.toUpperCase(), unique);
    }

    public void onGenerateBySeed(String seed) {
        String name = Dialogs.showStringInputDialog("Please enter a name for the kakuro that is about to be generated.");
        if (name == null) return;
        if (name.equals("")) Dialogs.showErrorDialog("A kakuro can not be nameless", "Invalid name");
        else presentationCtrl.generateKakuroFromSeed(name, seed);
    }

    public void onHandMadeClicked(int rows, int columns) {
        if (rows > 25 || columns > 25) {
            if (!Dialogs.showYesNoOptionDialog("Warning: Kakuros of large dimensions might not be rendered properly on screen.", "Continue?"))
                return;
        }
        presentationCtrl.startNewCreation(rows, columns);
    }

    public void onImportCreationClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import kakuro creation");

        int userSelection = fileChooser.showOpenDialog(getContents());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            presentationCtrl.importNewCreation(file);
        }
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
                    pointsToString(Math.round((Float) result.first.get(i).get("totalPts")))
            )));
        }
        if (!interestFound) {
            for (int i = 3; i < result.first.size(); i++) {
                if (result.first.get(i).get("name").equals(presentationCtrl.getUserSessionId())) {
                    topRanks.add(new Pair( i, new Pair<>(
                            (String) result.first.get(i).get("name"),
                            pointsToString(Math.round((Float) result.first.get(i).get("totalPts")))
                    )));
                }
            }
        }
        return topRanks;
    }

    private String pointsToString(int points) {
        if (points == 0) return "---";
        return points + " pts";
    }

    public Map<String, Object> getTopPointer() {
        Pair<Map<String, Object>, String> result = domainCtrl.getTopPointer(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new HashMap<>();
        }
        if (result.first.size() != 0) result.first.put("score", pointsToString(Math.round((float) result.first.get("score"))));
        return result.first;
    }

    public Map<String, Integer> getGamesPlayed() {
        Pair<Map<String, Integer>, String> result = domainCtrl.getNumberOfGamesPlayed(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new HashMap<>();
        }
        return result.first;
    }

    public ArrayList<Map<String, Object>> getHistory() {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getGameHistory(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, "Something went wrong!");
            return new ArrayList<>();
        }
        return result.first;
    }

    public void onResumeFromHistory(String kakuroID) {
        presentationCtrl.startNewGame(kakuroID);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        ((DashboardScreen)screen).updateHistoryPanel();
        ((DashboardScreen)screen).updateQuickStats();
    }
}
