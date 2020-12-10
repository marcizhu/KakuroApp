package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.MyKakurosScreen;

import java.util.ArrayList;

public class MyKakurosScreenCtrl extends AbstractScreenCtrl {

    private ArrayList<ArrayList<String>> infoToDisplay;

    public MyKakurosScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
        infoToDisplay = new ArrayList<>();
    }

    @Override
    public void build(int width, int height) { // Called at setScreen right after a call to onDestroy of the previous screen.
        screen = new MyKakurosScreen(this);
        infoToDisplay = domainCtrl.getMyKakurosList(presentationCtrl.getUserSessionId());
        super.build(width, height);
    }

    @Override
    public void onFocusRegained(int width, int height) {
        ArrayList<ArrayList<String>> newInfo = domainCtrl.getMyKakurosList(presentationCtrl.getUserSessionId());
        if (!infoToDisplayChanged(newInfo)) return;
        screen.build(width, height);
    }

    private boolean infoToDisplayChanged(ArrayList<ArrayList<String>> newInfo) {
        if (infoToDisplay == null || newInfo.size() != infoToDisplay.size()) return true;
        int size = newInfo.size();
        for (int i = 0; i < size; i++) {
            if (newInfo.get(i).size() != infoToDisplay.get(i).size()) return true;
            int numOfArgs = newInfo.get(i).size();
            for (int j = 0; j < numOfArgs; j++) {
                if (!newInfo.get(i).get(j).equals(infoToDisplay.get(i).get(j))) return true;
            }
        }

        return false;
    }

    public ArrayList<ArrayList<String>> getInfoToDisplay() {
        return infoToDisplay;
    }

    public void onExportKakuroClicked(String id) {
        System.out.println("Export: " + id);
    }

    public void onPlayKakuroClicked(String id) {
        presentationCtrl.startNewGame(id);
    }
}
