package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.entities.GameInProgress;
import src.domain.entities.Kakuro;
import src.domain.entities.Movement;
import src.domain.entities.User;
import src.presentation.controllers.GameScreenCtrl;
import src.utils.Pair;

import java.util.ArrayList;

public class GameCtrl {

    User user;
    Kakuro kakuro;
    GameInProgress currentGame;
    GameScreenCtrl viewCtrl;
    private int movementCount;

    private int totalNumberWhiteCells;
    private int currentNumberWhiteCellsAssigned;

    private int[] currentRowSums;           // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private int[] rowValuesUsed;            // cell values used (assigned) in each rowLine, always boolean[9]
    private Pair<Integer, Integer>[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private int[][] rowIDs;          // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] currentColSums;           // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private int[] colValuesUsed;            // cell values used (assigned) in each colLine, always boolean[9]
    private Pair<Integer, Integer>[] firstColCoord;    // coordinates to the first white cell in each colLine
    private int[][] colIDs;          // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    public GameCtrl(User user, Kakuro kakuro) {
        this.user = user;
        this.kakuro = kakuro;
        movementCount = 0;
        // First we check if there's a game in progress for this kakuro and this userName, if not we create a new game.
        ArrayList<GameInProgress> allGamesInProgress = new ArrayList<>();// = dades.getAllGamesInProgress(user.getName());
        boolean shouldCreateNewGame = true;
        for (GameInProgress g : allGamesInProgress) {
            if (g.getKakuro().getId().toString().equals(kakuro.getId().toString())) {
                currentGame = g;
                shouldCreateNewGame = false;
                break;
            }
        }

        if (shouldCreateNewGame) {
            currentGame = new GameInProgress(user, kakuro);
        }
    }

    public String gameSetUp(GameScreenCtrl view) {
        this.viewCtrl = view;
        preprocessRows();
        preprocessCols();
        return currentGame.getBoard().toString();
    }

