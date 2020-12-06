package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;

import javax.swing.*;

public abstract class AbstractScreen { //UI related stuff
    protected AbstractScreenCtrl ctrl;
    protected JPanel contents;

    private int previousWidth, previousHeight;

    public AbstractScreen(AbstractScreenCtrl ctrl) {
        this.ctrl = ctrl;
        previousWidth = 0;
        previousHeight = 0;
    }

    public void build(int width, int height) {  // Called at setScreen right after a call to onDestroy of the previous screen.
        previousWidth = width;
        previousHeight = height;
    }
    public abstract void onShow();    // Called whenever the window regains focus.
    public abstract void onHide();    // Called whenever the window loses focus (helpful for pausing timers, etc.).
    public abstract void onDestroy(); // Called whenever a new screen is requested or the window is closed.
    public void onResize(int width, int height) { // Called whenever the window is resized.
        if (width != previousWidth || height != previousHeight) {
            build(width, height);
            previousWidth = width;
            previousHeight = height;
        }
    }
    public JPanel getContents() { return contents == null ? new JPanel() : contents; } // Called at setScreen, right after build().
}
