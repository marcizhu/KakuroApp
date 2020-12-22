package src.presentation.controllers;

import src.domain.controllers.DomainCtrl;
import src.domain.controllers.GameplayCtrl;
import src.presentation.screens.GameScreen;
import src.presentation.utils.Dialogs;
import src.presentation.utils.Palette;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import java.awt.*;
import java.util.ArrayList;

public class GameScreenCtrl extends AbstractScreenCtrl {
    public static final int WHITE_CELL = -1;
    public static final int BLACK_SECTION_TOP = KakuroView.BLACK_SECTION_TOP;
    public static final int BLACK_SECTION_BOTTOM = KakuroView.BLACK_SECTION_BOTTOM;
    public static final int BLACK_SECTION_LEFT = KakuroView.BLACK_SECTION_LEFT;
    public static final int BLACK_SECTION_RIGHT = KakuroView.BLACK_SECTION_RIGHT;

    private GameplayCtrl game;
    private String boardToDisplay;
    private boolean notationsMode;
    private ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflictiveCoord;
    private Pair<Integer, Integer> selectedPos;

    private boolean ignoreDestroy;

    public GameScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public void setUpGame(GameplayCtrl gameInstance) {
        this.game = gameInstance;
        boardToDisplay = game.gameSetUp(this);
        notationsMode = false;
        conflictiveCoord = new ArrayList<>();
        selectedPos = new Pair<>(-1, -1);
        ignoreDestroy = false;
    }

    public int getColorCode() { return game.getColorCode(); }

    @Override
    public void build(int width, int height) {
        screen = new GameScreen(this);
        super.build(width, height);
        game.fetchInitialNotations();
    }

    public String getBoardToDisplay() {
        return boardToDisplay;
    }

    public ArrayList<Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>> getMovementList() {
        return game.getMovementList();
    }

