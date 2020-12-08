package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.domain.controllers.GameCtrl;
import src.presentation.screens.GameScreen;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import java.util.ArrayList;

public class GameScreenCtrl extends AbstractScreenCtrl {
    public static final int WHITE_CELL = -1;
    public static final int BLACK_SECTION_TOP = KakuroView.BLACK_SECTION_TOP;
    public static final int BLACK_SECTION_BOTTOM = KakuroView.BLACK_SECTION_BOTTOM;
    public static final int BLACK_SECTION_LEFT = KakuroView.BLACK_SECTION_LEFT;
    public static final int BLACK_SECTION_RIGHT = KakuroView.BLACK_SECTION_RIGHT;

    private GameCtrl game;
    private String boardToDisplay;
    private boolean notationsMode;
    private String notationsToDisplay;
    private ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflictiveCoord;
    private Pair<Integer, Integer> selectedPos;

    public GameScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public void setUpGame(GameCtrl gameInstance) {
        this.game = gameInstance;
        boardToDisplay = game.gameSetUp(this);
        notationsMode = false;
        conflictiveCoord = new ArrayList<>();
        selectedPos = new Pair<>(-1, -1);
    }

    @Override
    public void build(int width, int height) {
        screen = new GameScreen(this);
        super.build(width, height);
    }

    public void kakuroSolvedCorrectly() {
        // show alert indicating kakuro solved, the time, top 3 or whatever
        System.out.println("SOLVED!!!");
    }

    public String getBoardToDisplay() {
        return boardToDisplay;
    }

    public void setBoardToDisplay(String board) {
        // change values in view
    }
    public void setNotationsToDisplay(String board) {
        // change notations in view
    }
    public void setConflictiveCoord(ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflicts) {
        // erase previous conflicts, mark new conflicts
        unselectConflictiveCoord();
        for (Pair<Pair<Integer, Integer>, Integer> cc : conflicts) {
            ((GameScreen)screen).selectConflictive(cc.first.first, cc.first.second, cc.second);
        }
        conflictiveCoord = conflicts;
    }
    private void unselectConflictiveCoord() {
        for (Pair<Pair<Integer, Integer>, Integer> cc : conflictiveCoord) {
            if (cc.second == WHITE_CELL) {
                ((GameScreen)screen).unselectWhiteCell(cc.first.first, cc.first.second);
            } else {
                ((GameScreen)screen).unselectBlackCell(cc.first.first, cc.first.second, cc.second);
            }
        }
    }
    public void setSelectedPos(int row, int col) {
        System.out.println("Selecting: " + row + ", "+ col);
        unselectConflictiveCoord();
        if (selectedPos.first != -1) ((GameScreen)screen).unselectWhiteCell(selectedPos.first, selectedPos.second);
        selectedPos.first = row; selectedPos.second = col;
        ((GameScreen)screen).selectWhiteCell(selectedPos.first, selectedPos.second);
    }

    public void valueClicked(int value) {
        if (selectedPos.first == -1) return;
        unselectConflictiveCoord();
        if (notationsMode) {
            int response = game.toggleNotation(selectedPos.first, selectedPos.second, value);
            if (response == -1) return;
            ((GameScreen)screen).setNotationWhiteCell(selectedPos.first, selectedPos.second, response);
        } else {
            int response = game.playMove(selectedPos.first, selectedPos.second, value);
            if (response != -1) {
                ((GameScreen)screen).setValueWhiteCell(selectedPos.first, selectedPos.second, response);
            }
        }
    }

    public void toggleNotationsMode() {
        notationsMode = !notationsMode;
    }

    public void clearWhiteCell() {
        if (selectedPos.first == -1) return;
        if (!game.clearWhiteCell(selectedPos.first, selectedPos.second)) return;
        ((GameScreen)screen).setValueWhiteCell(selectedPos.first, selectedPos.second, 0);
        ((GameScreen)screen).setNotationWhiteCell(selectedPos.first, selectedPos.second, 0);
    }


    @Override
    public void onFocusRegained(int width, int height) {
        // In the case of the GameScreen this method should probably never get called.
    }
}
