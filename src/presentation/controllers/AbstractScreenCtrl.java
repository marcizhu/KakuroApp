package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.controllers.PresentationCtrl;
import src.presentation.screens.AbstractScreen;

import javax.swing.*;

public abstract class AbstractScreenCtrl { //Data handling and calls to Domain control for a certain screen
    protected PresentationCtrl presentationCtrl;
    protected DomainCtrl domainCtrl;
    protected AbstractScreen screen;

    private boolean beenBuilt;

    public AbstractScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        this.presentationCtrl = presentationCtrl;
        this.domainCtrl = domainCtrl;
        beenBuilt = false;
    }

    public boolean hasBeenBuilt() { return beenBuilt; }
    public void build(int width, int height) { // Called at setScreen right after a call to onDestroy of the previous screen.
        screen.build(width, height);
        beenBuilt = true;
    }
    public JPanel getContents() { return screen.getContents(); } // Called at setScreen, right after build().
    public void onShow() { screen.onShow(); }   // Called whenever the window regains focus.
    public void onHide() { screen.onHide(); }   // Called whenever the window loses focus (helpful for pausing timers, etc.).
    public void onDestroy() { screen.onDestroy(); } // Called whenever a new screen is requested or the window is closed.
    public void onResize(int width, int height) { screen.onResize(width, height); }// Called whenever the window is resized.

    public abstract void onFocusRegained(int width, int height);

    public void onDashboardMenuItemClicked() {}
    public void onKakuroListMenuItemClicked() {}
    public void onMyKakurosMenuItemClicked() {}
    public void onStatisticsMenuItemClicked() {}
    public void onRankingsMenuItemClicked() {}
    public void onLogOutMenuItemClicked() {}
}
