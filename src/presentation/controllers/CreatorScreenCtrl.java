package src.presentation.controllers;

import src.domain.controllers.CreatorCtrl;
import src.domain.controllers.DomainCtrl;
import src.domain.controllers.GameCtrl;
import src.presentation.screens.CreatorScreen;
import src.presentation.screens.GameScreen;
import src.presentation.views.KakuroView;
import src.utils.Pair;

import java.util.ArrayList;

public class CreatorScreenCtrl extends AbstractScreenCtrl {
    public static final int WHITE_CELL = -1;
    public static final int BLACK_SECTION_TOP = KakuroView.BLACK_SECTION_TOP;
    public static final int BLACK_SECTION_BOTTOM = KakuroView.BLACK_SECTION_BOTTOM;
    public static final int BLACK_SECTION_LEFT = KakuroView.BLACK_SECTION_LEFT;
    public static final int BLACK_SECTION_RIGHT = KakuroView.BLACK_SECTION_RIGHT;

    private CreatorCtrl creator;

    private Pair<Integer, Integer> selectedPos;

    public CreatorScreenCtrl(PresentationCtrl presentationCtrl, DomainCtrl domainCtrl) {
        super(presentationCtrl, domainCtrl);
    }

    public void setUpCreator(CreatorCtrl creatorInstance) {
        this.creator = creatorInstance;
        creator.setUp(this);
        selectedPos = new Pair<>(-1, -1);
    }

    public String getBoardToDisplay() {
        return creator.getBoardToString();
    }

    public void setSelectedPos(int r, int c) {
        selectedPos.first = r; selectedPos.second = c;
    }

    // Black cell manegent

    public void setBlackBrushEnabled(boolean b) {

    }
    public void clearSelectedBlackCellValueClicked () {

    }
    public void blackCellSelectValueClicked (int value) {

    }
    // possible values and wether black cell has a defined value
    public Pair<ArrayList<Integer>, Boolean> getBlackPossibilitiesList() {
        return new Pair<>(new ArrayList<>(), true);
    }

    // White cell management

    public void setWhiteBrushEnabled(boolean b) {

    }
    public void clearSelectedWhiteCellValueClicked () {

    }
    public void whiteCellSelectValueClicked (int value) {

    }
    // possible values and wether black cell has a defined value
    public Pair<ArrayList<Integer>, Boolean> getWhitePossibilitiesList() {
        return new Pair<>(new ArrayList<>(), true);
    }

    // Tab events
    public void onSelectedTabChanged(int tabIdx) {

    }

    // Button events
    public void onKakuroStateButtonPressed(String kakuroName) {

    }
    public void onImportButtonClicked() {
        System.out.println("IMPORT");
    }
    public void onExportButtonClicked() {
        System.out.println("EXPORT");
    }
    public void onFillKakuroButtonClicked() {
        System.out.println("FILL KAKURO");
    }
    public void onClearBoardButtonClicked() {
        System.out.println("CLEAR BOARD");
    }

    @Override
    public void build(int width, int height) {
        screen = new CreatorScreen(this);
        super.build(width, height);
    }

    @Override
    public void onFocusRegained(int width, int height) {} //should never get called in creator mode
}
