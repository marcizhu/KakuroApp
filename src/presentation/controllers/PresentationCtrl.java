package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.presentation.screens.AbstractScreenCtrl;
import src.presentation.screens.DemoScreen;
import src.presentation.screens.AbstractScreen;
import src.presentation.screens.DemoScreenCtrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PresentationCtrl {
    private final int defaultWidth = 1200;
    private final int defaultHeight = 720;
    private final int windowBarHeight = 40;

    // Domain controller
    private DomainCtrl domainCtrl;

    // Window frame
    private JFrame app;

    // Window menu
    private JMenuBar menu;

    // App content
    // The current one to handle events
    private AbstractScreenCtrl currentScreenCtrl;
    // List of all screen controllers to handle screen switching
    private DemoScreenCtrl demoScreenCtrl;

    public PresentationCtrl() {
        // Initialize JFrame;
        app = new JFrame();
        // Initialize the general domain Controller
        domainCtrl = new DomainCtrl();
        // Initialize screen controllers
        demoScreenCtrl = new DemoScreenCtrl(this, domainCtrl);
    }

    public void initializePresentationCtrl() {
        // Define the initial screen
        currentScreenCtrl = demoScreenCtrl;
        //Window related event listeners
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                currentScreenCtrl.onDestroy();
                super.windowClosing(e);
            }
        });
        app.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                currentScreenCtrl.onShow();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                currentScreenCtrl.onHide();
            }
        });
        app.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentScreenCtrl.onResize(app.getWidth(), app.getHeight()-2*windowBarHeight);
            }
        });

        // Dimensions and position
        app.setSize(defaultWidth, defaultHeight+windowBarHeight); // default size
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int xLocation = (int) ((dimension.getWidth() - app.getWidth()) / 2);
        int yLocation = (int) ((dimension.getHeight() - app.getHeight()) / 2);
        app.setLocation(xLocation, yLocation);

        // Contents
        // TODO: When initializing we should set the screen to LogInScreenCtrl, which hasn't got any menu bar
        //  the menu bar is left here in purpose to showcase it at the demo.
        buildMenuBar();
        app.setJMenuBar(menu);
        currentScreenCtrl.build(app.getWidth(), app.getHeight()-2*windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());

        // Make it visible
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

    private void buildMenuBar() {
        menu = new JMenuBar();
        menu.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        addMenuButton(menu, constraints, "DASHBARD", 0, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onDashboardMenuItemClicked();
            }
        });
        addMenuButton(menu, constraints, "KAKURO LIST", 1, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onKakuroListMenuItemClicked();
            }
        });
        addMenuButton(menu, constraints, "MY KAKUROS", 2, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMyKakurosMenuItemClicked();
            }
        });
        addMenuButton(menu, constraints, "STATISTICS", 3, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onStatisticsMenuItemClicked();
            }
        });
        addMenuButton(menu, constraints, "RANKINGS", 4, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onRankingsMenuItemClicked();
            }
        });

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0,0,0,0));
        constraints.gridx = 5;
        menu.add(separator, constraints);

        addMenuButton(menu, constraints, "LOG OUT", 6, 2, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLogOutMenuItemClicked();
            }
        });
        menu.getComponent(6).setForeground(Color.RED);
    }

    private void addMenuButton(JMenuBar menuBar, GridBagConstraints constraints, String text, int gridx, int weightx, MouseAdapter listener) {
        JButton item = new JButton(text);
        item.addMouseListener(listener);
        item.setHorizontalAlignment(SwingConstants.CENTER);
        item.setVerticalAlignment(SwingConstants.CENTER);
        item.setFocusable(false);
        constraints.gridx = gridx;
        constraints.weightx = weightx;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuBar.add(item, constraints);
    }

    private void onDashboardMenuItemClicked() {
        System.out.println("DASHBOARD");
        currentScreenCtrl.onDashboardMenuItemClicked();
        for (int i = 0; i < 5; i++) menu.getComponent(i).setForeground(Color.BLACK);
        menu.getComponent(0).setForeground(Color.BLUE);
        //setScreen(dashboardScreenCtrl);
    }
    private void onKakuroListMenuItemClicked() {
        System.out.println("KAKURO LIST");
        currentScreenCtrl.onKakuroListMenuItemClicked();
        for (int i = 0; i < 5; i++) menu.getComponent(i).setForeground(Color.BLACK);
        menu.getComponent(1).setForeground(Color.BLUE);
        //setScreen(kakuroListScreenCtrl);
    }
    private void onMyKakurosMenuItemClicked() {
        System.out.println("MY KAKUROS");
        currentScreenCtrl.onMyKakurosMenuItemClicked();
        for (int i = 0; i < 5; i++) menu.getComponent(i).setForeground(Color.BLACK);
        menu.getComponent(2).setForeground(Color.BLUE);
        //setScreen(myKakurosScreenCtrl);
    }
    private void onStatisticsMenuItemClicked() {
        System.out.println("STATISTICS");
        currentScreenCtrl.onStatisticsMenuItemClicked();
        for (int i = 0; i < 5; i++) menu.getComponent(i).setForeground(Color.BLACK);
        menu.getComponent(3).setForeground(Color.BLUE);
        //setScreen(statisticsScreenCtrl);
    }
    private void onRankingsMenuItemClicked() {
        System.out.println("RANKINGS");
        currentScreenCtrl.onRankingsMenuItemClicked();
        for (int i = 0; i < 5; i++) menu.getComponent(i).setForeground(Color.BLACK);
        menu.getComponent(4).setForeground(Color.BLUE);
        //setScreen(rankingsScreenCtrl);
    }
    private void onLogOutMenuItemClicked() {
        System.out.println("LOG OUT");
        currentScreenCtrl.onLogOutMenuItemClicked();
        // This one behaves differently because the login screen doesn't have the menu bar and we have to
        // warn domain that the user is logging out, etc.
    }

    // Screen management
    public void setScreen(AbstractScreenCtrl nextScreen) {
        currentScreenCtrl.onDestroy();
        currentScreenCtrl = nextScreen;
        if (!currentScreenCtrl.hasBeenBuilt()) currentScreenCtrl.build(app.getWidth(), app.getHeight()-2*windowBarHeight);
        else currentScreenCtrl.onFocusRegained();
        app.setContentPane(currentScreenCtrl.getContents());
    }

    // Domain communication
    public boolean logIn(String name) {
        // Call to domain controller, etc.
        return false;
    }
}