    public void setBoardToDisplay(ArrayList<Pair<Pair<Integer, Integer>, Integer>> whiteCellValues) {
        Pair<Integer, Integer> boardSize = game.getBoardSize();
        for (int i = 0; i < boardSize.first; i++) {
            for (int j = 0; j < boardSize.second; j++) {
                ((GameScreen) screen).setValueWhiteCell(i, j, 0);
                ((GameScreen) screen).setNotationWhiteCell(i, j, 0);
            }
        }
        for (Pair<Pair<Integer, Integer>, Integer> p : whiteCellValues)
            ((GameScreen)screen).setValueWhiteCell(p.first.first, p.first.second, p.second);
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
                if (cc.first.first == selectedPos.first && cc.first.second == selectedPos.second)
                    ((GameScreen)screen).selectWhiteCell(selectedPos.first, selectedPos.second);
            } else {
                ((GameScreen)screen).unselectBlackCell(cc.first.first, cc.first.second, cc.second);
            }
        }
    }

    public void setSelectedPos(int row, int col) {
        unselectConflictiveCoord();
        if (selectedPos.first != -1) ((GameScreen)screen).unselectWhiteCell(selectedPos.first, selectedPos.second);
        selectedPos.first = row; selectedPos.second = col;
        ((GameScreen)screen).selectWhiteCell(selectedPos.first, selectedPos.second);
        game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
    }

    public void selectMovement(int moveIdx) {
        if (game.selectMove(moveIdx)) {
            ((GameScreen)screen).updateMovesPanel(moveIdx);
            game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
        }
    }

    public void undoMovement() {
        if (game.undoMove()) {
            ((GameScreen)screen).updateMovesPanel(game.getCurrentMoveIdx());
            game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
        }
    }

    public void redoMovement() {
        if (game.redoMove()) {
            ((GameScreen)screen).updateMovesPanel(game.getCurrentMoveIdx());
            game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
        }
    }

    public void resetGame() {
        if (!Dialogs.showYesNoOptionDialog("This action will remove all your progress int this game and will leave the board in \"initial state\".", "Are you sure?"))
            return;
        game.resetGame();
        unselectConflictiveCoord();
        ((GameScreen)screen).updateMovesPanel(0);
        game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
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
                ((GameScreen)screen).updateMovesPanel(game.getCurrentMoveIdx());
                ((GameScreen)screen).setValueWhiteCell(selectedPos.first, selectedPos.second, response);
                game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
            }
        }
    }

    public void toggleNotationsMode() {
        notationsMode = !notationsMode;
    }

    public void clearWhiteCell() {
        if (selectedPos.first == -1) return;
        if (!game.clearWhiteCell(selectedPos.first, selectedPos.second)) return;
        ((GameScreen)screen).updateMovesPanel(game.getCurrentMoveIdx());
        ((GameScreen)screen).setValueWhiteCell(selectedPos.first, selectedPos.second, 0);
        ((GameScreen)screen).setNotationWhiteCell(selectedPos.first, selectedPos.second, 0);
        game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
    }

    public void toggleMark() {
        ((GameScreen)screen).toggleMovementMark(game.getCurrentMoveIdx());
    }

    public void setHelpRedButtonPanel(boolean checked) {
        game.setUsedValuesHelpIsActive(checked);
        if (selectedPos.first == -1) return;
        if (checked) game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);
        else clearMarkedButtonPanel();
    }
    public void markButtonPanelInRed(int valuesUsed, int selectedValue) {
        for (int i = 0; i < 9; i++) {
            if (i+1 == selectedValue) ((GameScreen)screen).tintValuePanelButtonText(i+1, Palette.StrongBlue);
            else if ((valuesUsed & (1<<i)) != 0) {
                ((GameScreen)screen).tintValuePanelButtonText(i+1, Palette.StrongRed);
            }
            else ((GameScreen)screen).tintValuePanelButtonText(i+1, Color.BLACK);
        }
    }
    private void clearMarkedButtonPanel() {
        for (int i = 0; i < 9; i++) {
            ((GameScreen) screen).tintValuePanelButtonText(i + 1, Color.BLACK);
        }
    }

    public void setHelpShowCombinations(boolean checked) {
        game.setCombinationsHelpIsActive(checked);
        if (selectedPos.first == -1) return;
        if (checked) game.getHelpOptionsAtSelect(selectedPos.first, selectedPos.second);

    }
    public void setShowCombinations(String rowComb, String colComb) {
        ((GameScreen)screen).setOptionsLblText("Row: "+rowComb, "Column " + colComb);
    }

    public void setHelpAutoEraseNotations(boolean checked) {
        game.setAutoEraseHelpIsActive(checked);
    }
    public void setNotations(ArrayList<Pair<Pair<Integer, Integer>, Integer>> notations) {
        for (Pair<Pair<Integer, Integer>, Integer> n : notations) {
            ((GameScreen) screen).setNotationWhiteCell(n.first.first, n.first.second, n.second);
        }
    }

    /*
    public void onExportClick() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export kakuro");

        int userSelection = fileChooser.showSaveDialog(getContents());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            Pair<Boolean, String> ret = game.exportKakuro(file);

            if(!ret.first) {
                Dialogs.showErrorDialog("Error while exporting kakuro: " + ret.second, "Error");
            }
        }
    }*/

    public void onHintClick() {
        Pair<Pair<Integer, Integer>, Integer> response = game.getHint();
        if (response.first.first == -1) {
            if (response.first.second == -1) {
                Dialogs.showInfoDialog("The hint system hasn't found any useful hints in this position, you may try again after making some move.", "Oups!");
                return;
            }
            selectMovement(response.first.second);
            Pair<Integer, Integer> coord = game.getCoordAtMove(response.first.second);
            setSelectedPos(coord.first, coord.second);
            ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflict = new ArrayList<>();
            conflict.add(new Pair<>(selectedPos, WHITE_CELL));
            setConflictiveCoord(conflict);
        } else {
            setSelectedPos(response.first.first, response.first.second);
            ((GameScreen)screen).selectWhiteCellColor(selectedPos.first, selectedPos.second, Palette.HintOrange);
            if (response.second != -1) {
                ((GameScreen)screen).updateMovesPanel(game.getCurrentMoveIdx());
                ((GameScreen)screen).setValueWhiteCell(response.first.first, response.first.second, response.second);
                ((GameScreen)screen).selectWhiteCellColor(selectedPos.first, selectedPos.second, Palette.HintGreen);
            }
        }
    }

    public void onSolveClick() {
        game.surrender();
    }

    public void onKakuroSolutionValidated(float score, float timeUsed, String board) {
        ignoreDestroy = true;

        DisplayKakuroScreenCtrl nextScreen = presentationCtrl.getNewDisplayKakuroScreenCtrlInstance();

        nextScreen.prepareContents(
                "KAKURO SOLVED!",
                board,
                new Color(game.getColorCode()),
                Palette.HintGreen,
                "You spent " + secondsToStringTime(timeUsed) + " during this game and obtained a total of " + Math.round(score) + " points!",
                () -> presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.DASHBOARD))
        );

        presentationCtrl.setScreen(nextScreen);
    }

    public void onSurrender(float timeUsed, String board, int numSolutions, float timeSolving) {
        ignoreDestroy = true;

        DisplayKakuroScreenCtrl nextScreen = presentationCtrl.getNewDisplayKakuroScreenCtrlInstance();

        String solutions = "no solutions.";
        if (numSolutions == 1) solutions = "only one solution, which is now displayed on screen.";
        else if (numSolutions == 2) solutions = "multiple solutions, one of which is now displayed on screen.";

        nextScreen.prepareContents(
                "YOU SURRENDERED",
                board,
                new Color(game.getColorCode()),
                Palette.WarningLightRed,
                "You spent " + secondsToStringTime(timeUsed) + " during this game. As you surrendered you get 0 points. The solver has determined that his board has " + solutions + " The solver took exactly " + timeSolving + " ms to achieve these results.",
                () -> presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.DASHBOARD))
        );

        presentationCtrl.setScreen(nextScreen);
    }

    private String secondsToStringTime(float time) {
        int hours = (int)time / 3600;
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
    public void onDestroy() {
        if (ignoreDestroy) return;
        game.persistProgress();
    }

    @Override
    public void onFocusRegained(int width, int height) {
        // In the case of the GameScreen this method should probably never get called.
    }
}
