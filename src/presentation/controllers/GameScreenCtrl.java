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
        selectedPos = new Pair<>(-1, -1);
    }

    @Override
    public void build(int width, int height) {
        screen = new GameScreen(this);
        super.build(width, height);
    }


    public void kakuroSolvedCorrectly() {
        // show alert indicating kakuro solved, the time, top 3 or whatever
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
    }
    public void setSelectedPos(int row, int col) {
        System.out.println("Selecting: " + row + ", "+ col);
        if (selectedPos.first != -1) ((GameScreen)screen).unselectWhiteCell(selectedPos.first, selectedPos.second);
        selectedPos.first = row; selectedPos.second = col;
        ((GameScreen)screen).selectWhiteCell(selectedPos.first, selectedPos.second);
    }

    public void valueClicked(int value) {
        if (selectedPos.first == -1) return;
        if (notationsMode) {
            int response = game.toggleNotation(selectedPos.first, selectedPos.second, value);
            if (response == -1) return;
            // set notations
        } else {
            if (game.playMove(selectedPos.first, selectedPos.second, value)) {
                // set new value to selected cell.
            }
        }
    }


    @Override
    public void onFocusRegained(int width, int height) {
        // In the case of the GameScreen this method should probably never get called.
    }
}
