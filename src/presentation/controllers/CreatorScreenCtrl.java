package src.presentation.controllers;

import src.domain.controllers.KakuroCreationCtrl;
import src.domain.controllers.DomainCtrl;
import src.presentation.screens.CreatorScreen;
import src.presentation.utils.Dialogs;
import src.presentation.views.KakuroView;
import src.utils.IntPair;
import src.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeSet;

public class CreatorScreenCtrl extends AbstractScreenCtrl {
    public static final int WHITE_CELL = -1;
    public static final int BLACK_SECTION_TOP = KakuroView.BLACK_SECTION_TOP;
    public static final int BLACK_SECTION_BOTTOM = KakuroView.BLACK_SECTION_BOTTOM;
    public static final int BLACK_SECTION_LEFT = KakuroView.BLACK_SECTION_LEFT;
    public static final int BLACK_SECTION_RIGHT = KakuroView.BLACK_SECTION_RIGHT;

    private KakuroCreationCtrl creator;

    private Pair<Pair<Integer, Integer>, Integer> selectedPos;

    private ArrayList<Pair<Pair<Integer, Integer>, Integer>> modifiedCoord;
    private ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflictingCoord;

    private int currentTab;
    private boolean blackBrushActive;
    private boolean whiteBrushActive;
    private boolean mouseIsPressed;
    private TreeSet<IntPair> brushPath;

    private boolean ignoreDestroy;

    public CreatorScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public void setUpCreator(KakuroCreationCtrl creatorInstance) {
        this.creator = creatorInstance;
        creator.setUp(this);
        selectedPos = new Pair<>(new Pair<>(-1, -1), -2);
        modifiedCoord = new ArrayList<>();
        conflictingCoord = new ArrayList<>();
        currentTab = 0;
        blackBrushActive = false;
        whiteBrushActive = false;
        mouseIsPressed = false;
        brushPath = new TreeSet<>();
        ignoreDestroy = false;
    }

    public String getBoardToDisplay() {
        return creator.getBoardToString();
    }
    public void setBoardToDisplay(String board) { ((CreatorScreen)screen).updateWholeBoardFromString(board); }

    private void unselectPrevPos() {
        if (selectedPos.first.first == -1 && selectedPos.first.second == -1 && selectedPos.second == -2) return;
        if (selectedPos.second == WHITE_CELL) {
            ((CreatorScreen)screen).unselectWhiteCell(selectedPos.first.first, selectedPos.first.second);
        }
        else {
            ((CreatorScreen)screen).unselectBlackCell(selectedPos.first.first, selectedPos.first.second, selectedPos.second);
            Pair<Pair<Integer, Integer>, Integer> symm = creator.getMatchingBlackPos(selectedPos.first.first, selectedPos.first.second, selectedPos.second);
            if (symm.first.first != -1) ((CreatorScreen)screen).unselectBlackCell(symm.first.first, symm.first.second, symm.second);
        }
        selectedPos.first.first = -1; selectedPos.first.second = -1; selectedPos.second = -2;
    }
    public void setSelectedPos(int r, int c, int s) {
        if (r == -1 && c == -1 && s == -2) {
            unselectPrevPos();
            ((CreatorScreen)screen).updateWhitePossibleValues(new Pair<>(new ArrayList<>(), false));
            ((CreatorScreen)screen).updateBlackPossibleValues(new Pair<>(new ArrayList<>(), false));
            return;
        }
        if (s == WHITE_CELL) {
            if (currentTab != 1) ((CreatorScreen)screen).setTab(1);
            if (creator.selectWhiteCell(r, c)) {
                unselectPrevPos();
                unselectConflictingCoord();
                unselectModifiedCoord();
                ((CreatorScreen)screen).selectWhiteCell(r, c);
                selectedPos.first.first = r; selectedPos.first.second = c; selectedPos.second = s;
            }
        } else {
            if (currentTab != 0) ((CreatorScreen)screen).setTab(0);
            if (creator.selectBlackCell(r, c, s)) {
                unselectPrevPos();
                unselectConflictingCoord();
                unselectModifiedCoord();
                ((CreatorScreen)screen).selectBlackCell(r, c, s);
                Pair<Pair<Integer, Integer>, Integer> symm = creator.getMatchingBlackPos(r, c, s);
                ((CreatorScreen)screen).selectBlackCell(symm.first.first, symm.first.second, symm.second);
                selectedPos.first.first = r; selectedPos.first.second = c; selectedPos.second = s;
            }
        }
    }

