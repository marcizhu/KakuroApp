package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;

import javax.swing.*;

// UI related stuff
public abstract class AbstractScreen {
    protected AbstractScreenCtrl ctrl;
    protected JPanel contents;

    private int previousWidth;
    private int previousHeight;

    public AbstractScreen(AbstractScreenCtrl ctrl) {
        this.ctrl = ctrl;
        this.previousWidth = 0;
        this.previousHeight = 0;
    }

    // Called at setScreen right after a call to onDestroy of the previous screen
    public void build(int width, int height) {
        previousWidth = width;
        previousHeight = height;
    }

    public abstract void onShow();    // Called whenever the window regains focus.
    public abstract void onHide();    // Called whenever the window loses focus (helpful for pausing timers, etc.).
    public abstract void onDestroy(); // Called whenever a new screen is requested or the window is closed.

    // Called whenever the window is resized
    public void onResize(int width, int height) {
        if (width == previousWidth && height == previousHeight) return;

        if (contents == null) build(width, height);
        else {
            contents.setSize(width, height);
            contents.revalidate();
        }

        previousWidth = width;
        previousHeight = height;
    }

    // Called at setScreen, right after build()
    public JPanel getContents() { return contents == null ? new JPanel() : contents; }
}
