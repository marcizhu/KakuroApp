package src.presentation.screens;

import src.presentation.controllers.AbstractScreenCtrl;
import src.presentation.controllers.DashboardScreenCtrl;
import src.presentation.utils.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DashboardScreen extends AbstractScreen {

    JPanel historyPanel;

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
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        titleFnt = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        subtitleFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        bodyFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        smallFnt = new Font(Font.SANS_SERIF, Font.PLAIN, 9);

        buildNewGamePanel();
        buildCreatePanel();



        constraints.insets = new Insets(10,10,10,10);
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(gamePanel, constraints);

        constraints.gridy = 1;
        contents.add(createPanel, constraints);
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

        genRows = new JSpinner(new SpinnerNumberModel(9, 2, 20, 1));

        JLabel colsLbl = new JLabel("Columns");
        colsLbl.setFont(bodyFnt);
        colsLbl.setForeground(Color.BLACK);
        colsLbl.setOpaque(false);
        colsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        colsLbl.setVerticalAlignment(SwingConstants.CENTER);

        genCols = new JSpinner(new SpinnerNumberModel(9, 2, 20, 1));

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

        JButton confirmGen = new JButton("✅");
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

        JButton confirmSeed = new JButton("✅");
        confirmSeed.setForeground(Palette.StrongGreen);
        confirmSeed.setBackground(Palette.HintGreen);
        confirmSeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DashboardScreenCtrl)ctrl).onGenerateBySeed(seed.getText());
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

        handRows = new JSpinner(new SpinnerNumberModel(9, 2, 20, 1));

        JLabel colsLbl = new JLabel("Columns");
        colsLbl.setFont(bodyFnt);
        colsLbl.setForeground(Color.BLACK);
        colsLbl.setOpaque(false);
        colsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        colsLbl.setVerticalAlignment(SwingConstants.CENTER);

        handCols = new JSpinner(new SpinnerNumberModel(9, 2, 20, 1));

        JButton confirmHand = new JButton("✅");
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

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onDestroy() {

    }
}