    public void setModifiedCoord (ArrayList<Pair<Pair<Integer, Integer>, Integer>> modifications) {
        // erase previous modifications, mark new modifications
        unselectModifiedCoord();
        for (Pair<Pair<Integer, Integer>, Integer> m : modifications) {
            ((CreatorScreen)screen).selectModified(m.first.first, m.first.second, m.second);
        }
        modifiedCoord = modifications;
    }
    private void unselectModifiedCoord() {
        for (Pair<Pair<Integer, Integer>, Integer> m : modifiedCoord) {
            if (m.second == WHITE_CELL) {
                ((CreatorScreen)screen).unselectWhiteCell(m.first.first, m.first.second);
                if (m.first.first == selectedPos.first.first && m.first.second == selectedPos.first.second)
                    ((CreatorScreen)screen).selectWhiteCell(selectedPos.first.first, selectedPos.first.second);
            } else {
                ((CreatorScreen)screen).unselectBlackCell(m.first.first, m.first.second, m.second);
                if (m.first.first == selectedPos.first.first && m.first.second == selectedPos.first.second) {
                    ((CreatorScreen) screen).selectBlackCell(m.first.first, m.first.second, m.second);
                    Pair<Pair<Integer, Integer>, Integer> symm = creator.getMatchingBlackPos(m.first.first, m.first.second, m.second);
                    ((CreatorScreen)screen).selectBlackCell(symm.first.first, symm.first.second, symm.second);
                }
            }
        }
    }

    public void setConflictingCoord(ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflicts) {
        // erase previous conflicts, mark new conflicts
        unselectConflictingCoord();
        for (Pair<Pair<Integer, Integer>, Integer> cc : conflicts) {
            ((CreatorScreen)screen).selectConflictive(cc.first.first, cc.first.second, cc.second);
        }
        conflictingCoord = conflicts;
    }
    public void repaintPrevConflicts() {
        for (Pair<Pair<Integer, Integer>, Integer> cc : conflictingCoord) {
            ((CreatorScreen)screen).selectConflictive(cc.first.first, cc.first.second, cc.second);
        }
    }

    private void unselectConflictingCoord() {
        for (Pair<Pair<Integer, Integer>, Integer> cc : conflictingCoord) {
            if (cc.second == WHITE_CELL) {
                ((CreatorScreen)screen).unselectWhiteCell(cc.first.first, cc.first.second);
                if (cc.first.first == selectedPos.first.first && cc.first.second == selectedPos.first.second)
                    ((CreatorScreen)screen).selectWhiteCell(selectedPos.first.first, selectedPos.first.second);
            } else {
                ((CreatorScreen)screen).unselectBlackCell(cc.first.first, cc.first.second, cc.second);
                if (cc.first.first == selectedPos.first.first && cc.first.second == selectedPos.first.second) {
                    ((CreatorScreen) screen).selectBlackCell(cc.first.first, cc.first.second, cc.second);
                    Pair<Pair<Integer, Integer>, Integer> symm = creator.getMatchingBlackPos(cc.first.first, cc.first.second, cc.second);
                    ((CreatorScreen)screen).selectBlackCell(symm.first.first, symm.first.second, symm.second);
                }
            }
        }
    }

    // Black cell management

