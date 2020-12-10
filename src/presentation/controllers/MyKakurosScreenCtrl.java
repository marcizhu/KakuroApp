package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.MyKakurosScreen;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class MyKakurosScreenCtrl extends AbstractScreenCtrl {

    private ArrayList<Map<String, Object>> infoToDisplay;

    public MyKakurosScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        infoToDisplay = new ArrayList<>();
    }

    @Override
    public void build(int width, int height) { // Called at setScreen right after a call to onDestroy of the previous screen.
        screen = new MyKakurosScreen(this);
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getKakuroListByUser(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            // TODO: handle error
            return;
        }
        infoToDisplay = result.first;
        super.build(width, height);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        Pair<ArrayList<Map<String, Object>>, String> result = domainCtrl.getKakuroListByUser(presentationCtrl.getUserSessionId());
        if (result.second != null) {
            // TODO: handle error
            return;
        }
        ArrayList<Map<String, Object>> newInfo = result.first;
        if (infoToDisplay != newInfo) {
            return;
        }

        return false;
    }

    public ArrayList<Map<String, Object>> getInfoToDisplay() {
        return infoToDisplay;
    }

    public void onExportKakuroClicked(String id) {
        System.out.println("Export: " + id);
    }

    public void onPlayKakuroClicked(String id) {
        presentationCtrl.startNewGame(id);
    }
}
