package src.presentation.screens;

import src.presentation.controllers.GameScreenCtrl;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameScreen extends AbstractScreen {

    KakuroView gameBoard;

    JPanel leftContent;

    JPanel rightContent;

    JButton[] valuePanel;
    JButton notationModeBtn;
    JButton clearCellBtn;
    JPanel movesPanel;
    JScrollPane movesPanelScroll;

    public GameScreen(GameScreenCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public void build(int width, int height) {
        super.build(width, height);
        contents = new JPanel();

        leftContent = new JPanel();
        leftContent.add(new JLabel("left"));
        leftContent.setSize(width/4, height);
        leftContent.setBackground(Color.CYAN);
        leftContent.setVisible(true);

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
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        contents.add(gameBoard, constraints);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 0;
        contents.add(rightContent, constraints);
        contents.setVisible(true);
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
        undoBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).undoMovement();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        movementsPanel.add(undoBtn, constraints);

        // Btn redo
        JButton redoBtn = new JButton("Re-do");
        redoBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).redoMovement();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 5;
        movementsPanel.add(redoBtn, constraints);

        // Btn redo
        JButton markBtn = new JButton("Mark");
        markBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).toggleMark();
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 5;
        movementsPanel.add(markBtn, constraints);

        // Btn reset
        JButton resetBtn = new JButton("Reset game");
        resetBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).resetGame();
            }
        });
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
                public void mouseClicked(MouseEvent e) {
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
            valuePanel[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ((GameScreenCtrl)ctrl).valueClicked(val);
                }
            });
            constraints.gridx = i%5;
            constraints.gridy = i/5;
            buttonPanel.add(valuePanel[i], constraints);
        }
        notationModeBtn = new JButton("N");
        notationModeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).toggleNotationsMode();
            }
        });
        constraints.gridx = 4;
        buttonPanel.add(notationModeBtn, constraints);
        clearCellBtn = new JButton("X");
        clearCellBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).clearWhiteCell();
            }
        });
        constraints.gridy = 2;
        buttonPanel.add(clearCellBtn, constraints);
        return buttonPanel;
    }

    public void toggleMovementMark(int moveIdx) {
        int idx = movesPanel.getComponents().length - moveIdx;
        if (idx < 0) return;
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
        int remainingWidth = width - leftContent.getWidth() - rightContent.getWidth();
        gameBoard.setSize(remainingWidth, height);
    }

    public void selectWhiteCell(int r, int c) {
        gameBoard.setWhiteCellSelectedColor(r, c, new Color(180, 180, 255));
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
    public void unselectBlackCell(int r, int c, int s) {
        gameBoard.unselectBlackCell(r, c, s);
    }
    public void selectConflictive(int r, int c, int s) {
        if (s == GameScreenCtrl.WHITE_CELL) {
            gameBoard.setWhiteCellSelectedColor(r, c, new Color(255, 160, 160));
        } else {
            gameBoard.setBlackCellSelectedColor(r, c, s, new Color(255, 160, 160));
        }
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
