package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.algorithms.helpers.KakuroConstants;
import src.domain.algorithms.helpers.KakuroFunctions;
import src.domain.algorithms.helpers.SwappingCellQueue;
import src.domain.entities.BlackCell;
import src.domain.entities.Board;
import src.domain.entities.User;
import src.domain.entities.WhiteCell;
import src.presentation.controllers.CreatorScreenCtrl;
import src.presentation.controllers.GameScreenCtrl;
import src.utils.IntPair;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.TreeSet;

public class KakuroCreationCtrl {

    private static final String LINE_SIZES_MESSAGE = "Please make sure that there are no rows/columns of more than 9 cells.";
    private static final String CONFLICTS_ON_REBUILD_MESSAGE = "After recomputing the board some cells had invalid assignations, we've cleared them to avoid these conflicts.";

    private CreatorScreenCtrl viewCtrl;

    private User user;
    private Board workingBoard;

    private final int rows, columns;

    private SwappingCellQueue swappingCellQueue;
    private KakuroFunctions assigFunctions;

    private int[] rowSums;                  // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private int[] rowValuesUsed;            // cell values used (assigned) in each rowLine, always boolean[9]
    private Pair<Integer, Integer>[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private int[][] rowIDs;                 // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] colSums;                  // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private int[] colValuesUsed;            // cell values used (assigned) in each colLine, always boolean[9]
    private Pair<Integer, Integer>[] firstColCoord;    // coordinates to the first white cell in each colLine
    private int[][] colIDs;                 // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    private boolean[][] forcedInitialValues;

    private boolean invalidSizes;

    // To be able to send feedback to presentation layer when doing assignations.

    private ArrayList<Integer> modifiedRowSums; //saves rowID, always clear before an assignation process
    private ArrayList<Integer> modifiedColSums; //saves colID, always clear before an assignation process
    private ArrayList<Pair<Integer, Integer>> modifiedCellValues; //saves cell coordinates, always clear before an assignation process
    private TreeSet<IntPair> modifiedCellNotations; //saves cell coordinates, always clear before an assignation process

    private ArrayList<Integer> conflictingRowSums; //saves rowID, always clear before an assignation process
    private ArrayList<Integer> conflictingColSums; //saves colID, always clear before an assignation process
    private ArrayList<IntPair> conflictingWhiteCells; //saves cell coordinates, always clear before an assignation process

    public KakuroCreationCtrl(User user, int numRows, int numColumns) {
        this.user = user;
        workingBoard = new Board(numColumns, numRows);
        rows = numRows;
        columns = numColumns;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (r == 0 || c == 0) workingBoard.setCell(new BlackCell(), r, c);
                else workingBoard.setCell(new WhiteCell(true), r, c);
            }
        }
        forcedInitialValues = new boolean[rows][columns];
        invalidSizes = false;
    }

    public KakuroCreationCtrl(User user, Board initialBoard) {
        this.user = user;
        this.rows = initialBoard.getHeight();
        this.columns = initialBoard.getWidth();
        this.workingBoard = initialBoard;
        forcedInitialValues = new boolean[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (workingBoard.isWhiteCell(r, c) && !workingBoard.isEmpty(r, c) && workingBoard.getCellNotationSize(r, c) > 1)
                    forcedInitialValues[r][c] = true; //inferred values would have only one notation left if it has followed the process correctly
            }
        }
        invalidSizes = false;
    }

    public void initializeCreatorStructures() {
        modifiedRowSums = new ArrayList<>();
        modifiedColSums = new ArrayList<>();
        modifiedCellValues = new ArrayList<>();
        modifiedCellNotations = new TreeSet<>();
        conflictingRowSums = new ArrayList<>();
        conflictingColSums = new ArrayList<>();
        conflictingWhiteCells = new ArrayList<>();
        recomputeBoardStructures();
    }

    private void clearModified() {
        if (modifiedRowSums == null) modifiedRowSums = new ArrayList<>();
        else modifiedRowSums.clear();
        if (modifiedColSums == null) modifiedColSums = new ArrayList<>();
        else modifiedColSums.clear();
        if (modifiedCellValues == null) modifiedCellValues = new ArrayList<>();
        else modifiedCellValues.clear();
        if (modifiedCellNotations == null) modifiedCellNotations = new TreeSet<>();
        else modifiedCellNotations.clear();
    }

    private void clearConflicting() {
        if (conflictingRowSums == null) conflictingRowSums = new ArrayList<>();
        else conflictingRowSums.clear();
        if (conflictingColSums == null) conflictingColSums = new ArrayList<>();
        else conflictingColSums.clear();
        if (conflictingWhiteCells == null) conflictingWhiteCells = new ArrayList<>();
        else conflictingWhiteCells.clear();
    }

    private boolean validateRowColSizes() {
        boolean allValid = true;
        int[] colSizes = new int[columns];
        for (int r = 0; r < rows; r++) {
            int rowSize = 0;
            for (int c = 0; c < columns; c++) {
                if (workingBoard.isBlackCell(r, c)) {
                    if (rowSize > 9) {
                        allValid = false;
                        setConflictingRow(r, c-1);
                    }
                    if (colSizes[c] > 9) {
                        allValid = false;
                        setConflictingCol(r-1, c);
                    }
                    rowSize = 0;
                    colSizes[c] = 0;
                } else {
                    rowSize++;
                    colSizes[c]++;
                }
            }
            if (rowSize > 9) {
                allValid = false;
                setConflictingRow(r, columns-1);
            }
        }
        for (int c = 0; c < columns; c++) if (colSizes[c] > 9) {
            allValid = false;
            setConflictingCol(rows-1, c);
        }
        return allValid;
    }

    private void setConflictingRow(int r, int c) {
        conflictingWhiteCells.add(new IntPair(r, c));
        int posC = c-1;
        while (posC > 0 && workingBoard.isWhiteCell(r, posC)) {
            conflictingWhiteCells.add(new IntPair(r, posC));
            posC--;
        }
        posC = c+1;
        while (posC < columns && workingBoard.isWhiteCell(r, posC)) {
            conflictingWhiteCells.add(new IntPair(r, posC));
            posC++;
        }
    }
    private void setConflictingCol(int r, int c) {
        conflictingWhiteCells.add(new IntPair(r, c));
        int posR = r-1;
        while (posR > 0 && workingBoard.isWhiteCell(posR, c)) {
            conflictingWhiteCells.add(new IntPair(posR, c));
            posR--;
        }
        posR = r+1;
        while (posR < columns && workingBoard.isWhiteCell(posR, c)) {
            conflictingWhiteCells.add(new IntPair(posR, c));
            posR++;
        }
    }

    private void recomputeBoardStructures() {
        clearConflicting();
        invalidSizes = !validateRowColSizes();
        if (invalidSizes) {
            sendMessageToPresentation(LINE_SIZES_MESSAGE);
            sendConflictingToPresentation();
            return;
        }
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> forcedInitial = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (workingBoard.isWhiteCell(r, c)) {
                    if (forcedInitialValues[r][c] && !workingBoard.isEmpty(r,c)) forcedInitial.add(new Pair<>(new Pair<>(r, c), workingBoard.getValue(r, c)));
                    workingBoard.setCell(new WhiteCell(true), r, c);
                } else {
                    int verticalSum = workingBoard.getVerticalSum(r, c);
                    int horizontalSum = workingBoard.getHorizontalSum(r, c);
                    boolean changed = false;
                    if (verticalSum != 0 && r < rows-1 && workingBoard.isBlackCell(r+1, c)) {
                        verticalSum = 0;
                        changed = true;
                    }
                    if (horizontalSum != 0 && c < columns-1 && workingBoard.isBlackCell(r, c+1)) {
                        horizontalSum = 0;
                        changed = true;
                    }
                    if (changed) workingBoard.setCell(new BlackCell(verticalSum, horizontalSum), r, c);
                }
            }
        }

        preprocessRows();
        preprocessCols();

        swappingCellQueue = new SwappingCellQueue(workingBoard);
        initializeAssigFunctions();

        for (int rowID = 0; rowID < rowLineSize; rowID++) {
            int r = firstRowCoord[rowID].first;
            int c = firstRowCoord[rowID].second-1;
            int currSum = workingBoard.getHorizontalSum(r, c);
            if (currSum != 0 && !assigFunctions.rowSumAssignation(r, c+1, workingBoard.getHorizontalSum(r, c))) {
                conflictingRowSums.add(rowID);
                workingBoard.setCell(new BlackCell(workingBoard.getVerticalSum(r, c), 0), r, c);
            }
        }

        for (int colID = 0; colID < colLineSize; colID++) {
            int r = firstColCoord[colID].first-1;
            int c = firstColCoord[colID].second;
            int currSum = workingBoard.getVerticalSum(r, c);
            if (currSum != 0 && !assigFunctions.colSumAssignation(r+1, c, workingBoard.getVerticalSum(r, c))) {
                conflictingColSums.add(colID);
                workingBoard.setCell(new BlackCell(0, workingBoard.getHorizontalSum(r, c)), r, c);
            }
        }

        forcedInitialValues = new boolean[rows][columns];
        for (Pair<Pair<Integer, Integer>, Integer> f : forcedInitial) {
            if (assigFunctions.cellValueAssignation(f.first.first, f.first.second, f.second)) {
                forcedInitialValues[f.first.first][f.first.second] = true;
            } else {
                conflictingWhiteCells.add(new IntPair(f.first.first, f.first.second));
            }
        }

        sendReshapedBoard();

        if (!conflictingRowSums.isEmpty() || !conflictingColSums.isEmpty() || !conflictingWhiteCells.isEmpty()) {
            sendMessageToPresentation(CONFLICTS_ON_REBUILD_MESSAGE);
        }
        sendConflictingToPresentation();
    }

    private void sendReshapedBoard() {
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> blackCellValues = new ArrayList<>();
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> whiteCellForcedValues = new ArrayList<>();
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> whiteCellNotations = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (workingBoard.isWhiteCell(r, c)) {
                    if (forcedInitialValues[r][c]) {
                        whiteCellForcedValues.add(new Pair<>(new Pair<>(r, c), workingBoard.getValue(r, c)));
                    } else {
                        whiteCellNotations.add(new Pair<>(new Pair<>(r, c), workingBoard.getCellNotations(r, c)));
                    }
                } else {
                    if (r > 0 && workingBoard.isWhiteCell(r-1, c)) {
                        if (colSums[colIDs[r-1][c]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, -1)));
                        else blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, colSums[colIDs[r-1][c]])));
                    } else {
                        blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, 0)));
                    }
                    if (r < rows-1 && workingBoard.isWhiteCell(r+1, c)) {
                        if (colSums[colIDs[r+1][c]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_BOTTOM, -1)));
                        else blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_BOTTOM, colSums[colIDs[r+1][c]])));
                    } else {
                        blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_BOTTOM, 0)));
                    }
                    if (c > 0 && workingBoard.isWhiteCell(r, c-1)) {
                        if (rowSums[rowIDs[r][c-1]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, -1)));
                        else blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, rowSums[rowIDs[r][c-1]])));
                    } else {
                        blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, 0)));
                    }
                    if (c < columns-1 && workingBoard.isWhiteCell(r, c+1)) {
                        if (rowSums[rowIDs[r][c+1]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_RIGHT, -1)));
                        else blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_RIGHT, rowSums[rowIDs[r][c+1]])));
                    } else {
                        blackCellValues.add(new Pair<>(new Pair<>(r, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_RIGHT, 0)));
                    }
                }
            }
        }

        //check last row and col
        for (int c = 0; c < columns; c++) {
            if (workingBoard.isWhiteCell(rows-1, c)) {
                if (rowSums[rowIDs[rows-1][c]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(rows, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, -1)));
                else blackCellValues.add(new Pair<>(new Pair<>(rows, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, rowSums[rowIDs[rows-1][c]])));
            } else {
                blackCellValues.add(new Pair<>(new Pair<>(rows, c), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_TOP, 0)));
            }
        }
        for (int r = 0; r < rows; r++) {
            if (workingBoard.isWhiteCell(r, columns-1)) {
                if (rowSums[rowIDs[r][columns-1]] == 0)  blackCellValues.add(new Pair<>(new Pair<>(r, columns), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, -1)));
                else blackCellValues.add(new Pair<>(new Pair<>(r, columns), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, rowSums[rowIDs[r][columns-1]])));
            } else {
                blackCellValues.add(new Pair<>(new Pair<>(r, columns), new Pair<>(CreatorScreenCtrl.BLACK_SECTION_LEFT, 0)));
            }
        }

        viewCtrl.setValuesToBlackCells(blackCellValues);
        viewCtrl.setValuesToWhiteCells(whiteCellForcedValues);
        viewCtrl.setNotationsToWhiteCells(whiteCellNotations);
    }


    private void preprocessRows() {
        int size = 0;
        int rowLineID = 0;
        rowIDs = new int[rows][columns];
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(workingBoard.isBlackCell(i, j)) {
                    if (j-1 >= 0 && workingBoard.isWhiteCell(i, j-1)) {// there is a row before the black cell
                        sizes.add(size);
                        size = 0;
                        rowLineID++; //prepare for next rowLine
                    }
                    if (j+1 < columns && workingBoard.isWhiteCell(i, j+1)) {
                        rowIDs[i][j] = rowLineID; //black cell is responsible for next rowLine
                        coord.add(new Pair<>(i, j+1));
                    } else {
                        rowIDs[i][j] = -1; //black cell is not responsible for any rowLine
                    }
                } else {
                    // assign rowLineID to this member of current rowLine
                    rowIDs[i][j] = rowLineID;
                    size++;
                }
            }
            if (size > 0) { //last rowLine in row if we have seen whiteCells
                sizes.add(size);
                size = 0;
                rowLineID++; //prepare for next rowLine
            }
        }
        rowLineSize = rowLineID;
        rowSums = new int[rowLineSize];
        rowSize = new int[rowLineSize];
        rowValuesUsed = new int[rowLineSize];
        firstRowCoord = new Pair[rowLineSize];
        for (int i = 0; i < rowLineSize; i++) { //initialize data at default values
            rowSums[i] = 0;
            rowSize[i] = sizes.get(i);
            rowValuesUsed[i] = 0;
            firstRowCoord[i] = coord.get(i);
        }
    }

    private void preprocessCols() {
        int size = 0;
        int colLineID = 0;
        colIDs = new int[rows][columns];
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if(workingBoard.isBlackCell(j, i)) {
                    if (j-1 >= 0 && workingBoard.isWhiteCell(j-1, i)) {// there is a col before the black cell
                        sizes.add(size);
                        size = 0;
                        colLineID++; //prepare for next colLine
                    }
                    if (j+1 < rows && workingBoard.isWhiteCell(j+1, i)) {
                        colIDs[j][i] = colLineID; //black cell is responsible for next colLine
                        coord.add(new Pair<>(j+1, i));
                    } else {
                        colIDs[j][i] = -1; //black cell is not responsible for any colLine
                    }
                } else {
                    // assign colLineID to this member of current colLine
                    colIDs[j][i] = colLineID;
                    size++;
                }
            }
            if (size > 0) { //last colLine in col if we have seen whiteCells
                sizes.add(size);
                size = 0;
                colLineID++; //prepare for next colLine
            }
        }
        colLineSize = colLineID;
        colSums = new int[colLineSize];
        colSize = new int[colLineSize];
        colValuesUsed = new int[colLineSize];
        firstColCoord = new Pair[colLineSize];
        for (int i = 0; i < colLineSize; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = 0;
            firstColCoord[i] = coord.get(i);
        }
    }


    public void setUp(CreatorScreenCtrl view) {
        this.viewCtrl = view;
    }

    public String getBoardToString() {
        int height = workingBoard.getHeight();
        int width = workingBoard.getWidth();
        String[] row = new String[height];
        String[] col = new String[width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    if (workingBoard.getHorizontalSum(i, j) == 0 && workingBoard.getVerticalSum(i,j) == 0) {
                        int sections = 0;
                        if (i-1>=0 && workingBoard.isWhiteCell(i-1, j)) sections |= (1<<(viewCtrl.BLACK_SECTION_TOP));
                        if (i+1<height && workingBoard.isWhiteCell(i+1, j)) sections |= (1<<(viewCtrl.BLACK_SECTION_BOTTOM));
                        if (j-1>=0 && workingBoard.isWhiteCell(i, j-1)) sections |= (1<<(viewCtrl.BLACK_SECTION_LEFT));
                        if (j+1<width && workingBoard.isWhiteCell(i, j+1)) sections |= (1<<(viewCtrl.BLACK_SECTION_RIGHT));
                        col[j] = sections == 0 ? "*" : "#"+sections;
                    } else {
                        col[j] = workingBoard.getCell(i,j).toString();
                    }
                } else {
                    col[j] = workingBoard.getCell(i,j).toString();
                }
            }

            row[i] = String.join(",", col);
        }

        String header = height + "," + width + "\n";
        return header + String.join("\n", row);
    }

    private void sendMessageToPresentation(String message) {
        viewCtrl.setTipMessage(message);
    }

    private void sendConflictingToPresentation() {
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflicts = new ArrayList<>();
        for (int r : conflictingRowSums) {
            conflicts.add(new Pair( new Pair(firstRowCoord[r].first, firstRowCoord[r].second-1), CreatorScreenCtrl.BLACK_SECTION_RIGHT));
            conflicts.add(new Pair( new Pair(firstRowCoord[r].first, firstRowCoord[r].second + rowSize[r]), CreatorScreenCtrl.BLACK_SECTION_LEFT));
        }
        for (int c : conflictingColSums) {
            conflicts.add(new Pair( new Pair(firstColCoord[c].first-1, firstColCoord[c].second), CreatorScreenCtrl.BLACK_SECTION_BOTTOM));
            conflicts.add(new Pair( new Pair(firstColCoord[c].first + colSize[c], firstColCoord[c].second), CreatorScreenCtrl.BLACK_SECTION_TOP));
        }
        for (IntPair ip : conflictingWhiteCells) {
            conflicts.add(new Pair(new Pair(ip.first, ip.second), CreatorScreenCtrl.WHITE_CELL));
        }
        viewCtrl.setConflictingCoord(conflicts);
    }

    public void setCellsToBlack(TreeSet<IntPair> coords) {
        ArrayList<IntPair> changes = new ArrayList<>();
        for (IntPair p : coords) {
            int r = (Integer)p.first;
            int c = (Integer)p.second;
            if (r < rows && c < columns && workingBoard.isWhiteCell(r, c)) {
                changes.add(p);
                workingBoard.setCell(new BlackCell(), r, c);
            }
        }
        if (changes.size() > 0) {
            viewCtrl.setCellsToBlack(changes);
            recomputeBoardStructures();
            if (invalidSizes) {
                viewCtrl.setBoardToDisplay(getBoardToString());
                viewCtrl.repaintPrevConflicts();
            }
        }
    }

    public void setCellsToWhite(TreeSet<IntPair> coords) {
        ArrayList<IntPair> changes = new ArrayList<>();
        for (IntPair p : coords) {
            int r = (Integer)p.first;
            int c = (Integer)p.second;
            if (r > 0 && c > 0 && r < rows && c < columns && workingBoard.isBlackCell( r, c)) {
                changes.add(p);
                workingBoard.setCell(new WhiteCell(), r, c);
            }
        }
        if (changes.size() > 0) {
            viewCtrl.setCellsToWhite(changes);
            recomputeBoardStructures();
            if (invalidSizes) {
                viewCtrl.setBoardToDisplay(getBoardToString());
                viewCtrl.repaintPrevConflicts();
            }
        }
    }

    public boolean selectBlackCell(int r, int c, int s) {
        // Cases where can't be selected
        if (invalidSizes) {
            sendMessageToPresentation(LINE_SIZES_MESSAGE);
            return false;
        }
        if (s == CreatorScreenCtrl.BLACK_SECTION_TOP && !(r>0 && workingBoard.isWhiteCell(r-1, c))) return false;
        if (s == CreatorScreenCtrl.BLACK_SECTION_BOTTOM && !(r<rows-1 && workingBoard.isWhiteCell(r+1, c))) return false;
        if (s == CreatorScreenCtrl.BLACK_SECTION_LEFT && !(c>0 && workingBoard.isWhiteCell(r, c-1))) return false;
        if (s == CreatorScreenCtrl.BLACK_SECTION_RIGHT && !(c<columns-1 && workingBoard.isWhiteCell(r, c+1))) return false;

        if (s == CreatorScreenCtrl.BLACK_SECTION_TOP) r = firstColCoord[colIDs[r-1][c]].first-1;
        else if (s == CreatorScreenCtrl.BLACK_SECTION_LEFT) c = firstRowCoord[rowIDs[r][c-1]].second-1;

        boolean isRow = s == CreatorScreenCtrl.BLACK_SECTION_LEFT || s == CreatorScreenCtrl.BLACK_SECTION_RIGHT;
        int lineSize;
        int valUsed;
        int firstCrossCoord;
        if (isRow) {
            int rowID = rowIDs[r][c+1];
            lineSize = rowSize[rowID];
            valUsed = rowValuesUsed[rowID];
            firstCrossCoord = firstRowCoord[rowID].second;
        } else {
            int colID = colIDs[r+1][c];
            lineSize = colSize[colID];
            valUsed = colValuesUsed[colID];
            firstCrossCoord = firstColCoord[colID].first;
        }

        ArrayList<Pair<Integer, Integer>> possibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(lineSize, valUsed);
        TreeSet<Integer> allSums = new TreeSet<>();

        int lastValueAdded = -1;
        for (int i = possibleCases.size()-1; i >= 0 ; i--) {
            while (i >= 0 && possibleCases.get(i).first == lastValueAdded) i--;
            if (i < 0) break;

            int pCase = possibleCases.get(i).second;
            ArrayList<Integer> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
            ArrayList<WhiteCell> containingCells = new ArrayList<>();
            for (int it = firstCrossCoord; it < firstCrossCoord+lineSize; it++) {
                Pair<Integer, Integer> coord = isRow ? new Pair<>(r, it) : new Pair<>(it, c);
                for (int digit : p) {
                    if ((!workingBoard.isEmpty(coord.first, coord.second) && workingBoard.getValue(coord.first, coord.second) == digit) || workingBoard.cellHasNotation(coord.first, coord.second, digit)) {
                        containingCells.add((WhiteCell)workingBoard.getCell(coord.first, coord.second));
                        break;
                    }
                }
            }
            if (assigFunctions.isCombinationPossible(p, containingCells)) {
                lastValueAdded = possibleCases.get(i).first;
                allSums.add(lastValueAdded);
            }
        }

        boolean hadValue = false;
        if (isRow) {
            if (workingBoard.getHorizontalSum(r,c) != 0) {
                hadValue = true;
                allSums.remove(workingBoard.getHorizontalSum(r,c));
            }
        } else {
            if (workingBoard.getHorizontalSum(r,c) != 0) {
                hadValue = true;
                allSums.remove(workingBoard.getHorizontalSum(r,c));
            }
        }

        ArrayList<Integer> blackCellPossibilities = new ArrayList<>(allSums);

        viewCtrl.setBlackPossibilitiesList(new Pair<>(blackCellPossibilities, hadValue));

        return true;
    }
    public Pair<Pair<Integer, Integer>, Integer> getMatchingBlackPos(int r, int c, int s) {
        Pair<Pair<Integer, Integer>, Integer> result = new Pair<>(new Pair<>(-1, -1), -2);
        if (invalidSizes) return result; //shouldn't ask for it in this case
        if (s == CreatorScreenCtrl.BLACK_SECTION_TOP && !(r>0 && workingBoard.isWhiteCell(r-1, c))) return result;
        if (s == CreatorScreenCtrl.BLACK_SECTION_BOTTOM && !(r<rows-1 && workingBoard.isWhiteCell(r+1, c))) return result;
        if (s == CreatorScreenCtrl.BLACK_SECTION_LEFT && !(c>0 && workingBoard.isWhiteCell(r, c-1))) return result;
        if (s == CreatorScreenCtrl.BLACK_SECTION_RIGHT && !(c<columns-1 && workingBoard.isWhiteCell(r, c+1))) return result;

        switch (s) {
            case CreatorScreenCtrl.BLACK_SECTION_TOP :
                result.first.first = firstColCoord[colIDs[r-1][c]].first-1;
                result.first.second = c;
                result.second = CreatorScreenCtrl.BLACK_SECTION_BOTTOM;
                break;
            case CreatorScreenCtrl.BLACK_SECTION_BOTTOM :
                result.first.first = firstColCoord[colIDs[r+1][c]].first+colSize[colIDs[r+1][c]];
                result.first.second = c;
                result.second = CreatorScreenCtrl.BLACK_SECTION_TOP;
                break;
            case CreatorScreenCtrl.BLACK_SECTION_LEFT :
                result.first.first = r;
                result.first.second = firstRowCoord[rowIDs[r][c-1]].second-1;
                result.second = CreatorScreenCtrl.BLACK_SECTION_RIGHT;
                break;
            case CreatorScreenCtrl.BLACK_SECTION_RIGHT :
                result.first.first = r;
                result.first.second = firstRowCoord[rowIDs[r][c+1]].second+rowSize[rowIDs[r][c+1]];
                result.second = CreatorScreenCtrl.BLACK_SECTION_LEFT;
                break;
        }
        return result;
    }

    public boolean selectWhiteCell(int r, int c) {
        // Cases where can't be selected
        if (invalidSizes) {
            sendMessageToPresentation(LINE_SIZES_MESSAGE);
            return false;
        }

        ArrayList<Integer> whiteCellPossibilities = new ArrayList<>();

        for (int i = 0; i < 9; i++) if (workingBoard.cellHasNotation(r, c, i+1)) whiteCellPossibilities.add(i+1);

        viewCtrl.setWhitePossibilitiesList(new Pair<>(whiteCellPossibilities, forcedInitialValues[r][c]));

        return true;
    }



    private void initializeAssigFunctions() {
        assigFunctions = new KakuroFunctions(new KakuroFunctions.KakuroFunctionsMaster() {
            @Override
            public int getRowID(int r, int c) {
                return rowIDs[r][c];
            }

            @Override
            public int getColID(int r, int c) {
                return colIDs[r][c];
            }

            @Override
            public int getRowLineSize(int r, int c) {
                return rowLineSize;
            }

            @Override
            public int getColLineSize(int r, int c) {
                return colLineSize;
            }

            @Override
            public int getRowSum(int r, int c) {
                return rowSums[rowIDs[r][c]];
            }

            @Override
            public int getColSum(int r, int c) {
                return colSums[colIDs[r][c]];
            }

            @Override
            public void setRowSum(int r, int c, int value) {
                if (value < 0 || value > 45) return;
                rowSums[rowIDs[r][c]] = value;
            }

            @Override
            public void setColSum(int r, int c, int value) {
                if (value < 0 || value > 45) return;
                colSums[colIDs[r][c]] = value;
            }

            @Override
            public int getRowSize(int r, int c) {
                return rowSize[rowIDs[r][c]];
            }

            @Override
            public int getColSize(int r, int c) {
                return colSize[colIDs[r][c]];
            }

            @Override
            public int getRowValuesUsed(int r, int c) {
                return rowValuesUsed[rowIDs[r][c]];
            }

            @Override
            public int getColValuesUsed(int r, int c) {
                return colValuesUsed[colIDs[r][c]];
            }

            @Override
            public void setRowValuesUsed(int r, int c, int values) {
                if (values>>9 > 0) return;
                rowValuesUsed[rowIDs[r][c]] = values;
            }

            @Override
            public void setColValuesUsed(int r, int c, int values) {
                if (values>>9 > 0) return;
                colValuesUsed[colIDs[r][c]] = values;
            }

            @Override
            public Pair<Integer, Integer> getFirstRowCoord(int r, int c) {
                return new Pair<>(firstRowCoord[rowIDs[r][c]].first, firstRowCoord[rowIDs[r][c]].second);
            }

            @Override
            public Pair<Integer, Integer> getFirstColCoord(int r, int c) {
                return new Pair<>(firstColCoord[colIDs[r][c]].first, firstColCoord[colIDs[r][c]].second);
            }

            @Override
            public Board getWorkingBoard() {
                return workingBoard;
            }

            @Override
            public SwappingCellQueue getNotationsQueue() {
                return swappingCellQueue;
            }
        });

        assigFunctions.setCellValueAssignationListener(new KakuroFunctions.CellValueAssignationListener() {
            @Override
            public boolean onCellValueAssignation(Pair<Pair<Integer, Integer>, Integer> assig) {
                modifiedCellValues.add(assig.first);
                return false;
            }
        });
    }
}
