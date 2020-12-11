package src.presentation.screens;

import src.presentation.controllers.CreatorScreenCtrl;
import src.presentation.utils.Palette;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CreatorScreen extends AbstractScreen {

    JPanel leftContent;
    JTabbedPane blackWhiteSelectors;
    JPanel selectorBlack;
    JCheckBox blackBrushChk;
    JScrollPane blackValuesScroll;
    JPanel blackPossibleValues;
    JPanel selectorWhite;
    JCheckBox whiteBrushChk;
    JScrollPane whiteValuesScroll;
    JPanel whitePossibleValues;

    JTextField kakuroName;
    JButton kakuroStateBtn;

    JPanel lowerRightContent;
    KakuroView creatorBoard;

    public CreatorScreen(CreatorScreenCtrl ctrl) { super(ctrl); }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();

        leftContent = buildLeftContent();
        lowerRightContent = buildRightContent();

        String initialBoard = ((CreatorScreenCtrl)ctrl).getBoardToDisplay();
        creatorBoard = new KakuroView(initialBoard, true);
        creatorBoard.setSize(width/2, height - lowerRightContent.getSize().height);
        creatorBoard.setVisible(true);
        creatorBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setUpListener();

        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        contents.add(leftContent, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        contents.add(creatorBoard, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        contents.add(lowerRightContent, constraints);
        contents.setVisible(true);
    }

    private JPanel buildLeftContent() {
        JPanel left = new JPanel();
        left.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        blackWhiteSelectors = new JTabbedPane();

        buildSelectorBlack();
        blackWhiteSelectors.addTab("BLACK CELLS", selectorBlack);
        buildSelectorWhite();
        blackWhiteSelectors.addTab("WHITE CELLS", selectorWhite);

        blackWhiteSelectors.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ((CreatorScreenCtrl)ctrl).onSelectedTabChanged(blackWhiteSelectors.getSelectedIndex());
            }
        });

        JPanel lowerLeft = new JPanel();
        lowerLeft.setLayout(new GridBagLayout());

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new GridBagLayout());
        constraints.insets = new Insets(2, 5, 2, 5);

        JLabel setNameLbl = new JLabel("<html><body>Give it a unique and memorable name ;)</body></html>");
        setNameLbl.setForeground(Color.BLACK);
        setNameLbl.setOpaque(false);
        setNameLbl.setAlignmentX(SwingConstants.LEFT);
        setNameLbl.setAlignmentY(SwingConstants.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        namePanel.add(setNameLbl, constraints);

        kakuroName = new JTextField("");
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        namePanel.add(kakuroName, constraints);

        kakuroStateBtn = new JButton("VALIDATE");
        kakuroStateBtn.setForeground(Palette.HintOrange);
        kakuroStateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CreatorScreenCtrl)ctrl).onKakuroStateButtonPressed(kakuroName.getText());
            }
        });

        constraints.insets = new Insets(5, 20, 5, 20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 4;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        lowerLeft.add(namePanel, constraints);

        constraints.insets = new Insets(5, 5, 5, 20);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        lowerLeft.add(kakuroStateBtn, constraints);

        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        left.add(blackWhiteSelectors, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        left.add(lowerLeft, constraints);

        return left;
    }

    private void buildSelectorBlack() {
        GridBagConstraints constraints = new GridBagConstraints();
        selectorBlack = new JPanel();
        selectorBlack.setLayout(new GridBagLayout());

        JPanel upperSelector = new JPanel();
        upperSelector.setLayout(new GridBagLayout());

        JLabel brushToolLbl = new JLabel("Brush tool");
        brushToolLbl.setForeground(Color.BLACK);
        brushToolLbl.setHorizontalAlignment(SwingConstants.CENTER);
        brushToolLbl.setVerticalAlignment(SwingConstants.CENTER);
        brushToolLbl.setOpaque(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        upperSelector.add(brushToolLbl, constraints);

        blackBrushChk = new JCheckBox();
        blackBrushChk.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((CreatorScreenCtrl)ctrl).setBlackBrushEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        upperSelector.add(blackBrushChk, constraints);

        JPanel lowerSelector = new JPanel();
        lowerSelector.setLayout(new GridBagLayout());

        JLabel valueAssigLbl = new JLabel("Possible value assignations");
        valueAssigLbl.setForeground(Color.BLACK);
        valueAssigLbl.setHorizontalAlignment(SwingConstants.LEFT);
        valueAssigLbl.setVerticalAlignment(SwingConstants.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        lowerSelector.add(valueAssigLbl, constraints);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        constraints.gridy = 1;
        lowerSelector.add(separator, constraints);

        buildBlackPossibleValues();
        blackValuesScroll = new JScrollPane(blackPossibleValues);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 5;
        constraints.fill = GridBagConstraints.BOTH;
        lowerSelector.add(blackValuesScroll, constraints);


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        selectorBlack.add(upperSelector, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        selectorBlack.add(lowerSelector, constraints);
    }

    private void buildBlackPossibleValues() {
        Pair<ArrayList<Integer>, Boolean> allPossibilities = ((CreatorScreenCtrl)ctrl).getBlackPossibilitiesList();
        blackPossibleValues = new JPanel();
        blackPossibleValues.setLayout(new GridLayout(1, allPossibilities.first.size() + (allPossibilities.second ? 1 : 0)));

        if (allPossibilities.second) { //black cell has value, offer to clear it
            JLabel clearLbl = new JLabel("X");
            clearLbl.setForeground(Color.BLACK);
            clearLbl.setBackground(Palette.WarningLightRed);
            clearLbl.setAlignmentX(SwingConstants.CENTER);
            clearLbl.setAlignmentY(SwingConstants.CENTER);
            clearLbl.setOpaque(true);
            clearLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).clearSelectedBlackCellValueClicked();
                }
            });
            blackPossibleValues.add(clearLbl);
        }

        int howMany = allPossibilities.first.size();
        for (int i = 0; i < howMany; i++) {
            int value = allPossibilities.first.get(i);

            JLabel valueOptLbl = new JLabel(""+value);
            valueOptLbl.setForeground(Color.BLACK);
            valueOptLbl.setBackground(Palette.SelectionBlue);
            valueOptLbl.setAlignmentX(SwingConstants.CENTER);
            valueOptLbl.setAlignmentY(SwingConstants.CENTER);
            valueOptLbl.setOpaque(true);
            valueOptLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).blackCellSelectValueClicked(value);
                }
            });
            blackPossibleValues.add(valueOptLbl);
        }
    }

    public void updateBlackPossibleValues() {
        buildBlackPossibleValues();
        blackValuesScroll.setViewportView(blackPossibleValues);
    }

    private void buildSelectorWhite() {
        GridBagConstraints constraints = new GridBagConstraints();
        selectorWhite = new JPanel();
        selectorWhite.setLayout(new GridBagLayout());

        JPanel upperSelector = new JPanel();
        upperSelector.setLayout(new GridBagLayout());

        JLabel brushToolLbl = new JLabel("Brush tool");
        brushToolLbl.setForeground(Color.BLACK);
        brushToolLbl.setHorizontalAlignment(SwingConstants.CENTER);
        brushToolLbl.setVerticalAlignment(SwingConstants.CENTER);
        brushToolLbl.setOpaque(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        upperSelector.add(brushToolLbl, constraints);

        whiteBrushChk = new JCheckBox();
        whiteBrushChk.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((CreatorScreenCtrl)ctrl).setWhiteBrushEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        upperSelector.add(whiteBrushChk, constraints);

        JPanel lowerSelector = new JPanel();
        lowerSelector.setLayout(new GridBagLayout());

        JLabel valueAssigLbl = new JLabel("Possible value assignations");
        valueAssigLbl.setForeground(Color.BLACK);
        valueAssigLbl.setHorizontalAlignment(SwingConstants.LEFT);
        valueAssigLbl.setVerticalAlignment(SwingConstants.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        lowerSelector.add(valueAssigLbl, constraints);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        constraints.gridy = 1;
        lowerSelector.add(separator, constraints);

        buildWhitePossibleValues();
        whiteValuesScroll = new JScrollPane(whitePossibleValues);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 5;
        constraints.fill = GridBagConstraints.BOTH;
        lowerSelector.add(whiteValuesScroll, constraints);


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        selectorWhite.add(upperSelector, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        selectorWhite.add(lowerSelector, constraints);
    }

    private void buildWhitePossibleValues() {
        Pair<ArrayList<Integer>, Boolean> allPossibilities = ((CreatorScreenCtrl)ctrl).getWhitePossibilitiesList();
        whitePossibleValues = new JPanel();
        whitePossibleValues.setLayout(new GridLayout(1, allPossibilities.first.size() + (allPossibilities.second ? 1 : 0)));

        if (allPossibilities.second) { //black cell has value, offer to clear it
            JLabel clearLbl = new JLabel("X");
            clearLbl.setForeground(Color.BLACK);
            clearLbl.setBackground(Palette.WarningLightRed);
            clearLbl.setAlignmentX(SwingConstants.CENTER);
            clearLbl.setAlignmentY(SwingConstants.CENTER);
            clearLbl.setOpaque(true);
            clearLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).clearSelectedWhiteCellValueClicked();
                }
            });
            whitePossibleValues.add(clearLbl);
        }

        int howMany = allPossibilities.first.size();
        for (int i = 0; i < howMany; i++) {
            int value = allPossibilities.first.get(i);

            JLabel valueOptLbl = new JLabel(""+value);
            valueOptLbl.setForeground(Color.BLACK);
            valueOptLbl.setBackground(Palette.SelectionBlue);
            valueOptLbl.setAlignmentX(SwingConstants.CENTER);
            valueOptLbl.setAlignmentY(SwingConstants.CENTER);
            valueOptLbl.setOpaque(true);
            valueOptLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).whiteCellSelectValueClicked(value);
                }
            });
            whitePossibleValues.add(valueOptLbl);
        }
    }

    private JPanel buildRightContent() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 15, 5, 15);
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Import button
        JButton importBtn = new JButton("IMPORT");
        importBtn.setForeground(Color.BLACK);
        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CreatorScreenCtrl)ctrl).onImportButtonClicked();
            }
        });
        constraints.gridx = 0;
        buttonPanel.add(importBtn, constraints);

        // Export button
        JButton exportBtn = new JButton("EXPORT");
        exportBtn.setForeground(Color.BLACK);
        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CreatorScreenCtrl)ctrl).onExportButtonClicked();
            }
        });
        constraints.gridx = 1;
        buttonPanel.add(exportBtn, constraints);

        // Fill kakuro button
        JButton fillKakuroBtn = new JButton("FILL IT FOR ME");
        fillKakuroBtn.setForeground(Color.BLACK);
        fillKakuroBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CreatorScreenCtrl)ctrl).onFillKakuroButtonClicked();
            }
        });
        constraints.gridx = 2;
        buttonPanel.add(fillKakuroBtn, constraints);

        // Clear values button
        JButton clearValuesBtn = new JButton("CLEAR ALL VALUES");
        clearValuesBtn.setForeground(Palette.StrongRed);
        clearValuesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CreatorScreenCtrl)ctrl).onClearBoardButtonClicked();
            }
        });
        constraints.gridx = 3;
        buttonPanel.add(clearValuesBtn, constraints);

        return buttonPanel;
    }

    public void updateWhitePossibleValues() {
        buildWhitePossibleValues();
        whiteValuesScroll.setViewportView(whitePossibleValues);
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        leftContent.setSize(width/2, height);
        lowerRightContent.setSize(width/2, lowerRightContent.getHeight());
        int remainingWidth = width - leftContent.getWidth();
        int remainingHeight = height - lowerRightContent.getHeight();
        creatorBoard.setSize(remainingWidth, remainingHeight);
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    private void setUpListener() {
        creatorBoard.setBoardMouseEventListener(new KakuroView.BoardMouseEventListener() {
            @Override
            public void onBlackCellViewClicked(int row, int col, int section) {
                ((CreatorScreenCtrl)ctrl).setSelectedPos(row, col, section);
            }

            @Override
            public void onWhiteCellViewClicked(int row, int col) {
                ((CreatorScreenCtrl)ctrl).setSelectedPos(row, col, CreatorScreenCtrl.WHITE_CELL);
            }

            @Override
            public void onBlackCellViewEntered(int row, int col) { }

            @Override
            public void onWhiteCellViewEntered(int row, int col) { }

            @Override
            public void onCellInBoardPressed(int row, int col) { }

            @Override
            public void onCellInBoardReleased(int row, int col) { }

            @Override
            public void onListenerDetached() { }
        });
    }
}
