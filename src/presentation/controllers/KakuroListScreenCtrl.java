package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.domain.entities.Difficulty;
import src.presentation.screens.KakuroListScreen;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class KakuroListScreenCtrl extends AbstractScreenCtrl {

    private ArrayList<ArrayList<Map<String, Object>>> infoToDisplay;

    public KakuroListScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        infoToDisplay = new ArrayList<>();
    }

    public void setSelectedTab(String difficulty) {
        ((KakuroListScreen)screen).setSelectedTab(difficulty);
    }

    @Override
    // Called at setScreen right after a call to onDestroy of the previous screen.
    public void build(int width, int height) {
        screen = new KakuroListScreen(this);
        for(Difficulty diff : Difficulty.values()) {
            Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getKakuroListByDifficulty(diff.name());
            if (result.second != null) {
                // TODO: handle error
                return;
            }

            infoToDisplay.add(result.first);
        }
        super.build(width, height);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        for(Difficulty diff : Difficulty.values()) {
            Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getKakuroListByDifficulty(diff.name());
            if (result.second != null) {
                // TODO: handle error
                return;
            }

            infoToDisplay.add(result.first);
        }
        screen.build(width, height);
    }

    public ArrayList<Map<String, Object>> getInfoToDisplay(Difficulty diff) {
        return infoToDisplay.get(diff.ordinal());
    }

    public void onExportKakuroClicked(String id) {
        System.out.println("Export: " + id);
    }

    public void onPlayKakuroClicked(String id) {
        presentationCtrl.startNewGame(id);
    }
}
