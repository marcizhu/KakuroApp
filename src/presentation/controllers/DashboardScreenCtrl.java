package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.DashboardScreen;

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
        System.out.println("Easy game");
    }

    public void onNewMediumGameClicked() {
        System.out.println("Medium game");
    }

    public void onNewHardGameClicked() {
        System.out.println("Hard game");
    }

    public void onNewExtremeGameClicked() {
        System.out.println("Extreme game");
    }

    public void onImportGameClicked() {
        System.out.println("Import game");
    }

    public void onGenerateByParameters(int rows, int columns, String difficulty, boolean unique) {
        System.out.println("Generate... rows: " + rows + ", columns : " + columns + ", diff: " + difficulty + ", unique: " + unique);
    }

    public void onGenerateBySeed(String seed) {
        System.out.println("Generate... seed:" + seed);
    }

    public void onHandMadeClicked(int rows, int columns) {
        presentationCtrl.startNewCreation(rows, columns);
    }

    public void onImportCreationClicked() {
        System.out.println("Import creation");
    }

    @Override
    public void onFocusRegained(int width, int height) {

    }
}
