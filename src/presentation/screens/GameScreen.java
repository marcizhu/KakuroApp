package src.presentation.screens;

import src.presentation.controllers.GameScreenCtrl;
import src.presentation.utils.Palette;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameScreen extends AbstractScreen {
    KakuroView gameBoard;

    JPanel leftContent;

    JCheckBox redBtnPanChk;
    JCheckBox showCombChk;
    JCheckBox autoEraseChk;

    JPanel rightContent;

    JButton[] valuePanel;
    JButton notationModeBtn;
    JButton clearCellBtn;
    JPanel movesPanel;
    JScrollPane movesPanelScroll;

    JLabel rowOptionsLbl;
    JLabel colOptionsLbl;

    public GameScreen(GameScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();

        buildLeftContent(width, height);
        buildRightContent(width, height);

        String initialBoard = ((GameScreenCtrl)ctrl).getBoardToDisplay();
        gameBoard = new KakuroView(initialBoard, true);
        gameBoard.setSize(width/2, height);
        gameBoard.setVisible(true);
        setUpListener();

        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        contents.add(leftContent, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        contents.add(gameBoard, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 0;
        contents.add(rightContent, constraints);
        contents.setVisible(true);
    }

    public void buildLeftContent(int width, int height) {
        leftContent = new JPanel();
        leftContent.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 10, 5);

        JPanel helpOptionsPanel = buildHelpOptionsPanel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        leftContent.add(helpOptionsPanel, constraints);

        JPanel bigButtonsPanel = buildBigButtonsPanel();
        constraints.gridy = 1;
        leftContent.add(bigButtonsPanel, constraints);

        leftContent.setSize(width/4, height);
        leftContent.setVisible(true);
    }

    public JPanel buildHelpOptionsPanel() {
        JPanel helpOptionsPanel = new JPanel();
        helpOptionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 5, 10, 5);

        // Help options title
        JLabel title = new JLabel("HELP OPTIONS:");
        title.setForeground(Color.BLACK);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        helpOptionsPanel.add(title, constraints);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        constraints.gridy = 1;
        helpOptionsPanel.add(separator, constraints);

        // First help option label
        JLabel redNumPadLbl = new JLabel("<html><body>Show the values used in row/column in red in the number pad</body></html>");
        redNumPadLbl.setForeground(Color.BLACK);
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        helpOptionsPanel.add(redNumPadLbl, constraints);

        // Second help option label
        JLabel showCombLbl = new JLabel("<html><body>Show all combinations for the selected cell's row and column</body></html>");
        showCombLbl.setForeground(Color.BLACK);
        constraints.gridy = 3;
        helpOptionsPanel.add(showCombLbl, constraints);

        // Third help option label
        JLabel autoEraseLbl = new JLabel("<html><body>Auto erase notations when value is assigned to a cell in the row or column</body></html>");
        autoEraseLbl.setForeground(Color.BLACK);
        constraints.gridy = 4;
        helpOptionsPanel.add(autoEraseLbl, constraints);

        // First help option checkbox
        redBtnPanChk = new JCheckBox();
        redBtnPanChk.addItemListener(e -> ((GameScreenCtrl)ctrl).setHelpRedButtonPanel(e.getStateChange() == ItemEvent.SELECTED));
        constraints.gridx = 3;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        helpOptionsPanel.add(redBtnPanChk, constraints);

        // Second help option checkbox
        showCombChk = new JCheckBox();
        showCombChk.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            if (selected) {
                rowOptionsLbl.setVisible(true);
                colOptionsLbl.setVisible(true);
            } else {
                rowOptionsLbl.setVisible(false);
                colOptionsLbl.setVisible(false);
            }
            ((GameScreenCtrl)ctrl).setHelpShowCombinations(selected);
            rowOptionsLbl.revalidate();
            colOptionsLbl.revalidate();
        });
        constraints.gridy = 3;
        helpOptionsPanel.add(showCombChk, constraints);

        // First help option checkbox
        autoEraseChk = new JCheckBox();
        autoEraseChk.addItemListener(e -> ((GameScreenCtrl)ctrl).setHelpAutoEraseNotations(e.getStateChange() == ItemEvent.SELECTED));
        constraints.gridy = 4;
        helpOptionsPanel.add(autoEraseChk, constraints);

        return helpOptionsPanel;
    }

    public JPanel buildBigButtonsPanel() {
        JPanel bigButtonsPanel = new JPanel();
        bigButtonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 5, 10, 5);
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;

        // Button export
        /*JButton exportBtn = new JButton("EXPORT MY PROGRESS");
        exportBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).onExportClick());
        constraints.gridy = 0;
        bigButtonsPanel.add(exportBtn, constraints);*/

        // Button hint
        JButton hintBtn = new JButton("I'M STUCK, GIVE ME A HINT");
        hintBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).onHintClick());
        constraints.gridy = 0;
        bigButtonsPanel.add(hintBtn, constraints);

        // Button hint
        JButton solveBtn = new JButton("I GIVE UP, SOLVE IT FOR ME");
        solveBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).onSolveClick());
        constraints.gridy = 1;
        bigButtonsPanel.add(solveBtn, constraints);

        return bigButtonsPanel;
    }

    private void buildRightContent(int width, int height) {
        rightContent = new JPanel();
        rightContent.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 10, 5);

        JPanel notationsPanel = buildNotationsPanel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 4;
        constraints.fill = GridBagConstraints.BOTH;
        rightContent.add(notationsPanel, constraints);

        JPanel btnPanel = buildButtonPanel();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 2;
        constraints.fill = GridBagConstraints.BOTH;
        rightContent.add(btnPanel, constraints);

        rightContent.setSize(width/4, height);
        rightContent.setBackground(Color.GRAY);
        rightContent.setVisible(true);
    }

    private JPanel buildNotationsPanel() {
        JPanel movementsPanel = new JPanel();
        movementsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel movementsTitle = new JLabel("MOVEMENTS");
        movementsTitle.setForeground(Color.BLACK);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        movementsPanel.add(movementsTitle, constraints);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        constraints.gridy = 1;
        movementsPanel.add(separator, constraints);

        // Header num
        JLabel numOfMove = new JLabel("Num.");
        numOfMove.setForeground(Color.BLACK);
        numOfMove.setHorizontalAlignment(SwingConstants.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        movementsPanel.add(numOfMove, constraints);
        // Header coord
        JLabel coordOfMove = new JLabel("Coord.");
        coordOfMove.setForeground(Color.BLACK);
        coordOfMove.setHorizontalAlignment(SwingConstants.CENTER);
        constraints.gridx = 1;
        movementsPanel.add(coordOfMove, constraints);
        // Header From
        JLabel prevToMove = new JLabel("From");
        prevToMove.setForeground(Color.BLACK);
        prevToMove.setHorizontalAlignment(SwingConstants.CENTER);
        constraints.gridx = 2;
        movementsPanel.add(prevToMove, constraints);
        // Header To
        JLabel toMove = new JLabel("To");
        toMove.setForeground(Color.BLACK);
        toMove.setHorizontalAlignment(SwingConstants.CENTER);
        constraints.gridx = 3;
        movementsPanel.add(toMove, constraints);
        // Header Marked
        JLabel markedMove = new JLabel("Marked");
        markedMove.setForeground(Color.BLACK);
        markedMove.setHorizontalAlignment(SwingConstants.CENTER);
        constraints.gridx = 4;
        movementsPanel.add(markedMove, constraints);

        // Moves panel
        buildMovesPanel(-1);
        movesPanelScroll = new JScrollPane(movesPanel);
        movesPanelScroll.getVerticalScrollBar().setUnitIncrement(20);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 5;
        constraints.weighty = 5;
        constraints.fill = GridBagConstraints.BOTH;
        movementsPanel.add(movesPanelScroll, constraints);

        // Separator
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(Color.BLACK);
        constraints.gridy = 4;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        movementsPanel.add(separator2, constraints);

        // Btn undo
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).undoMovement());
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        movementsPanel.add(undoBtn, constraints);

        // Btn redo
        JButton redoBtn = new JButton("Re-do");
        redoBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).redoMovement());
        constraints.gridx = 1;
        constraints.gridy = 5;
        movementsPanel.add(redoBtn, constraints);

        // Btn redo
        JButton markBtn = new JButton("Mark");
        markBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).toggleMark());
        constraints.gridx = 2;
        constraints.gridy = 5;
        movementsPanel.add(markBtn, constraints);

        // Btn reset
        JButton resetBtn = new JButton("Reset game");
        resetBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).resetGame());
        resetBtn.setForeground(Color.RED);
        constraints.gridx = 3;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        movementsPanel.add(resetBtn, constraints);

        movementsPanel.setBackground(Color.LIGHT_GRAY);
        return movementsPanel;
    }

    public void updateMovesPanel(int selectedMove) {
        // remember marks:
        ArrayList<Integer> marked = new ArrayList<>();
        Component[] moves = movesPanel.getComponents();
        for (int i = 0; i < moves.length; i++) {
            if ((((JPanel)movesPanel.getComponents()[i]).getComponents()[4]).isVisible()) {
                marked.add(moves.length - i);
            }
        }
        buildMovesPanel(selectedMove);
        // reassign marks:
        for (int m : marked) {
            toggleMovementMark(m);
        }
        movesPanelScroll.setViewportView(movesPanel);
    }

    private void buildMovesPanel(int selectedMove) {
        ArrayList<Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>> allMoves = ((GameScreenCtrl)ctrl).getMovementList();
        if (selectedMove == -1) selectedMove = allMoves.size();
        movesPanel = new JPanel();
        movesPanel.setLayout(new GridLayout(allMoves.size(),1));
        int howMany = allMoves.size();
        for (int i = howMany-1; i >= 0; i--) {
            JPanel singleMovePanel = new JPanel();
            singleMovePanel.setLayout(new GridLayout(1,5));
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> m = allMoves.get(i);
            JLabel num = new JLabel("#"+m.first+":");
            num.setForeground(Color.BLACK);
            num.setHorizontalAlignment(SwingConstants.CENTER);
            num.setOpaque(false);
            JLabel coord = new JLabel(""+m.second.first.first+","+m.second.first.second);
            coord.setForeground(Color.BLACK);
            coord.setHorizontalAlignment(SwingConstants.CENTER);
            coord.setOpaque(false);
            JLabel from = new JLabel(""+m.second.second.first);
            from.setForeground(Color.BLACK);
            from.setHorizontalAlignment(SwingConstants.CENTER);
            from.setOpaque(false);
            JLabel to = new JLabel(""+m.second.second.second);
            to.setForeground(Color.BLACK);
            to.setHorizontalAlignment(SwingConstants.CENTER);
            to.setOpaque(false);
            JLabel mark = new JLabel("");
            mark.setHorizontalAlignment(SwingConstants.CENTER);
            mark.setOpaque(true);
            mark.setVisible(false);
            singleMovePanel.add(num);
            singleMovePanel.add(coord);
            singleMovePanel.add(from);
            singleMovePanel.add(to);
            singleMovePanel.add(mark);
            if (m.first == selectedMove) singleMovePanel.setBackground(Color.GRAY);
            singleMovePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    ((GameScreenCtrl)ctrl).selectMovement(m.first);
                }
            });
            movesPanel.add(singleMovePanel);
        }
        movesPanel.setAlignmentY(SwingConstants.TOP);
        movesPanel.setSize(movesPanel.getMinimumSize());
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        valuePanel = new JButton[9];
        constraints.weightx = 1;
        constraints.weighty = 1;
        for (int i = 0; i < 9; i++) {
            final int val = i+1;
            valuePanel[i] = new JButton(""+(val));
            valuePanel[i].setFocusable(false);
            valuePanel[i].setForeground(Color.BLACK);
            valuePanel[i].addActionListener(e -> ((GameScreenCtrl)ctrl).valueClicked(val));
            constraints.gridx = i%5;
            constraints.gridy = i/5;
            buttonPanel.add(valuePanel[i], constraints);
        }
        notationModeBtn = new JButton("N");
        notationModeBtn.setFocusable(false);
        notationModeBtn.setForeground(Color.BLACK);
        notationModeBtn.addActionListener(e -> {
            notationModeBtn.setBorderPainted(!notationModeBtn.isBorderPainted());
            notationModeBtn.setForeground(notationModeBtn.getForeground() == Color.BLACK ? Color.GRAY : Color.BLACK);
            ((GameScreenCtrl)ctrl).toggleNotationsMode();
        });
        constraints.gridx = 4;
        buttonPanel.add(notationModeBtn, constraints);
        clearCellBtn = new JButton("X");
        clearCellBtn.setFocusable(false);
        clearCellBtn.setForeground(Color.BLACK);
        clearCellBtn.addActionListener(e -> ((GameScreenCtrl)ctrl).clearWhiteCell());
        constraints.gridy = 2;
        buttonPanel.add(clearCellBtn, constraints);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridBagLayout());

        rowOptionsLbl = new JLabel("");
        rowOptionsLbl.setForeground(Color.BLACK);
        rowOptionsLbl.setOpaque(false);
        rowOptionsLbl.setVisible(false);
        rowOptionsLbl.setAlignmentY(SwingConstants.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        optionsPanel.add(rowOptionsLbl, constraints);

        colOptionsLbl = new JLabel("");
        colOptionsLbl.setForeground(Color.BLACK);
        colOptionsLbl.setOpaque(false);
        colOptionsLbl.setVisible(false);
        colOptionsLbl.setAlignmentY(SwingConstants.CENTER);
        constraints.gridy = 1;
        optionsPanel.add(colOptionsLbl, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.weightx = 4;
        constraints.fill = GridBagConstraints.BOTH;

        buttonPanel.add(optionsPanel, constraints);

        return buttonPanel;
    }

    public void toggleMovementMark(int moveIdx) {
        int idx = movesPanel.getComponents().length - moveIdx;
        if (idx < 0 || movesPanel.getComponents().length == 0) return;
        JLabel mark = (JLabel)((JPanel)movesPanel.getComponents()[idx]).getComponents()[4];
        mark.setVisible(!mark.isVisible());
        if (mark.isVisible()) mark.setBackground(Color.ORANGE);
        mark.revalidate();
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onResize(int width, int height) {
        contents.setSize(width, height);
        leftContent.setSize(width/4, height);
        rightContent.setSize(width/4, height);
        int remainingWidth = width - leftContent.getWidth() - rightContent.getWidth();
        gameBoard.setSize(remainingWidth, height);
    }

    public void selectWhiteCell(int r, int c) {
        gameBoard.setWhiteCellSelectedColor(r, c, Palette.SelectionBlue);
    }
    public void selectWhiteCellColor(int r, int c, Color col) {
        gameBoard.setWhiteCellSelectedColor(r, c, col);
    }
    public void unselectWhiteCell(int r, int c) {
        gameBoard.unselectWhiteCell(r, c);
    }
    public void setValueWhiteCell(int r, int c, int value) {
        gameBoard.setWhiteCellValue(r, c, value);
    }
    public void setNotationWhiteCell(int r, int c, int notations) {
        gameBoard.setWhiteCellNotations(r, c, notations);
    }

    // FIXME: the resize is only a patch to solve visual problems when turning a cell to black/white,
    //  it only works if there is a resize for some reason
    public void unselectBlackCell(int r, int c, int s) {
        gameBoard.unselectBlackCell(r, c, s);
        onResize(contents.getWidth(), contents.getHeight());
    }
    public void selectConflictive(int r, int c, int s) {
        if (s == GameScreenCtrl.WHITE_CELL) {
            gameBoard.setWhiteCellSelectedColor(r, c, Palette.WarningLightRed);
        } else {
            gameBoard.setBlackCellSelectedColor(r, c, s, Palette.WarningLightRed);
            onResize(contents.getWidth(), contents.getHeight());
        }
    }
    public void tintValuePanelButtonText(int value, Color tint) {
        valuePanel[value-1].setForeground(tint);
        valuePanel[value-1].revalidate();
    }

    public void setOptionsLblText(String rowTxt, String colTxt) {
        rowOptionsLbl.setText("<html><body>"+rowTxt+"</body></html>");
        colOptionsLbl.setText("<html><body>"+colTxt+"</body></html>");
        rowOptionsLbl.revalidate();
        colOptionsLbl.revalidate();
    }

    private void setUpListener() {
        gameBoard.setBoardMouseEventListener(new KakuroView.BoardMouseEventListener() {
            @Override
            public void onBlackCellViewClicked(int row, int col, int section) { }

            @Override
            public void onWhiteCellViewClicked(int row, int col) {
                ((GameScreenCtrl)ctrl).setSelectedPos(row, col);
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
