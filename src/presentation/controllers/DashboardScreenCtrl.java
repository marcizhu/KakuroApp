package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.DashboardScreen;
import src.presentation.utils.Dialogs;
import src.utils.Pair;

import javax.swing.*;

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
            presentationCtrl.importNewGame(file);
        }
    }

    public void onGenerateByParameters(int rows, int columns, String difficulty, boolean unique) {
        if (rows > 25 || columns > 25) {
            if (!Dialogs.showYesNoOptionDialog("Warning: Kakuros of large dimensions might not be rendered properly on screen.", "Continue?"))
                return;
        }
        presentationCtrl.generateKakuroFromParameters(rows, columns, difficulty, unique);
    }

    public void onGenerateBySeed(String seed) {
        presentationCtrl.generateKakuroFromSeed(seed);
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

    @Override
    public void onFocusRegained(int width, int height) {

    }
}