    private void preprocessRows() {
        int rows = currentGame.getBoard().getHeight();
        int columns = currentGame.getBoard().getWidth();
        rowIDs = new int[rows][columns];
        int size = 0;
        int rowLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> forced = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(currentGame.getBoard().isBlackCell(i, j)) {
                    if (j-1 >= 0 && currentGame.getBoard().isWhiteCell(i, j-1)) {// there is a row before the black cell
                        sizes.add(size);
                        size = 0;
                        rowLineID++; //prepare for next rowLine
                    }
                    if (j+1 < columns && currentGame.getBoard().isWhiteCell(i, j+1)) {
                        rowIDs[i][j] = rowLineID; //black cell is responsible for next rowLine
                        coord.add(new Pair<>(i, j+1));
                    } else {
                        rowIDs[i][j] = -1; //black cell is not responsible for any rowLine
                    }
                } else {
                    // assign rowLineID to this member of current rowLine
                    rowIDs[i][j] = rowLineID;
                    size++;
                    // only in row preprocessing to count just once
                    totalNumberWhiteCells++;
                    if (!currentGame.getBoard().isEmpty(i, j)) forced.add(new Pair<>(i, j));
                }
            }
            if (size > 0) { //last rowLine in row if we have seen whiteCells
                sizes.add(size);
                size = 0;
                rowLineID++; //prepare for next rowLine
            }
        }
        rowLineSize = rowLineID;
        currentRowSums = new int[rowLineSize];
        rowSize = new int[rowLineSize];
        rowValuesUsed = new int[rowLineSize];
        firstRowCoord = new Pair[rowLineSize];
        for (int i = 0; i < rowLineSize; i++) { //initialize data at default values
            currentRowSums[i] = 0;
            rowSize[i] = sizes.get(i);
            rowValuesUsed[i] = 0;
            firstRowCoord[i] = coord.get(i);
        }
        for (Pair<Integer, Integer> p : forced) {
            currentRowSums[rowIDs[p.first][p.second]] += currentGame.getBoard().getValue(p.first, p.second);
            rowValuesUsed[rowIDs[p.first][p.second]] |= (1<<(currentGame.getBoard().getValue(p.first, p.second)-1));
        }
        // only in rows to count just once
        currentNumberWhiteCellsAssigned = forced.size();
    }

    private void preprocessCols() {
        int rows = currentGame.getBoard().getHeight();
        int columns = currentGame.getBoard().getWidth();
        colIDs = new int[rows][columns];
        int size = 0;
        int colLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> forced = new ArrayList<>();
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if(currentGame.getBoard().isBlackCell(j, i)) {
                    if (j-1 >= 0 && currentGame.getBoard().isWhiteCell(j-1, i)) {// there is a col before the black cell
                        sizes.add(size);
                        size = 0;
                        colLineID++; //prepare for next colLine
                    }
                    if (j+1 < rows && currentGame.getBoard().isWhiteCell(j+1, i)) {
                        colIDs[j][i] = colLineID; //black cell is responsible for next colLine
                        coord.add(new Pair<>(j+1, i));
                    } else {
                        colIDs[j][i] = -1; //black cell is not responsible for any colLine
                    }
                } else {
                    // assign colLineID to this member of current colLine
                    colIDs[j][i] = colLineID;
                    size++;
                    if (!currentGame.getBoard().isEmpty(j, i)) forced.add(new Pair<>(j, i));
                }
            }
            if (size > 0) { //last colLine in col if we have seen whiteCells
                sizes.add(size);
                size = 0;
                colLineID++; //prepare for next colLine
            }
        }
        colLineSize = colLineID;
        currentColSums = new int[colLineSize];
        colSize = new int[colLineSize];
        colValuesUsed = new int[colLineSize];
        firstColCoord = new Pair[colLineSize];
        for (int i = 0; i < colLineSize; i++) { //initialize data at default values
            currentColSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = 0;
            firstColCoord[i] = coord.get(i);
        }
        for (Pair<Integer, Integer> p : forced) {
            currentColSums[colIDs[p.first][p.second]] += currentGame.getBoard().getValue(p.first, p.second);
            colValuesUsed[colIDs[p.first][p.second]] |= (1<<(currentGame.getBoard().getValue(p.first, p.second)-1));
        }
    }

    // returns if move was valid.
    public int playMove(int r, int c, int value) {
        if (currentGame.getBoard().isBlackCell(r, c) || value == 0) return -1;
        int rowID = rowIDs[r][c];
        int colID = colIDs[r][c];
        int previousValue = currentGame.getBoard().getValue(r, c);
        if (previousValue == value) {
            movementCount++;
            currentGame.insertMovement(new Movement(movementCount, value, 0, r, c));
            rowValuesUsed[rowID] &= ~(1<<(value-1));
            currentRowSums[rowID] -= value;
            colValuesUsed[colID] &= ~(1<<(value-1));
            currentColSums[colID] -= value;
            currentNumberWhiteCellsAssigned--;
            return 0;
        }
        boolean valid = true;
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> conflicting = new ArrayList<>();
        if ((rowValuesUsed[rowID] & (1<<(value-1))) != 0) {
            valid = false;
            // look for cell that has it
            int confCol = colPositionOfValueInRow(r, c, value);
            conflicting.add(new Pair<>(new Pair<>(r, confCol), GameScreenCtrl.WHITE_CELL));
        }
        if ((colValuesUsed[colID] & (1<<(value-1))) != 0) {
            valid = false;
            // look for cell that has it
            int confRow = rowPositionOfValueInCol(r, c, value);
            conflicting.add(new Pair<>(new Pair<>(confRow, c), GameScreenCtrl.WHITE_CELL));
        }
        int numRowValuesUsed = Integer.bitCount(rowValuesUsed[rowID]);
        if (kakuro.getBoard().getHorizontalSum(r, firstRowCoord[rowID].second-1) < currentRowSums[rowID]-previousValue+value ||
                (numRowValuesUsed + (previousValue == 0 ? 1 : 0) == rowSize[rowID] && kakuro.getBoard().getHorizontalSum(r, firstRowCoord[rowID].second-1) != currentRowSums[rowID]-previousValue+value)) {
            valid = false;
            // select both left and right cells as conflicting
            int colLeft = firstRowCoord[rowID].second-1;
            int colRight = firstRowCoord[rowID].second + rowSize[rowID];
            conflicting.add(new Pair<>(new Pair<>(r, colLeft), GameScreenCtrl.BLACK_SECTION_RIGHT));
            conflicting.add(new Pair<>(new Pair<>(r, colRight), GameScreenCtrl.BLACK_SECTION_LEFT));
        }
        int numColValuesUsed = Integer.bitCount(colValuesUsed[colID]);
        if (kakuro.getBoard().getVerticalSum(firstColCoord[colID].first-1, c) < currentColSums[colID]-previousValue+value ||
                (numColValuesUsed + (previousValue == 0 ? 1 : 0) == colSize[colID] && kakuro.getBoard().getVerticalSum(firstColCoord[colID].first-1, c) != currentColSums[colID]-previousValue+value)) {
            valid = false;
            // select both top and bottom cells as conflicting
            int rowTop = firstColCoord[colID].first-1;
            int rowBottom = firstColCoord[colID].first + colSize[colID];
            conflicting.add(new Pair<>(new Pair<>(rowTop, c), GameScreenCtrl.BLACK_SECTION_BOTTOM));
            conflicting.add(new Pair<>(new Pair<>(rowBottom, c), GameScreenCtrl.BLACK_SECTION_TOP));
        }

        if (valid) {
            movementCount++;
            currentGame.insertMovement(new Movement(movementCount, previousValue, value, r, c));
            rowValuesUsed[rowID] |= (1<<(value-1));
            currentRowSums[rowID] += value;
            colValuesUsed[colID] |= (1<<(value-1));
            currentColSums[colID] += value;
            if (previousValue == 0) currentNumberWhiteCellsAssigned++;
            else {
                rowValuesUsed[rowID] &= ~(1<<(previousValue-1));
                currentRowSums[rowID] -= previousValue;
                colValuesUsed[colID] &= ~(1<<(previousValue-1));
                currentColSums[colID] -= previousValue;
            }
            if (currentNumberWhiteCellsAssigned == totalNumberWhiteCells) validateKakuro();
        } else {
            viewCtrl.setConflictiveCoord(conflicting);
        }

        return valid ? value : previousValue;
    }

    public int toggleNotation(int r, int c, int value) {
        if (currentGame.getBoard().isBlackCell(r, c)) return -1;
        boolean hadNotation = currentGame.getBoard().cellHasNotation(r, c, value);
        currentGame.getBoard().setCellNotation(r, c, value, !hadNotation);
        return currentGame.getBoard().getCellNotations(r, c);
    }

    public boolean clearWhiteCell(int r, int c) {
        if (currentGame.getBoard().isBlackCell(r, c)) return false;
        // if it's a forced initial value don't clear it
        if (!kakuro.getBoard().isEmpty(r, c)) return false;
        int prevValue = currentGame.getBoard().getValue(r, c);
        movementCount++;
        currentGame.insertMovement(new Movement(movementCount, currentGame.getBoard().getValue(r, c), 0, r, c));
        currentGame.getBoard().clearCellNotations(r, c);
        if (prevValue != 0) {
            rowValuesUsed[rowIDs[r][c]] &= ~(1<<(prevValue-1));
            currentRowSums[rowIDs[r][c]] -= prevValue;
            colValuesUsed[colIDs[r][c]] &= ~(1<<(prevValue-1));
            currentColSums[colIDs[r][c]] -= prevValue;
            currentNumberWhiteCellsAssigned--;
        }
        return true;
    }

    private int colPositionOfValueInRow (int r, int c, int value) {
        int rowID = rowIDs[r][c];
        int lastCol = firstRowCoord[rowID].second + rowSize[rowID];
        for (int it = firstRowCoord[rowID].second; it < lastCol; it++) {
            if (currentGame.getBoard().getValue(r, it) == value) return it;
        }
        return -1;
    }

    private int rowPositionOfValueInCol (int r, int c, int value) {
        int colID = colIDs[r][c];
        int lastRow = firstColCoord[colID].first + colSize[colID];
        for (int it = firstColCoord[colID].first; it < lastRow; it++) {
            if (currentGame.getBoard().getValue(it, c) == value) return it;
        }
        return -1;
    }

    private void validateKakuro() {
        // TODO implement validation
        viewCtrl.kakuroSolvedCorrectly();
    }

}
