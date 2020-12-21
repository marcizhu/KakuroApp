package src.presentation.screens;

import src.presentation.controllers.DashboardScreenCtrl;
import src.presentation.utils.Dialogs;
import src.presentation.utils.Palette;
import src.presentation.views.BarChartView;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class DashboardScreen extends AbstractScreen {
    JPanel historyPanel;
    JScrollPane historyScroll;
    Component hHistoryFiller;

    JPanel quickStatsPanel;
    BarChartView playedChartPanel;

    JPanel interactiveActionsPanel;
    JPanel gamePanel;
    JPanel createPanel;
    JSpinner genRows, genCols;
    JComboBox<String> difficultyChooser;
    JCheckBox forceUnique;
    JTextField seed;

    JSpinner handRows, handCols;

    Font titleFnt;
    Font subtitleFnt;
    Font bodyFnt;
    Font smallFnt;


    public DashboardScreen(DashboardScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        contents = new JPanel();
        contents.setLayout(new BorderLayout());

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.X_AXIS));

        titleFnt = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        subtitleFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        bodyFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        smallFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 9);

        interactiveActionsPanel = new JPanel();
        interactiveActionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        buildNewGamePanel();
        buildCreatePanel();
        JPanel quickPanel = buildQuickStatsPanel();

        constraints.insets = new Insets(10,10,10,10);
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;
        interactiveActionsPanel.add(gamePanel, constraints);
        constraints.gridy = 1;
        interactiveActionsPanel.add(createPanel, constraints);

        quickStatsPanel = new JPanel();
        quickStatsPanel.setLayout(new GridBagLayout());
        constraints.insets = new Insets(10,10,10,20);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        quickStatsPanel.add(quickPanel, constraints);

        JPanel history = buildHistoryPanel(width);

        contentWrapper.add(interactiveActionsPanel);
        contentWrapper.add(quickStatsPanel);
        contentWrapper.add(history);

        contents.add(contentWrapper, BorderLayout.CENTER);
        contents.add(Box.createRigidArea(new Dimension(width, 40)), BorderLayout.NORTH);
        contents.add(Box.createRigidArea(new Dimension(width, 40)), BorderLayout.SOUTH);
        contents.add(Box.createRigidArea(new Dimension(40, height-80)), BorderLayout.EAST);
        contents.add(Box.createRigidArea(new Dimension(40, height-80)), BorderLayout.WEST);

        onResize(width, height);
    }

    private void buildNewGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("NEW GAME", titleFnt, SwingConstants.CENTER);

        JButton easyBtn = new JButton(" EASY ");
        easyBtn.setHorizontalAlignment(SwingConstants.CENTER);
        easyBtn.setVerticalAlignment(SwingConstants.CENTER);
        easyBtn.setForeground(Palette.StrongGreen);
        easyBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onNewEasyGameClicked());

        JButton mediumBtn = new JButton("MEDIUM");
        mediumBtn.setHorizontalAlignment(SwingConstants.CENTER);
        mediumBtn.setVerticalAlignment(SwingConstants.CENTER);
        mediumBtn.setForeground(Palette.StrongOrange);
        mediumBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onNewMediumGameClicked());

        JButton hardBtn = new JButton(" HARD ");
        hardBtn.setHorizontalAlignment(SwingConstants.CENTER);
        hardBtn.setVerticalAlignment(SwingConstants.CENTER);
        hardBtn.setForeground(Palette.StrongRed);
        hardBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onNewHardGameClicked());

        JButton extremeBtn = new JButton("EXTREME");
        extremeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        extremeBtn.setVerticalAlignment(SwingConstants.CENTER);
        extremeBtn.setForeground(Palette.StrongBlue);
        extremeBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onNewExtremeGameClicked());

        JButton importBtn = new JButton("IMPORT KAKURO");
        importBtn.setHorizontalAlignment(SwingConstants.CENTER);
        importBtn.setVerticalAlignment(SwingConstants.CENTER);
        importBtn.setForeground(Color.BLACK);
        importBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onImportGameClicked());

        constraints.insets = new Insets(5,5,5,5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gamePanel.add(title, constraints);

        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        gamePanel.add(easyBtn, constraints);
        constraints.gridx = 1;
        gamePanel.add(mediumBtn, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        gamePanel.add(hardBtn, constraints);
        constraints.gridx = 1;
        gamePanel.add(extremeBtn, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.weightx = 2;
        gamePanel.add(importBtn, constraints);

        gamePanel.setBackground(Color.LIGHT_GRAY);
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private void buildCreatePanel() {
        createPanel = new JPanel();
        createPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("CREATE KAKURO", titleFnt, SwingConstants.CENTER);

        JPanel generatePanel = buildGeneratePanel();
        JPanel handMadePanel = buildHandMadePanel();

        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        createPanel.add(title, constraints);

        constraints.gridy = 1;
        createPanel.add(generatePanel, constraints);

        constraints.gridy = 2;
        createPanel.add(handMadePanel, constraints);

        createPanel.setBackground(Color.LIGHT_GRAY);
        createPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private JPanel buildGeneratePanel() {
        JPanel generatePanel = new JPanel();
        generatePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("GENERATE", subtitleFnt, SwingConstants.CENTER);

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Color.BLACK);

        JLabel paramLbl = buildLabel("By parameter:", bodyFnt, SwingConstants.LEFT);

        // the input for parameters
        JPanel paramInput = new JPanel();
        paramInput.setLayout(new GridBagLayout());

        JLabel rowsLbl = buildLabel("ROWS", bodyFnt, SwingConstants.LEFT);

        genRows = new JSpinner(new SpinnerNumberModel(9, 2, 80, 1));
        ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Color.BLACK);
        genRows.addChangeListener(e -> {
            if ((Integer)genRows.getValue() > 25) ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Palette.StrongRed);
            else ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Color.BLACK);
        });

        JLabel colsLbl = buildLabel("Columns", bodyFnt, SwingConstants.LEFT);

        genCols = new JSpinner(new SpinnerNumberModel(9, 2, 80, 1));
        ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Color.BLACK);
        genCols.addChangeListener(e -> {
            if ((Integer)genCols.getValue() > 25) ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Palette.StrongRed);
            else ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Color.BLACK);
        });

        JLabel diffLbl = buildLabel("Difficulty", bodyFnt, SwingConstants.LEFT);

        difficultyChooser = new JComboBox<>(new String[] { "Easy", "Medium", "Hard", "Extreme" });

        forceUnique = new JCheckBox();
        forceUnique.setSelected(true);
        forceUnique.setOpaque(false);
        forceUnique.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel forceUniqueLbl1 = buildLabel("Try to force unique solution by", smallFnt, SwingConstants.LEFT);
        forceUniqueLbl1.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel forceUniqueLbl2 = buildLabel("defining initial values if needed.", smallFnt, SwingConstants.LEFT);
        forceUniqueLbl2.setVerticalAlignment(SwingConstants.TOP);

        JButton confirmGen = new JButton("Ok"); //✅
        confirmGen.setForeground(Palette.StrongGreen);
        confirmGen.setBackground(Palette.HintGreen);
        confirmGen.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onGenerateByParameters((Integer)genRows.getValue(), (Integer)genCols.getValue(), (String)difficultyChooser.getSelectedItem(), forceUnique.isSelected()));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        paramInput.add(rowsLbl, constraints);
        constraints.gridy = 1;
        paramInput.add(genRows, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        paramInput.add(colsLbl, constraints);
        constraints.gridy = 1;
        paramInput.add(genCols, constraints);

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        paramInput.add(diffLbl, constraints);
        constraints.gridy = 1;
        paramInput.add(difficultyChooser, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        paramInput.add(forceUnique, constraints);
        constraints.gridx = 2;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        paramInput.add(forceUniqueLbl1, constraints);
        constraints.gridy = 3;
        constraints.insets.top = 0;
        paramInput.add(forceUniqueLbl2, constraints);

        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 8;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 3;
        paramInput.add(confirmGen, constraints);
        constraints.gridheight = 1;

        paramInput.setOpaque(false);


        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.BLACK);

        JLabel bySeedLbl = buildLabel("By seed:", bodyFnt, SwingConstants.LEFT);

        JPanel seedInput = new JPanel();
        seedInput.setLayout(new GridBagLayout());

        seed = new JTextField(17);
        seed.setFont(bodyFnt);
        seed.setText("Please enter a valid seed...");
        seed.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (seed.getText().equals("Please enter a valid seed..."))
                    seed.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (seed.getText().equals(""))
                    seed.setText("Please enter a valid seed...");
            }
        });

        JButton confirmSeed = new JButton("Ok");
        confirmSeed.setForeground(Palette.StrongGreen);
        confirmSeed.setBackground(Palette.HintGreen);
        confirmSeed.addActionListener(e -> {
            if (seed.getText().equals("Please enter a valid seed...") || seed.getText().equals(""))
                Dialogs.showErrorDialog("A seed must be provided to use this functionality", "Invalid seed");
            else ((DashboardScreenCtrl)ctrl).onGenerateBySeed(seed.getText());
        });

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 8;
        seedInput.add(seed, constraints);

        constraints.gridx = 8;
        constraints.gridwidth = 1;
        seedInput.add(confirmSeed, constraints);

        seedInput.setOpaque(false);


        constraints.insets = new Insets(5,4, 3, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        generatePanel.add(title, constraints);

        constraints.insets.top = 3;
        constraints.gridy = 1;
        generatePanel.add(sep1, constraints);

        constraints.gridy = 2;
        generatePanel.add(paramLbl, constraints);

        constraints.gridy = 3;
        generatePanel.add(paramInput, constraints);

        constraints.gridy = 4;
        generatePanel.add(sep2, constraints);

        constraints.gridy = 5;
        generatePanel.add(bySeedLbl, constraints);

        constraints.insets.bottom = 5;
        constraints.gridy = 6;
        generatePanel.add(seedInput, constraints);

        generatePanel.setBackground(Color.WHITE);
        generatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return generatePanel;
    }

    private JPanel buildHandMadePanel() {
        JPanel handMadePanel = new JPanel();
        handMadePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("HAND MADE", subtitleFnt, SwingConstants.CENTER);

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Color.BLACK);

        JPanel creatorInput = new JPanel();
        creatorInput.setLayout(new GridBagLayout());

        JLabel rowsLbl = buildLabel("Rows", bodyFnt, SwingConstants.LEFT);

        handRows = new JSpinner(new SpinnerNumberModel(9, 2, 30, 1));
        ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Color.BLACK);
        handRows.addChangeListener(e -> {
            if ((Integer)handRows.getValue() > 25) ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Palette.StrongRed);
            else ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Color.BLACK);
        });

        JLabel colsLbl = buildLabel("Columns", bodyFnt, SwingConstants.LEFT);

        handCols = new JSpinner(new SpinnerNumberModel(9, 2, 30, 1));
        ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Color.BLACK);
        handCols.addChangeListener(e -> {
            if ((Integer)handCols.getValue() > 25) ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Palette.StrongRed);
            else ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Color.BLACK);
        });

        JButton confirmHand = new JButton("Ok");
        confirmHand.setForeground(Palette.StrongGreen);
        confirmHand.setBackground(Palette.HintGreen);
        confirmHand.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onHandMadeClicked((Integer)handRows.getValue(), (Integer)handCols.getValue()));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 25, 2, 4);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        creatorInput.add(rowsLbl, constraints);
        constraints.gridy = 1;
        creatorInput.add(handRows, constraints);

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.insets.left = 4;
        creatorInput.add(colsLbl, constraints);
        constraints.gridy = 1;
        creatorInput.add(handCols, constraints);

        constraints.gridx = 8;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.insets.left = 25;
        constraints.insets.right = 25;
        creatorInput.add(confirmHand, constraints);

        creatorInput.setOpaque(false);


        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.BLACK);

        JButton importBtn = new JButton("IMPORT CREATION");
        importBtn.setHorizontalAlignment(SwingConstants.CENTER);
        importBtn.setVerticalAlignment(SwingConstants.CENTER);
        importBtn.setForeground(Color.BLACK);
        importBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onImportCreationClicked());

        constraints.insets = new Insets(5,4, 3, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        handMadePanel.add(title, constraints);

        constraints.insets.top = 3;
        constraints.gridy = 1;
        handMadePanel.add(sep1, constraints);

        constraints.gridy = 2;
        handMadePanel.add(creatorInput, constraints);

        constraints.gridy = 3;
        handMadePanel.add(sep2, constraints);

        constraints.insets.bottom = 5;
        constraints.gridy = 4;
        handMadePanel.add(importBtn, constraints);

        handMadePanel.setBackground(Color.WHITE);
        handMadePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return handMadePanel;
    }

    public void updateQuickStats() {
        quickStatsPanel.removeAll();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10,10,10,20);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        quickStatsPanel.add(buildQuickStatsPanel(), constraints);
        quickStatsPanel.revalidate();
    }

    private JPanel buildQuickStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = buildLabel("QUICK STATS", titleFnt, SwingConstants.CENTER);

        JLabel rankLbl = buildLabel("Ranking", subtitleFnt, SwingConstants.LEFT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.BLACK);

        String interest = ((DashboardScreenCtrl)ctrl).interestPlayer();
        ArrayList<Pair<Integer, Pair<String, String>>> ranking = ((DashboardScreenCtrl)ctrl).getTopRanking();

        JPanel topRankPanel = new JPanel();
        topRankPanel.setLayout(new GridLayout(ranking.size(), 2));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < ranking.size(); i++) {
            Pair<Integer, Pair<String, String>> item = ranking.get(i);
            Font itemFnt = bodyFnt;
            if (item.second.first.equals(interest)) itemFnt = new Font(Font.SANS_SERIF, Font.BOLD, 12);

            constraints.gridx = 0;
            constraints.gridy = i;
            topRankPanel.add(buildLabel("#" + (item.first+1) + ": " + item.second.first, itemFnt, SwingConstants.LEFT));

            constraints.gridx = 1;
            topRankPanel.add(buildLabel(item.second.second, itemFnt, SwingConstants.RIGHT));
        }
        topRankPanel.setOpaque(false);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Color.BLACK);

        JLabel topPointerLbl = buildLabel("Your top pointer", subtitleFnt, SwingConstants.LEFT);

        Map<String, Object> topPointerInfo = ((DashboardScreenCtrl)ctrl).getTopPointer();
        JPanel topPointerPanel = new JPanel();
        topPointerPanel.setLayout(new GridBagLayout());

        if (topPointerInfo.size() == 0) {
            constraints.insets = new Insets(65,0,65,0);
            constraints.gridx = 0;
            constraints.gridy = 0;
            topPointerPanel.add(buildLabel("No games played yet.", bodyFnt, SwingConstants.CENTER), constraints);
        } else {
            KakuroView topPointerKak = new KakuroView((String) topPointerInfo.get("board"), false);
            topPointerKak.setSize(150, 150);

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridheight = 5;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(0,5,0,25);
            topPointerPanel.add(topPointerKak, constraints);
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets.right = 5;
            constraints.insets.bottom = 10;
            constraints.gridheight = 1;
            topPointerPanel.add(buildLabel((String) topPointerInfo.get("score"), subtitleFnt, SwingConstants.RIGHT), constraints);
            constraints.insets.bottom = 2;
            constraints.gridy = 1;
            topPointerPanel.add(buildLabel((String) topPointerInfo.get("name"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 2;
            topPointerPanel.add(buildLabel("Difficulty: " + topPointerInfo.get("difficulty"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 3;
            topPointerPanel.add(buildLabel("Size: " + topPointerInfo.get("height") + "x" + topPointerInfo.get("width"), bodyFnt, SwingConstants.RIGHT), constraints);
            constraints.gridy = 4;
            constraints.insets.bottom = 0;
            topPointerPanel.add(buildLabel("Time: " + secondsToStringTime((int) topPointerInfo.get("timeSpent")), bodyFnt, SwingConstants.RIGHT), constraints);
        }
        topPointerPanel.setOpaque(false);

        JSeparator sep3 = new JSeparator();
        sep3.setForeground(Color.BLACK);

        JLabel playedLbl = buildLabel("Total kakuros played", subtitleFnt, SwingConstants.LEFT);

        Map<String, Integer> kakurosPlayedInfo = ((DashboardScreenCtrl)ctrl).getGamesPlayed();

        playedChartPanel = new BarChartView();
        playedChartPanel.setBarValueFont(subtitleFnt);
        playedChartPanel.setBarLabelFont(smallFnt);
        playedChartPanel.addBar("EASY",     kakurosPlayedInfo.get("easy"),     Palette.HintGreen);
        playedChartPanel.addBar("MEDIUM",   kakurosPlayedInfo.get("medium"),   Palette.HintOrange);
        playedChartPanel.addBar("HARD",     kakurosPlayedInfo.get("hard"),     Palette.WarningLightRed);
        playedChartPanel.addBar("EXTREME",  kakurosPlayedInfo.get("extreme"),  Palette.SelectionBlue);
        playedChartPanel.addBar("BY USERS", kakurosPlayedInfo.get("userMade"), new Color(0xAAAAAA));
        playedChartPanel.setPreferredHeight(ranking.size() == 3 ? 144 : 128);
        playedChartPanel.layoutHistogram();
        playedChartPanel.setBackground(Color.WHITE);
        playedChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        constraints.insets = new Insets(5,5,5,5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        statsPanel.add(title, constraints);

        constraints.gridy = 1;
        statsPanel.add(topPointerLbl, constraints);

        constraints.gridy = 2;
        statsPanel.add(rankLbl, constraints);

        constraints.gridy = 3;
        statsPanel.add(sep, constraints);

        constraints.gridy = 4;
        statsPanel.add(topRankPanel, constraints);

        constraints.gridy = 5;
        statsPanel.add(sep2, constraints);

        constraints.gridy = 6;
        statsPanel.add(topPointerLbl, constraints);

        constraints.gridy = 7;
        statsPanel.add(topPointerPanel, constraints);

        constraints.gridy = 8;
        statsPanel.add(sep3, constraints);

        constraints.gridy = 9;
        statsPanel.add(playedLbl, constraints);

        constraints.gridy = 10;
        constraints.fill = GridBagConstraints.BOTH;
        statsPanel.add(playedChartPanel, constraints);

        statsPanel.setBackground(Color.LIGHT_GRAY);
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return statsPanel;
    }

    private JPanel buildHistoryPanel(int width) {
        JPanel history = new JPanel();
        history.setLayout(new BoxLayout(history, BoxLayout.Y_AXIS));

        JLabel title = buildLabel("HISTORY", titleFnt, SwingConstants.LEFT);

        buildGameHistoryPanel(width);
        historyScroll = new JScrollPane(historyPanel);
        historyScroll.getVerticalScrollBar().setUnitIncrement(20);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        history.add(title);
        history.add(historyScroll);

        //history.add(hHistoryFiller);

        return history;
    }

    public void updateHistoryPanel() {
        buildGameHistoryPanel(contents.getWidth());
        historyScroll.setViewportView(historyPanel);
    }
    private void buildGameHistoryPanel(int windowWidth) {
        ArrayList<Map<String, Object>> allGames = ((DashboardScreenCtrl) ctrl).getHistory();
        historyPanel = new JPanel();
        historyPanel.setLayout(new GridLayout(allGames.size()+1, 1));
        historyPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        if (allGames.size() == 0) {
            JLabel noGamesYetLbl = buildLabel(
                    "<html><body>Welcome! You haven't played any games yet, click on one of the buttons in the NEW GAME section to get started.</body></html>",
                    bodyFnt,
                    SwingConstants.CENTER);
            //historyPanel.add(noGamesYetLbl);
        }

        for (Map<String, Object> gameData : allGames) {
            String state = (String) gameData.get("state");

            String board = (String) gameData.get("board");
            String name = (String) gameData.get("name");
            int width = (Integer) gameData.get("width");
            int height = (Integer) gameData.get("height");
            String difficulty = (String) gameData.get("difficulty");
            int timeSpent = (int) gameData.get("timeSpent");
            String lastPlayed = ((Timestamp) gameData.get("lastPlayed")).toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("EEEE d MMMM uuuu"));
            float score = 0;
            if (!state.equals("unfinished")) score = (float) gameData.get("score");

            JPanel tile = buildHistoryGameTile(board, name, width, height, difficulty, secondsToStringTime(timeSpent), lastPlayed, score, state);

            historyPanel.add(tile);
        }

        hHistoryFiller = Box.createRigidArea(new Dimension(windowWidth/2-90, 1));
        historyPanel.add(hHistoryFiller);
    }

    private JPanel buildHistoryGameTile(String board, String name, int width, int height, String difficulty, String timeSpent, String lastPlayed, float score, String state) {
        JPanel tile = new JPanel();
        tile.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        KakuroView kakuroView = new KakuroView(board, false);
        kakuroView.setSize(200, 200);

        JLabel headerLbl = buildLabel(
                state.equals("unfinished")? "Unfinished" : "Score: " + score,
                subtitleFnt,
                SwingConstants.LEFT);

        JButton resumeBtn = new JButton("Resume game");
        resumeBtn.setFont(bodyFnt);
        resumeBtn.setForeground(Color.BLACK);
        resumeBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        resumeBtn.setVerticalAlignment(SwingConstants.CENTER);
        resumeBtn.addActionListener(e -> ((DashboardScreenCtrl)ctrl).onResumeFromHistory(name));

        JLabel nameLbl = buildLabel(name, bodyFnt, SwingConstants.LEFT);

        JLabel timeSpentLbl = buildLabel("Time: " + timeSpent, bodyFnt, SwingConstants.LEFT);

        JLabel sizeLbl = buildLabel("Size: " + height + " x " + width, bodyFnt, SwingConstants.LEFT);

        JLabel difficultyLbl = buildLabel("Difficulty: " + difficulty, bodyFnt, SwingConstants.LEFT);

        JLabel lastPlayedLbl = buildLabel("Last played on: " + lastPlayed, bodyFnt, SwingConstants.LEFT);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 6;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.insets = new Insets(0, 5, 0, 5);
        tile.add(kakuroView, constraints);

        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets.top = 20;
        tile.add(headerLbl, constraints);
        constraints.insets.bottom = 5;

        if (state.equals("unfinished")) {
            constraints.gridx = 2;
            constraints.insets.right = 30;
            tile.add(resumeBtn, constraints);
            constraints.insets.right = 5;
        }

        constraints.insets.top = 5;

        constraints.gridx = 1;
        constraints.gridy = 1;
        tile.add(nameLbl, constraints);
        constraints.gridy = 2;
        tile.add(timeSpentLbl, constraints);
        constraints.gridy = 3;
        tile.add(sizeLbl, constraints);
        constraints.gridy = 4;
        tile.add(difficultyLbl, constraints);
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        //constraints.insets.right = 30;
        tile.add(lastPlayedLbl, constraints);

        return tile;
    }

    private String secondsToStringTime(int time) {
        int hours = time/3600;
        int minutes = time/60 - hours*60;
        int seconds = time - minutes*60 - hours*3600;
        String timeStr = "";
        if (hours > 0) {
            timeStr += hours+":";
            if (minutes < 10) timeStr += "0";
        }
        timeStr += minutes+":";
        if (seconds < 10) timeStr += "0";
        timeStr += seconds;

        return timeStr;
    }

    private JLabel buildLabel(String text, Font font, int horizontalAlign) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.BLACK);
        label.setOpaque(false);
        label.setHorizontalAlignment(horizontalAlign);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onResize(int width, int height) {
        hHistoryFiller = Box.createRigidArea(new Dimension(width/2 -90, 1));
        //playedChartPanel.setSize(playedChartPanel.getWidth(), (gamePanel.getHeight()+createPanel.getHeight()-18) - (quickStatsPanel.getHeight()-playedChartPanel.getHeight()));
        playedChartPanel.layoutHistogram();
        Component[] histComp = historyPanel.getComponents();
        histComp[histComp.length-1] = hHistoryFiller;
        super.onResize(width, height);
    }
}
