package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.AbstractScreen;

import javax.swing.*;

// Data handling and calls to Domain control for a certain screen
public abstract class AbstractScreenCtrl {
    protected PresentationCtrl presentationCtrl;
    protected DomainCtrl domainCtrl;
    protected AbstractScreen screen;

    private boolean beenBuilt;

    public AbstractScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        this.presentationCtrl = presentationCtrl;
        this.domainCtrl = domainCtrl;
        this.beenBuilt = false;
    }

    public boolean hasBeenBuilt() { return beenBuilt; }
    public void invalidate() { beenBuilt = false; }

    // Called at setScreen right after a call to onDestroy of the previous screen.
    public void build(int width, int height) {
        screen.build(width, height);
        beenBuilt = true;
    }

    // Called at setScreen, right after build().
    public JPanel getContents() {
        return screen.getContents();
    }

    // Called whenever the window regains focus.
    public void onShow() {
        screen.onShow();
    }

    // Called whenever the window loses focus (helpful for pausing timers, etc.).
    public void onHide() {
        screen.onHide();
    }

    // Called whenever a new screen is requested or the window is closed.
    public void onDestroy() {
        screen.onDestroy();
    }

    // Called whenever the window is resized.
    public void onResize(int width, int height) {
        screen.onResize(width, height);
    }

    public abstract void onFocusRegained(int width, int height);

    public void onDashboardMenuItemClicked() {}
    public void onKakuroListMenuItemClicked() {}
    public void onMyKakurosMenuItemClicked() {}
    public void onStatisticsMenuItemClicked() {}
    public void onRankingsMenuItemClicked() {}
    public void onLogOutMenuItemClicked() {}
}
