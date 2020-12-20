package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.domain.controllers.GameplayCtrl;
import src.domain.controllers.KakuroCreationCtrl;
import src.presentation.utils.Dialogs;
import src.presentation.utils.Palette;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PresentationCtrl {
    // Misc settings
    private static final int defaultWidth = 1200;
    private static final int defaultHeight = 720;
    private static final int windowBarHeight = 40;

    private String userSessionId;

    // Domain controller
    private final DomainCtrl domainCtrl;

    // Window frame
    private final JFrame app;

    // Window menu
    private JMenuBar menu;

    // App content
    private AbstractScreenCtrl currentScreenCtrl;          ///< The current screen controller to handle events

    private final LoginScreenCtrl loginScreenCtrl;           ///< "Login" Screen controller
    private final DashboardScreenCtrl dashboardScreenCtrl;   ///< "Dashboard" Screen controller
    private final MyKakurosScreenCtrl myKakurosScreenCtrl;   ///< "My Kakuros" Screen controller
    private final KakuroListScreenCtrl kakuroListScreenCtrl; ///< "Kakuro List" Screen controller

    // FIXME: temporary
    private final DisplayKakuroScreenCtrl statisticsScreenCtrl;
    private final DisplayKakuroScreenCtrl rankingsScreenCtrl;

    // List of all screen controllers to handle screen switching
    //private DemoScreenCtrl demoScreenCtrl;

    // ScreenCtrl ids
    public static final int DASHBOARD = 1;
    public static final int KAKURO_LIST = 2;
    public static final int MY_KAKUROS = 3;
    public static final int STATISTICS = 4;
    public static final int RANKINGS = 5;

    public PresentationCtrl() {
        // Initialize JFrame;
        app = new JFrame();

        // Initialize the general domain Controller
        domainCtrl = new DomainCtrl();

        // Initialize screen controllers
        loginScreenCtrl = new LoginScreenCtrl(this, domainCtrl);
        dashboardScreenCtrl = new DashboardScreenCtrl(this, domainCtrl);
        myKakurosScreenCtrl = new MyKakurosScreenCtrl(this, domainCtrl);
        kakuroListScreenCtrl = new KakuroListScreenCtrl(this, domainCtrl);

        // FIXME: temporary
        statisticsScreenCtrl = rankingsScreenCtrl = new DisplayKakuroScreenCtrl(this, domainCtrl);
    }

    public void initializePresentationCtrl() {
        // Define the initial screen
        currentScreenCtrl = loginScreenCtrl;
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

        addMenuButton(menu, constraints, "DASHBOARD", 0, 2, e -> onDashboardMenuItemClicked());
        addMenuButton(menu, constraints, "KAKURO LIST", 1, 2, e -> onKakuroListMenuItemClicked());
        addMenuButton(menu, constraints, "MY KAKUROS", 2, 2, e -> onMyKakurosMenuItemClicked());
        addMenuButton(menu, constraints, "STATISTICS", 3, 2, e -> onStatisticsMenuItemClicked());
        addMenuButton(menu, constraints, "RANKINGS", 4, 2, e -> onRankingsMenuItemClicked());

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0,0,0,0));
        constraints.gridx = 5;
        menu.add(separator, constraints);

        addMenuButton(menu, constraints, "LOG OUT", 6, 2, e -> onLogOutMenuItemClicked());

        menu.getComponent(6).setForeground(Palette.PastelRed);
    }

    private void addMenuButton(JMenuBar menuBar, GridBagConstraints constraints, String text, int gridx, int weightx, ActionListener listener) {
        JButton item = new JButton(text);
        item.addActionListener(listener);
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
        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        menu.getComponent(0).setForeground(Color.BLUE);
        setScreen(dashboardScreenCtrl);
    }

    private void onKakuroListMenuItemClicked() {
        System.out.println("KAKURO LIST");
        currentScreenCtrl.onKakuroListMenuItemClicked();
        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        menu.getComponent(1).setForeground(Color.BLUE);
        setScreen(kakuroListScreenCtrl);
    }

    private void onMyKakurosMenuItemClicked() {
        System.out.println("MY KAKUROS");
        currentScreenCtrl.onMyKakurosMenuItemClicked();
        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        menu.getComponent(2).setForeground(Color.BLUE);
        setScreen(myKakurosScreenCtrl);
    }

    private void onStatisticsMenuItemClicked() {
        System.out.println("STATISTICS");
        currentScreenCtrl.onStatisticsMenuItemClicked();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        menu.getComponent(3).setForeground(Color.BLUE);
        //setScreen(statisticsScreenCtrl);
    }

    private void onRankingsMenuItemClicked() {
        System.out.println("RANKINGS");
        currentScreenCtrl.onRankingsMenuItemClicked();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        menu.getComponent(4).setForeground(Color.BLUE);
        //setScreen(rankingsScreenCtrl);
    }

    private void onLogOutMenuItemClicked() {
        System.out.println("LOG OUT");
        currentScreenCtrl.onLogOutMenuItemClicked();
        // This one behaves differently because the login screen doesn't have the menu bar and we have to
        // warn domain that the user is logging out, etc.
        int dialogResult = JOptionPane.showConfirmDialog(
                null,
                "Do you want to log out?",
                "Warning: Log out",
                JOptionPane.YES_NO_OPTION);

        if(dialogResult == JOptionPane.YES_OPTION){
            logOut();
        }
    }

    // Screen management
    public void setScreen(AbstractScreenCtrl nextScreen) {
        currentScreenCtrl.onDestroy();
        currentScreenCtrl = nextScreen;
        if (!currentScreenCtrl.hasBeenBuilt()) currentScreenCtrl.build(app.getWidth(), app.getHeight()-2*windowBarHeight);
        else currentScreenCtrl.onFocusRegained(app.getWidth(), app.getHeight()-2*windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());
        app.revalidate();
    }

    public AbstractScreenCtrl getScreenCtrl(int screenID) {
        switch (screenID) {
            case DASHBOARD:
                return dashboardScreenCtrl;
            case KAKURO_LIST:
                return kakuroListScreenCtrl;
            case MY_KAKUROS:
                return myKakurosScreenCtrl;
            case STATISTICS:
                return statisticsScreenCtrl;
            case RANKINGS:
                return rankingsScreenCtrl;
        }
        // Should never happen
        return loginScreenCtrl;
    }

    // Domain communication
    public boolean logIn(String name) {
        Pair<Boolean, String> result = domainCtrl.loginUser(name);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, result.second);
            return false;
        }
        if (result.first) { // user exists
            userSessionId = name;
            buildMenuBar();
            app.setJMenuBar(menu);
            setScreen(dashboardScreenCtrl);
            return true;
        }

        // user does not exist, this should not happen with the current UI since users are listed
        return false;
    }

    public void logOut() {
        userSessionId = "";
        app.setJMenuBar(null);
        setScreen(loginScreenCtrl);
    }

    public String getUserSessionId() {
        if (userSessionId == null) return "";
        return userSessionId;
    }

    public void startNewGame(String kakuroID) {
        currentScreenCtrl.onDestroy();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        Pair<GameplayCtrl, String> result = domainCtrl.newGameInstance(userSessionId, kakuroID);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, result.second);
            return;
        }

        currentScreenCtrl = new GameScreenCtrl(this, domainCtrl);
        ((GameScreenCtrl)currentScreenCtrl).setUpGame(result.first);
        currentScreenCtrl.build(app.getWidth(), app.getHeight() - 2 * windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());
        app.revalidate();
    }

    public void importNewGame(String name, String filePath) {
        Pair<GameplayCtrl, String> result = domainCtrl.newImportedGameInstance(userSessionId, filePath, name);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, result.second);
            return;
        }

        currentScreenCtrl.onDestroy();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        currentScreenCtrl = new GameScreenCtrl(this, domainCtrl);
        ((GameScreenCtrl)currentScreenCtrl).setUpGame(result.first);
        currentScreenCtrl.build(app.getWidth(), app.getHeight() - 2 * windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());
        app.revalidate();
    }

    public void startNewCreation(int numRows, int numCols) {
        currentScreenCtrl.onDestroy();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        Pair<KakuroCreationCtrl, String> result = domainCtrl.newCreatorInstance(userSessionId, numRows, numCols);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, result.second);
            return;
        }

        currentScreenCtrl = new CreatorScreenCtrl(this, domainCtrl);
        ((CreatorScreenCtrl)currentScreenCtrl).setUpCreator(result.first);
        currentScreenCtrl.build(app.getWidth(), app.getHeight() - 2 * windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());
        app.revalidate();
    }

    public void importNewCreation(String filePath) {
        Pair<KakuroCreationCtrl, String> result = domainCtrl.newImportedCreatorInstance(userSessionId, filePath);
        if (result.second != null) {
            Dialogs.showErrorDialog(result.second, result.second);
            return;
        }

        currentScreenCtrl.onDestroy();

        for (int i = 0; i < 5; i++)
            menu.getComponent(i).setForeground(Color.BLACK);

        currentScreenCtrl = new CreatorScreenCtrl(this, domainCtrl);
        ((CreatorScreenCtrl)currentScreenCtrl).setUpCreator(result.first);
        currentScreenCtrl.build(app.getWidth(), app.getHeight() - 2 * windowBarHeight);
        app.setContentPane(currentScreenCtrl.getContents());
        app.revalidate();
    }

    public void generateKakuroFromParameters(String name, int rows, int columns, String difficulty, boolean forceUnique) {
        // TODO, might throw an error if name is already in use
        domainCtrl.generateKakuroFromParameters(rows, columns, difficulty, forceUnique, name);
    }

    public void generateKakuroFromSeed(String name, String seed) {
        // TODO, might throw an error if name is already in use
        domainCtrl.generateKakuroFromSeed(seed, name);
    }

    public DisplayKakuroScreenCtrl getNewDisplayKakuroScreenCtrlInstance() {
        return new DisplayKakuroScreenCtrl(this, domainCtrl);
    }
}
