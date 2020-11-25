package src.presentation;

import src.presentation.screens.LoginScreen;
import src.presentation.screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PresentationCtrl {
    private JFrame app;
    private Screen currentScreen;

    public PresentationCtrl() {
        app = new JFrame();
        currentScreen = new LoginScreen(this);
    }

    public void initializePresentationCtrl() {
        //Window related event listeners
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                currentScreen.onDestroy();
                super.windowClosing(e);
            }
        });
        app.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                currentScreen.onShow();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                currentScreen.onHide();
            }
        });
        app.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentScreen.onResize(app.getWidth(), app.getHeight());
                super.componentResized(e);
            }
        });

        // Dimensions and position
        app.pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int xLocation = (int) ((dimension.getWidth() - app.getWidth()) / 2);
        int yLocation = (int) ((dimension.getHeight() - app.getHeight()) / 2);
        app.setLocation(xLocation, yLocation);

        // Contents
        currentScreen.build(app.getWidth(), app.getHeight());
        app.setContentPane(currentScreen.getContents());

        // Make it visible
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

    // Screen management
    public void setScreen(Screen nextScreen) {
        currentScreen.onDestroy();
        currentScreen = nextScreen;
        currentScreen.build(app.getWidth(), app.getHeight());
        app.setContentPane(currentScreen.getContents());
    }

    // Domain communication
    public boolean logIn(String name) {
        // Call to domain controller, etc.
        return false;
    }
}
