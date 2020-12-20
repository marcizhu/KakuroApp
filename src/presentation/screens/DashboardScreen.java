package src.presentation.screens;

import src.domain.entities.GameFinished;
import src.domain.entities.GameInProgress;
import src.domain.entities.Kakuro;
import src.presentation.controllers.AbstractScreenCtrl;
import src.presentation.controllers.DashboardScreenCtrl;
import src.presentation.controllers.GameScreenCtrl;
import src.presentation.controllers.MyKakurosScreenCtrl;
import src.presentation.utils.Dialogs;
import src.presentation.utils.Palette;
import src.presentation.views.KakuroInfoCardView;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DashboardScreen extends AbstractScreen {

    JPanel historyPanel;
    JScrollPane historyScroll;
    Component hHistoryFiller;

    JPanel quickStatsPanel;

    JPanel interactiveActionsPanel;
    JPanel gamePanel;
    JPanel createPanel;
    JSpinner genRows, genCols;
    JComboBox difficultyChooser;
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

        JPanel interactivePanel = new JPanel();
        interactivePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        buildNewGamePanel();
        buildCreatePanel();

        constraints.insets = new Insets(10,10,10,10);
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;
        interactivePanel.add(gamePanel, constraints);
        constraints.gridy = 1;
        interactivePanel.add(createPanel, constraints);

        JPanel history = buildHistoryPanel(width);

        contentWrapper.add(interactivePanel);
        contentWrapper.add(history);

        contents.add(contentWrapper, BorderLayout.CENTER);
        contents.add(Box.createRigidArea(new Dimension(width, 40)), BorderLayout.NORTH);
        contents.add(Box.createRigidArea(new Dimension(width, 40)), BorderLayout.SOUTH);
        contents.add(Box.createRigidArea(new Dimension(40, height-80)), BorderLayout.EAST);
        contents.add(Box.createRigidArea(new Dimension(40, height-80)), BorderLayout.WEST);
    }

    private void buildNewGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = new JLabel("NEW GAME");
        title.setFont(titleFnt);
        title.setForeground(Color.BLACK);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JButton easyBtn = new JButton(" EASY ");
        easyBtn.setHorizontalAlignment(SwingConstants.CENTER);
        easyBtn.setVerticalAlignment(SwingConstants.CENTER);
        easyBtn.setForeground(Palette.StrongGreen);
        easyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onNewEasyGameClicked();
            }
        });

        JButton mediumBtn = new JButton("MEDIUM");
        mediumBtn.setHorizontalAlignment(SwingConstants.CENTER);
        mediumBtn.setVerticalAlignment(SwingConstants.CENTER);
        mediumBtn.setForeground(Palette.StrongOrange);
        mediumBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onNewMediumGameClicked();
            }
        });

        JButton hardBtn = new JButton(" HARD ");
        hardBtn.setHorizontalAlignment(SwingConstants.CENTER);
        hardBtn.setVerticalAlignment(SwingConstants.CENTER);
        hardBtn.setForeground(Palette.StrongRed);
        hardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onNewHardGameClicked();
            }
        });

        JButton extremeBtn = new JButton("EXTREME");
        extremeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        extremeBtn.setVerticalAlignment(SwingConstants.CENTER);
        extremeBtn.setForeground(Palette.StrongBlue);
        extremeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onNewExtremeGameClicked();
            }
        });

        JButton importBtn = new JButton("IMPORT KAKURO");
        importBtn.setHorizontalAlignment(SwingConstants.CENTER);
        importBtn.setVerticalAlignment(SwingConstants.CENTER);
        importBtn.setForeground(Color.BLACK);
        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onImportGameClicked();
            }
        });

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

        JLabel title = new JLabel("CREATE KAKURO");
        title.setFont(titleFnt);
        title.setForeground(Color.BLACK);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

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

        JLabel title = new JLabel("GENERATE");
        title.setFont(subtitleFnt);
        title.setForeground(Color.BLACK);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Color.BLACK);

        JLabel paramLbl = new JLabel("By parameters:");
        paramLbl.setFont(bodyFnt);
        paramLbl.setForeground(Color.BLACK);
        paramLbl.setOpaque(false);
        paramLbl.setHorizontalAlignment(SwingConstants.LEFT);
        paramLbl.setVerticalAlignment(SwingConstants.CENTER);

        // the input for parameters
        JPanel paramInput = new JPanel();
        paramInput.setLayout(new GridBagLayout());

        JLabel rowsLbl = new JLabel("Rows");
        rowsLbl.setFont(bodyFnt);
        rowsLbl.setForeground(Color.BLACK);
        rowsLbl.setOpaque(false);
        rowsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        rowsLbl.setVerticalAlignment(SwingConstants.CENTER);

        genRows = new JSpinner(new SpinnerNumberModel(9, 2, 80, 1));
        ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Color.BLACK);
        genRows.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer)genRows.getValue() > 25) ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Palette.StrongRed);
                else ((JSpinner.DefaultEditor) genRows.getEditor()).getTextField().setForeground(Color.BLACK);
            }
        });

        JLabel colsLbl = new JLabel("Columns");
        colsLbl.setFont(bodyFnt);
        colsLbl.setForeground(Color.BLACK);
        colsLbl.setOpaque(false);
        colsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        colsLbl.setVerticalAlignment(SwingConstants.CENTER);

        genCols = new JSpinner(new SpinnerNumberModel(9, 2, 80, 1));
        ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Color.BLACK);
        genCols.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer)genCols.getValue() > 25) ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Palette.StrongRed);
                else ((JSpinner.DefaultEditor) genCols.getEditor()).getTextField().setForeground(Color.BLACK);
            }
        });

        JLabel diffLbl = new JLabel("Difficulty");
        diffLbl.setFont(bodyFnt);
        diffLbl.setForeground(Color.BLACK);
        diffLbl.setOpaque(false);
        diffLbl.setHorizontalAlignment(SwingConstants.LEFT);
        diffLbl.setVerticalAlignment(SwingConstants.CENTER);

        difficultyChooser = new JComboBox(new String[] {"Easy", "Medium", "Hard", "Extreme"});

        forceUnique = new JCheckBox();
        forceUnique.setSelected(true);
        forceUnique.setOpaque(false);
        forceUnique.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel forceUniqueLbl1 = new JLabel("Try to force unique solution by");
        forceUniqueLbl1.setFont(smallFnt);
        forceUniqueLbl1.setForeground(Color.BLACK);
        forceUniqueLbl1.setOpaque(false);
        forceUniqueLbl1.setHorizontalAlignment(SwingConstants.LEFT);
        forceUniqueLbl1.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel forceUniqueLbl2 = new JLabel("defining initial values if needed.");
        forceUniqueLbl2.setFont(smallFnt);
        forceUniqueLbl2.setForeground(Color.BLACK);
        forceUniqueLbl2.setOpaque(false);
        forceUniqueLbl2.setHorizontalAlignment(SwingConstants.LEFT);
        forceUniqueLbl2.setVerticalAlignment(SwingConstants.TOP);

        JButton confirmGen = new JButton("Ok"); //✅
        confirmGen.setForeground(Palette.StrongGreen);
        confirmGen.setBackground(Palette.HintGreen);
        confirmGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onGenerateByParameters((Integer)genRows.getValue(), (Integer)genCols.getValue(), (String)difficultyChooser.getSelectedItem(), forceUnique.isSelected());
            }
        });

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

        JLabel bySeedLbl = new JLabel("By seed:");
        bySeedLbl.setFont(bodyFnt);
        bySeedLbl.setForeground(Color.BLACK);
        bySeedLbl.setOpaque(false);
        bySeedLbl.setHorizontalAlignment(SwingConstants.LEFT);
        bySeedLbl.setVerticalAlignment(SwingConstants.CENTER);

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
        confirmSeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (seed.getText().equals("Please enter a valid seed...") || seed.getText().equals(""))
                    Dialogs.showErrorDialog("A seed must be provided to use this functionality", "Invalid seed");
                else ((DashboardScreenCtrl)ctrl).onGenerateBySeed(seed.getText());
            }
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

        JLabel title = new JLabel("HAND MADE");
        title.setFont(subtitleFnt);
        title.setForeground(Color.BLACK);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Color.BLACK);

        JPanel creatorInput = new JPanel();
        creatorInput.setLayout(new GridBagLayout());

        JLabel rowsLbl = new JLabel("Rows");
        rowsLbl.setFont(bodyFnt);
        rowsLbl.setForeground(Color.BLACK);
        rowsLbl.setOpaque(false);
        rowsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        rowsLbl.setVerticalAlignment(SwingConstants.CENTER);

        handRows = new JSpinner(new SpinnerNumberModel(9, 2, 30, 1));
        ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Color.BLACK);
        handRows.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer)handRows.getValue() > 25) ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Palette.StrongRed);
                else ((JSpinner.DefaultEditor) handRows.getEditor()).getTextField().setForeground(Color.BLACK);
            }
        });

        JLabel colsLbl = new JLabel("Columns");
        colsLbl.setFont(bodyFnt);
        colsLbl.setForeground(Color.BLACK);
        colsLbl.setOpaque(false);
        colsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        colsLbl.setVerticalAlignment(SwingConstants.CENTER);

        handCols = new JSpinner(new SpinnerNumberModel(9, 2, 30, 1));
        ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Color.BLACK);
        handCols.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer)handCols.getValue() > 25) ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Palette.StrongRed);
                else ((JSpinner.DefaultEditor) handCols.getEditor()).getTextField().setForeground(Color.BLACK);
            }
        });

        JButton confirmHand = new JButton("Ok");
        confirmHand.setForeground(Palette.StrongGreen);
        confirmHand.setBackground(Palette.HintGreen);
        confirmHand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onHandMadeClicked((Integer)handRows.getValue(), (Integer)handCols.getValue());
            }
        });

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
        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onImportCreationClicked();
            }
        });

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


    private JPanel buildHistoryPanel(int width) {
        JPanel history = new JPanel();
        history.setLayout(new BoxLayout(history, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("HISTORY");
        title.setFont(titleFnt);
        title.setForeground(Color.BLACK);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        title.setVerticalAlignment(SwingConstants.CENTER);

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
            JLabel noGamesYetLbl = new JLabel("<html><body>Welcome! You haven't played any games yet, click on one of the buttons in the NEW GAME section to get started.</body></html>");
            noGamesYetLbl.setFont(bodyFnt);
            noGamesYetLbl.setForeground(Color.BLACK);
            noGamesYetLbl.setOpaque(false);
            noGamesYetLbl.setHorizontalAlignment(SwingConstants.CENTER);
            noGamesYetLbl.setVerticalAlignment(SwingConstants.CENTER);
            //historyPanel.add(noGamesYetLbl);
        }

        for (Map<String, Object> gameData : allGames) {
            String state = (String) gameData.get("state");

            String board = (String) gameData.get("board");
            String name = (String) gameData.get("name");
            int width = (Integer) gameData.get("width");
            int height = (Integer) gameData.get("height");
            String difficulty = (String) gameData.get("difficulty");
            float timeSpent = (float) gameData.get("timeSpent");
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

        JLabel headerLbl = new JLabel(state.equals("unfinished")? "Unfinished" : "Score: " + score);
        headerLbl.setFont(subtitleFnt);
        headerLbl.setForeground(Color.BLACK);
        headerLbl.setOpaque(false);
        headerLbl.setHorizontalAlignment(SwingConstants.LEFT);
        headerLbl.setVerticalAlignment(SwingConstants.CENTER);

        JButton resumeBtn = new JButton("Resume game");
        resumeBtn.setFont(bodyFnt);
        resumeBtn.setForeground(Color.BLACK);
        resumeBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        resumeBtn.setVerticalAlignment(SwingConstants.CENTER);
        resumeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onResumeFromHistory(name);
            }
        });

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(bodyFnt);
        nameLbl.setForeground(Color.BLACK);
        nameLbl.setOpaque(false);
        nameLbl.setHorizontalAlignment(SwingConstants.LEFT);
        nameLbl.setVerticalAlignment(SwingConstants.CENTER);

        JLabel timeSpentLbl = new JLabel("Time: " + timeSpent);
        timeSpentLbl.setFont(bodyFnt);
        timeSpentLbl.setForeground(Color.BLACK);
        timeSpentLbl.setOpaque(false);
        timeSpentLbl.setHorizontalAlignment(SwingConstants.LEFT);
        timeSpentLbl.setVerticalAlignment(SwingConstants.CENTER);

        JLabel sizeLbl = new JLabel("Size: " + height + " x " + width);
        sizeLbl.setFont(bodyFnt);
        sizeLbl.setForeground(Color.BLACK);
        sizeLbl.setOpaque(false);
        sizeLbl.setHorizontalAlignment(SwingConstants.LEFT);
        sizeLbl.setVerticalAlignment(SwingConstants.CENTER);

        JLabel difficultyLbl = new JLabel("Difficulty: " + difficulty);
        difficultyLbl.setFont(bodyFnt);
        difficultyLbl.setForeground(Color.BLACK);
        difficultyLbl.setOpaque(false);
        difficultyLbl.setHorizontalAlignment(SwingConstants.LEFT);
        difficultyLbl.setVerticalAlignment(SwingConstants.CENTER);

        JLabel lastPlayedLbl = new JLabel("Last played on: " + lastPlayed);
        lastPlayedLbl.setFont(bodyFnt);
        lastPlayedLbl.setForeground(Color.BLACK);
        lastPlayedLbl.setOpaque(false);
        lastPlayedLbl.setHorizontalAlignment(SwingConstants.LEFT);
        lastPlayedLbl.setVerticalAlignment(SwingConstants.CENTER);


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

    private String secondsToStringTime(float time) {
        int hours = (int)time/3600;
        int minutes = (int)time/60 - hours*60;
        int seconds = (int)time - minutes*60 - hours*3600;
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

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onResize(int width, int height) {
        hHistoryFiller = Box.createRigidArea(new Dimension(width/2 -90, 1));
        Component[] histComp = historyPanel.getComponents();
        histComp[histComp.length-1] = hHistoryFiller;
        super.onResize(width, height);
    }
}
