package src.presentation.screens;

import src.presentation.controllers.CreatorScreenCtrl;
import src.presentation.utils.Palette;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
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

    JLabel tipBox;

    JTextField kakuroName;
    JButton kakuroStateBtn;

    Component horizontalLeftFiller;

    JPanel lowerRightContent;
    KakuroView creatorBoard;

    public CreatorScreen(CreatorScreenCtrl ctrl) { super(ctrl); }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();

        buildLeftContent(width, height);
        lowerRightContent = buildRightContent();

        String initialBoard = ((CreatorScreenCtrl)ctrl).getBoardToDisplay();
        creatorBoard = new KakuroView(initialBoard, 0, true);
        creatorBoard.setSize(width/2, height - lowerRightContent.getSize().height);
        creatorBoard.setVisible(true);
        creatorBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setUpListener();

        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0,10,0,10);
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

        onResize(width, height);
    }

    private void buildLeftContent(int width, int height) {
        leftContent = new JPanel();
        leftContent.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        blackWhiteSelectors = new JTabbedPane();

        buildSelectorBlack();
        blackWhiteSelectors.addTab("BLACK CELLS", selectorBlack);
        buildSelectorWhite();
        blackWhiteSelectors.addTab("WHITE CELLS", selectorWhite);

        blackWhiteSelectors.addChangeListener(e -> {
            int tab = blackWhiteSelectors.getSelectedIndex();
            ((CreatorScreenCtrl)ctrl).onSelectedTabChanged(tab);
            if (tab == 0) whiteBrushChk.setSelected(false);
            else if (tab == 1) blackBrushChk.setSelected(false);
        });

        JPanel lowerLeft = new JPanel();
        lowerLeft.setLayout(new GridBagLayout());

        tipBox = new JLabel("<html><body>Hi! I'm the TipBox that will help you throughout the process of generating a Kakuro. Thank me later!</body></html>");
        tipBox.setForeground(Color.BLACK);
        tipBox.setHorizontalAlignment(SwingConstants.LEFT);
        tipBox.setVerticalAlignment(SwingConstants.CENTER);
        tipBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel setNameLbl = new JLabel("<html><body>Give it a unique and memorable name ;)</body></html>");
        setNameLbl.setForeground(Color.BLACK);
        setNameLbl.setOpaque(false);
        setNameLbl.setHorizontalAlignment(SwingConstants.LEFT);
        setNameLbl.setVerticalAlignment(SwingConstants.CENTER);

        kakuroName = new JTextField("");

        kakuroStateBtn = new JButton("VALIDATE");
        kakuroStateBtn.setForeground(Palette.HintOrange);
        kakuroStateBtn.addActionListener(e -> ((CreatorScreenCtrl)ctrl).onKakuroStateButtonPressed(kakuroName.getText()));

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener(e -> {
            ((CreatorScreenCtrl)ctrl).onSelectedColor(colorChooser.getColor().getRGB());
        });
        AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
            if(!accp.getDisplayName().equals("Swatches")) {
                colorChooser.removeChooserPanel(accp);
            }
        }
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.setMinimumSize(new Dimension(width*4/11, height/5));
        colorChooser.setPreferredSize(new Dimension(width*4/11, height/5));
        colorChooser.setMaximumSize(new Dimension(width*4/11, height/5));

        horizontalLeftFiller = Box.createRigidArea(new Dimension(width/3, 5));

        constraints.insets = new Insets(5, 20, 5, 20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        lowerLeft.add(setNameLbl, constraints);

        constraints.insets = new Insets(5, 20, 5, 20);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        lowerLeft.add(kakuroName, constraints);

        constraints.insets = new Insets(5, 20, 5, 20);
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        lowerLeft.add(kakuroStateBtn, constraints);

        constraints.insets = new Insets(10, 5, 10, 5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 4;
        constraints.fill = GridBagConstraints.BOTH;
        leftContent.add(blackWhiteSelectors, constraints);

        constraints.gridy = 1;
        constraints.weighty = 2;
        constraints.fill = GridBagConstraints.BOTH;
        leftContent.add(tipBox, constraints);

        constraints.gridy = 2;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.NONE;
        leftContent.add(colorChooser, constraints);

        constraints.gridy = 3;
        constraints.insets.bottom = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        leftContent.add(lowerLeft, constraints);

        constraints.gridy = 4;
        leftContent.add(horizontalLeftFiller, constraints);
    }

    private void buildSelectorBlack() {
        GridBagConstraints constraints = new GridBagConstraints();
        selectorBlack = new JPanel();
        selectorBlack.setLayout(new GridBagLayout());

        JPanel upperSelector = new JPanel();
        upperSelector.setLayout(new GridBagLayout());

        JLabel brushToolLbl = new JLabel("Brush tool");
        brushToolLbl.setForeground(Color.BLACK);
        brushToolLbl.setHorizontalAlignment(SwingConstants.LEFT);
        brushToolLbl.setVerticalAlignment(SwingConstants.CENTER);
        brushToolLbl.setOpaque(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        upperSelector.add(brushToolLbl, constraints);

        blackBrushChk = new JCheckBox();
        blackBrushChk.addItemListener(e -> ((CreatorScreenCtrl)ctrl).setBlackBrushEnabled(e.getStateChange() == ItemEvent.SELECTED));
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

        buildBlackPossibleValues(new Pair<>(new ArrayList<>(), false));
        blackValuesScroll = new JScrollPane(blackPossibleValues);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 4;
        constraints.weighty = 4;
        constraints.fill = GridBagConstraints.BOTH;
        lowerSelector.add(blackValuesScroll, constraints);


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        selectorBlack.add(upperSelector, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.weightx = 1;
        constraints.weighty = 3;
        constraints.fill = GridBagConstraints.BOTH;
        selectorBlack.add(lowerSelector, constraints);
    }

    private void buildBlackPossibleValues(Pair<ArrayList<Integer>, Boolean> allPossibilities) {
        blackPossibleValues = new JPanel();
        int numItems = allPossibilities.first.size() + (allPossibilities.second ? 1 : 0);
        int gridWidth = Math.min(6, numItems);
        if (gridWidth == 0) gridWidth = 1;
        int gridHeight = numItems / gridWidth + (numItems%gridWidth == 0 ? 0 : 1);
        blackPossibleValues.setLayout(new GridLayout(gridHeight, gridWidth, 2, 2));

        if (allPossibilities.second) { //black cell has value, offer to clear it
            JLabel clearLbl = new JLabel("X");
            clearLbl.setForeground(Color.BLACK);
            clearLbl.setBackground(Palette.WarningLightRed);
            clearLbl.setHorizontalAlignment(SwingConstants.CENTER);
            clearLbl.setVerticalAlignment(SwingConstants.CENTER);
            clearLbl.setOpaque(true);
            clearLbl.setBorder(BorderFactory.createLineBorder(Palette.StrongRed));
            clearLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).clearSelectedBlackCellValueClicked();
                }
            });
            clearLbl.setSize(clearLbl.getHeight(), clearLbl.getHeight());
            blackPossibleValues.add(clearLbl);
        }

        int howMany = allPossibilities.first.size();
        for (int i = 0; i < howMany; i++) {
            int value = allPossibilities.first.get(i);

            JLabel valueOptLbl = new JLabel(""+value);
            valueOptLbl.setForeground(Color.BLACK);
            valueOptLbl.setBackground(Palette.SelectionBlue);
            valueOptLbl.setHorizontalAlignment(SwingConstants.CENTER);
            valueOptLbl.setVerticalAlignment(SwingConstants.CENTER);
            valueOptLbl.setOpaque(true);
            valueOptLbl.setBorder(BorderFactory.createLineBorder(Palette.StrongBlue));
            valueOptLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ((CreatorScreenCtrl)ctrl).blackCellSelectValueClicked(value);
                }
            });
            blackPossibleValues.add(valueOptLbl);
        }
    }

    public void updateBlackPossibleValues(Pair<ArrayList<Integer>, Boolean> allPossibilities) {
        buildBlackPossibleValues(allPossibilities);
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
        whiteBrushChk.addItemListener(e -> ((CreatorScreenCtrl)ctrl).setWhiteBrushEnabled(e.getStateChange() == ItemEvent.SELECTED));
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.NONE;
        upperSelector.add(whiteBrushChk, constraints);

        JPanel lowerSelector = new JPanel();
        lowerSelector.setLayout(new GridBagLayout());

        JLabel valueAssigLbl = new JLabel("Forced initial value assignation");
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

        buildWhitePossibleValues(new Pair<>(new ArrayList<>(), false));
        whiteValuesScroll = new JScrollPane(whitePossibleValues);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 4;
        constraints.weighty = 4;
        constraints.fill = GridBagConstraints.BOTH;
        lowerSelector.add(whiteValuesScroll, constraints);


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        selectorWhite.add(upperSelector, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 3;
        constraints.weightx = 1;
        constraints.weighty = 3;
        constraints.fill = GridBagConstraints.BOTH;
        selectorWhite.add(lowerSelector, constraints);
    }

    private void buildWhitePossibleValues(Pair<ArrayList<Integer>, Boolean> allPossibilities) {
        whitePossibleValues = new JPanel();
        int numItems = allPossibilities.first.size() + (allPossibilities.second ? 1 : 0);
        int gridWidth = Math.min(3, numItems);
        if (gridWidth == 0) gridWidth = 1;
        int gridHeight = numItems / gridWidth + (numItems%gridWidth == 0 ? 0 : 1);
        whitePossibleValues.setLayout(new GridLayout(gridHeight, gridWidth, 2, 2));

        if (allPossibilities.second) { //black cell has value, offer to clear it
            JLabel clearLbl = new JLabel("X");
            clearLbl.setForeground(Color.BLACK);
            clearLbl.setBackground(Palette.WarningLightRed);
            clearLbl.setHorizontalAlignment(SwingConstants.CENTER);
            clearLbl.setVerticalAlignment(SwingConstants.CENTER);
            clearLbl.setOpaque(true);
            clearLbl.setBorder(BorderFactory.createLineBorder(Palette.StrongRed));
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
            valueOptLbl.setHorizontalAlignment(SwingConstants.CENTER);
            valueOptLbl.setVerticalAlignment(SwingConstants.CENTER);
            valueOptLbl.setOpaque(true);
            valueOptLbl.setBorder(BorderFactory.createLineBorder(Palette.StrongBlue));
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

        // Export button
        JButton exportBtn = new JButton("EXPORT");
        exportBtn.setForeground(Color.BLACK);
        exportBtn.addActionListener(e -> ((CreatorScreenCtrl)ctrl).onExportButtonClicked());
        constraints.gridx = 0;
        buttonPanel.add(exportBtn, constraints);

        // Fill kakuro button
        JButton fillKakuroBtn = new JButton("FILL IT FOR ME");
        fillKakuroBtn.setForeground(Color.BLACK);
        fillKakuroBtn.addActionListener(e -> ((CreatorScreenCtrl)ctrl).onFillKakuroButtonClicked());
        constraints.gridx = 1;
        buttonPanel.add(fillKakuroBtn, constraints);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setVisible(false);
        constraints.gridx = 2;
        buttonPanel.add(separator, constraints);

        // Clear values button
        JButton clearValuesBtn = new JButton("CLEAR ALL VALUES");
        clearValuesBtn.setForeground(Palette.StrongRed);
        clearValuesBtn.addActionListener(e -> ((CreatorScreenCtrl)ctrl).onClearBoardButtonClicked());
        constraints.gridx = 3;
        buttonPanel.add(clearValuesBtn, constraints);

        return buttonPanel;
    }

    public void updateWhitePossibleValues(Pair<ArrayList<Integer>, Boolean> allPossibilities) {
        buildWhitePossibleValues(allPossibilities);
        whiteValuesScroll.setViewportView(whitePossibleValues);
    }

    public void setTab(int tabIdx) {
        blackWhiteSelectors.setSelectedIndex(tabIdx);
    }

    // FIXME: the resize is only a patch to solve visual problems when turning a cell to black/white,
    //  it only works if there is a resize for some reason

    public void selectWhiteCell(int r, int c) {
        creatorBoard.setWhiteCellSelectedColor(r, c, Palette.SelectionBlue);
    }
    public void unselectWhiteCell(int r, int c) {
        creatorBoard.unselectWhiteCell(r, c);
    }
    public void setValueWhiteCell(int r, int c, int value) {
        creatorBoard.setWhiteCellValue(r, c, value);
    }
    public void setNotationWhiteCell(int r, int c, int notations) { creatorBoard.setWhiteCellNotations(r, c, notations); }
    public void setBlackCellColor(Color c) {
        creatorBoard.setBlackCellColor(c);
        onResize(contents.getWidth(), contents.getHeight());
    }
    public void selectBlackCell(int r, int c, int s) {
        creatorBoard.setBlackCellSelectedColor(r, c, s, Palette.SelectionBlue);
        onResize(contents.getWidth(), contents.getHeight());
    }
    public void unselectBlackCell(int r, int c, int s) {
        creatorBoard.unselectBlackCell(r, c, s);
        onResize(contents.getWidth(), contents.getHeight());
    }

    public void setValueBlackCell(int r, int c, int s, int value) {
        creatorBoard.setBlackCellValue(r, c, s, value);
        onResize(contents.getWidth(), contents.getHeight());
    }

    public void selectModified(int r, int c, int s) {
        if (s == CreatorScreenCtrl.WHITE_CELL) {
            creatorBoard.setWhiteCellSelectedColor(r, c, Palette.HintGreen);
        } else {
            creatorBoard.setBlackCellSelectedColor(r, c, s, Palette.HintGreen);
            onResize(contents.getWidth(), contents.getHeight());
        }
    }

    public void selectConflictive(int r, int c, int s) {
        if (s == CreatorScreenCtrl.WHITE_CELL) {
            creatorBoard.setWhiteCellSelectedColor(r, c, Palette.WarningLightRed);
        } else {
            creatorBoard.setBlackCellSelectedColor(r, c, s, Palette.WarningLightRed);
            onResize(contents.getWidth(), contents.getHeight());
        }
    }

    public void prepareCellToWhite(int r, int c) {
        creatorBoard.prepareCellToTurnWhite(r, c);
        onResize(contents.getWidth(), contents.getHeight());
    }
    public void setCellToWhite(int r, int c) {
        creatorBoard.setCellToWhite(r, c);
        onResize(contents.getWidth(), contents.getHeight());
    }
    public void prepareCellToBlack(int r, int c) {
        creatorBoard.prepareCellToTurnBlack(r, c);
        //onResize(contents.getWidth(), contents.getHeight());
    }
    public void setCellToBlack(int r, int c) {
        creatorBoard.setCellToBlack(r, c);
        onResize(contents.getWidth(), contents.getHeight());
    }

    public void updateWholeBoardFromString(String b) {
        creatorBoard.updateFromString(b, true);
        onResize(contents.getWidth(), contents.getHeight());
    }

    public void setTipBoxText(String tip) {
        tipBox.setText("<html><body>"+tip+"</body></html>");
        tipBox.revalidate();
    }

    public void setKakuroStateBtn(boolean toPublish) {
        if (toPublish) {
            kakuroStateBtn.setText("PUBLISH");
            kakuroStateBtn.setForeground(Palette.StrongGreen);
        } else {
            kakuroStateBtn.setText("VALIDATE");
            kakuroStateBtn.setForeground(Palette.HintOrange);
        }
        kakuroStateBtn.revalidate();
    }

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        horizontalLeftFiller = Box.createRigidArea(new Dimension(width/3, 5));
        Component[] leftComp = leftContent.getComponents();
        leftComp[leftComp.length -1] = horizontalLeftFiller;
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
            public void onBlackCellViewEntered(int row, int col) {
                ((CreatorScreenCtrl)ctrl).onMouseEntered(row, col);
            }

            @Override
            public void onWhiteCellViewEntered(int row, int col) {
                ((CreatorScreenCtrl)ctrl).onMouseEntered(row, col);
            }

            @Override
            public void onCellInBoardPressed(int row, int col) {
                ((CreatorScreenCtrl)ctrl).onMousePressed(row, col);
            }

            @Override
            public void onCellInBoardReleased(int row, int col) {
                ((CreatorScreenCtrl)ctrl).onMouseReleased();
            }

            @Override
            public void onListenerDetached() { }
        });
    }
}
