package src.presentation.screens;

import src.presentation.controllers.GameScreenCtrl;
import src.presentation.views.KakuroView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreen extends AbstractScreen {

    KakuroView gameBoard;

    JPanel leftContent;

    JPanel rightContent;
    JButton[] valuePanel;
    JButton notationModeBtn;
    JButton clearCellBtn;

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

        rightContent = new JPanel();
        rightContent.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
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
            rightContent.add(valuePanel[i], constraints);
        }
        notationModeBtn = new JButton("N");
        notationModeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).toggleNotationsMode();
            }
        });
        constraints.gridx = 4;
        rightContent.add(notationModeBtn, constraints);
        clearCellBtn = new JButton("X");
        clearCellBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((GameScreenCtrl)ctrl).clearWhiteCell();
            }
        });
        constraints.gridy = 2;
        rightContent.add(clearCellBtn, constraints);
        rightContent.setSize(width/4, height);
        rightContent.setBackground(Color.GREEN);
        rightContent.setVisible(true);

        String initialBoard = ((GameScreenCtrl)ctrl).getBoardToDisplay();
        gameBoard = new KakuroView(initialBoard, true);
        gameBoard.setSize(width/2, height);
        gameBoard.setVisible(true);
        setUpListener();

        contents.setLayout(new GridBagLayout());
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
            gameBoard.setWhiteCellSelectedColor(r, c, new Color(255, 180, 180));
        } else {
            gameBoard.setBlackCellSelectedColor(r, c, s, new Color(255, 180, 180));
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
