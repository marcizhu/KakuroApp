package src.presentation.screens;

import src.presentation.controllers.GameScreenCtrl;
import src.presentation.views.KakuroView;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends AbstractScreen {

    KakuroView gameBoard;
    JPanel leftContent;
    JPanel rightContent;

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
        rightContent.add(new JLabel("right"));
        rightContent.setSize(width/4, height);
        rightContent.setBackground(Color.GREEN);
        rightContent.setVisible(true);

        String initialBoard = ((GameScreenCtrl)ctrl).getBoardToDisplay();
        gameBoard = new KakuroView(initialBoard, true);
        gameBoard.setSize(width/2, height);
        gameBoard.setVisible(true);
        setUpListener();

        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
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