    public void setBlackBrushEnabled(boolean b) {
        blackBrushActive = b;
        if (b) whiteBrushActive = false;
    }
    public void clearSelectedBlackCellValueClicked () {
        unselectConflictingCoord();
        unselectModifiedCoord();
        creator.clearBlackCell(selectedPos.first.first, selectedPos.first.second, selectedPos.second);
    }
    public void blackCellSelectValueClicked (int value) {
        unselectConflictingCoord();
        unselectModifiedCoord();
        creator.blackCellAssignation(selectedPos.first.first, selectedPos.first.second, selectedPos.second, value);
    }
    // possible values and whether black cell has a defined value
    public void setBlackPossibilitiesList(Pair<ArrayList<Integer>, Boolean> blackPossibilitiesList) {
        ((CreatorScreen)screen).updateBlackPossibleValues(blackPossibilitiesList);
    }
    public void setValuesToBlackCells(ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> values) {
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> v : values) {
            ((CreatorScreen)screen).setValueBlackCell(v.first.first, v.first.second, v.second.first, v.second.second);
        }
    }
    public void setCellsToBlack(ArrayList<IntPair> coords) {
        for (IntPair p : coords) ((CreatorScreen)screen).setCellToBlack((Integer) p.first, (Integer) p.second);
    }


    // White cell management

    public void setWhiteBrushEnabled(boolean b) {
        whiteBrushActive = b;
        if (b) blackBrushActive = false;
    }
    public void clearSelectedWhiteCellValueClicked () {
        unselectConflictingCoord();
        unselectModifiedCoord();
        if (creator.clearWhiteCell(selectedPos.first.first, selectedPos.first.second))
            ((CreatorScreen)screen).setValueWhiteCell(selectedPos.first.first, selectedPos.first.second, 0);
    }
    public void whiteCellSelectValueClicked (int value) {
        unselectConflictingCoord();
        unselectModifiedCoord();
        creator.whiteCellAssignation(selectedPos.first.first, selectedPos.first.second, value);
    }
    // possible values and whether black cell has a defined value
    public void setWhitePossibilitiesList(Pair<ArrayList<Integer>, Boolean> whitePossibilitiesList) {
        ((CreatorScreen)screen).updateWhitePossibleValues(whitePossibilitiesList);
    }
    public void setValuesToWhiteCells(ArrayList<Pair<Pair<Integer, Integer>, Integer>> values) {
        for (Pair<Pair<Integer, Integer>, Integer> v : values) {
            ((CreatorScreen)screen).setValueWhiteCell(v.first.first, v.first.second, v.second);
        }
    }
    public void setNotationsToWhiteCells(ArrayList<Pair<Pair<Integer, Integer>, Integer>> notations) {
        for (Pair<Pair<Integer, Integer>, Integer> n : notations) {
            ((CreatorScreen) screen).setNotationWhiteCell(n.first.first, n.first.second, n.second);
        }
    }
    public void setCellsToWhite(ArrayList<IntPair> coords) {
        for (IntPair p : coords) ((CreatorScreen)screen).setCellToWhite((Integer) p.first, (Integer) p.second);
    }

    // Tab events
    public void onSelectedTabChanged(int tabIdx) {
        if (tabIdx == 0 && currentTab == 1) {
            currentTab = 0;
            whiteBrushActive = false;
            ((CreatorScreen)screen).updateWhitePossibleValues(new Pair<>(new ArrayList<>(), false));
            if (selectedPos.second == WHITE_CELL) unselectPrevPos();
        }
        else if (tabIdx == 1 && currentTab == 0) {
            currentTab = 1;
            blackBrushActive = false;
            ((CreatorScreen)screen).updateBlackPossibleValues(new Pair<>(new ArrayList<>(), false));
            if (selectedPos.second != WHITE_CELL) unselectPrevPos();
        }
    }

    // Button events
    public void onKakuroStateButtonPressed(String kakuroName) {
        creator.publishKakuro(kakuroName);
    }
    public void setKakuroStateButtonValidate() {
        ((CreatorScreen)screen).setKakuroStateBtn(false);
    }
    public void setKakuroStateButtonPublish() {
        ((CreatorScreen)screen).setKakuroStateBtn(true);
    }
    public void onKakuroPublished() {
        ignoreDestroy = true;
        Dialogs.showInfoDialog("Successfully saved to database!", "Congrats!");
        presentationCtrl.setScreen(presentationCtrl.getScreenCtrl(PresentationCtrl.MY_KAKUROS));
    }
    public void onExportButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export kakuro");

        int userSelection = fileChooser.showSaveDialog(getContents());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            Pair<Boolean, String> ret = creator.exportKakuro(file);

            if(!ret.first) {
                Dialogs.showErrorDialog("Error while exporting kakuro: " + ret.second, "Error");
            }
        }
    }
    public void onFillKakuroButtonClicked() {
        creator.fillKakuro();
    }
    public void onClearBoardButtonClicked() {
        if (Dialogs.showYesNoOptionDialog("This action will remove all values from the cells. It will preserve the black cell layout but in \"initial state\".", "Are you sure?"))
            creator.clearWholeBoard();
    }

    public void setTipMessage(String message) {
        ((CreatorScreen)screen).setTipBoxText(message);
    }

    public void onMousePressed(int r, int c) {
        if (blackBrushActive || whiteBrushActive) {
            mouseIsPressed = true;
            brushPath.clear();
            brushPath.add(new IntPair(r, c));

            if (blackBrushActive) {
                ((CreatorScreen)screen).prepareCellToBlack(r, c);
            } else {
                ((CreatorScreen)screen).prepareCellToWhite(r, c);
            }
        }
    }
    public void onMouseEntered(int r, int c) {
        if (mouseIsPressed && (blackBrushActive || whiteBrushActive)) {
            brushPath.add(new IntPair(r, c));

            if (blackBrushActive) {
                ((CreatorScreen)screen).prepareCellToBlack(r, c);
            } else {
                ((CreatorScreen)screen).prepareCellToWhite(r, c);
            }
        }
    }
    public void onMouseReleased() {
        mouseIsPressed = false;
        if (blackBrushActive) creator.setCellsToBlack(brushPath);
        else if (whiteBrushActive) creator.setCellsToWhite(brushPath);
    }

    @Override
    public void build(int width, int height) {
        screen = new CreatorScreen(this);
        super.build(width, height);
        creator.initializeCreatorStructures();
    }

    @Override
    public void onDestroy() {
        if (ignoreDestroy) return;
        if (Dialogs.showYesNoOptionDialog(
                "The system doesn't save half-built kakuros. If you want to be able to work on your creation later, export your progress.",
                "Export?"
        )) {
            onExportButtonClicked();
        }
    }

    @Override
    public void onFocusRegained(int width, int height) {} //should never get called in creator mode
}
