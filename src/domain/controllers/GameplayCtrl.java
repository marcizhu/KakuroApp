package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.algorithms.Solver;
import src.domain.algorithms.helpers.KakuroConstants;
import src.domain.algorithms.helpers.KakuroFunctions;
import src.domain.algorithms.helpers.SwappingCellQueue;
import src.domain.entities.*;
import src.presentation.controllers.GameScreenCtrl;
import src.utils.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameplayCtrl {

    User user;
    Kakuro kakuro;
    GameInProgress currentGame;
    GameScreenCtrl viewCtrl;
    private int movementCount;
    private int currentMovement;

    private int totalNumberWhiteCells;
    private int currentNumberWhiteCellsAssigned;

    private int[] currentRowSums;           // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private int[] rowValuesUsed;            // cell values used (assigned) in each rowLine, always boolean[9]
    private Pair<Integer, Integer>[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private int[][] rowIDs;                 // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] currentColSums;           // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private int[] colValuesUsed;            // cell values used (assigned) in each colLine, always boolean[9]
    private Pair<Integer, Integer>[] firstColCoord;    // coordinates to the first white cell in each colLine
    private int[][] colIDs;                 // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    private boolean usedValuesHelpIsActive;
    private boolean combinationsHelpIsActive;
    private boolean autoEraseHelpIsActive;

    private int hintAtMove;
    private Pair<Pair<Integer, Integer>, Integer> lastHint;

    public GameplayCtrl(User user, Kakuro kakuro) {
        this.user = user;
        this.kakuro = kakuro;
        movementCount = 0;
        currentMovement = 0;
        hintAtMove = -1;
        lastHint = new Pair<>(new Pair<>(-1, -1), -1);
        // First we check if there's a game in progress for this kakuro and this userName, if not we create a new game.
        ArrayList<GameInProgress> allGamesInProgress = new ArrayList<>();// = dades.getAllGamesInProgress(user.getName());
        boolean shouldCreateNewGame = true;
        for (GameInProgress g : allGamesInProgress) {
            if (g.getKakuro().getName().toString().equals(kakuro.getName().toString())) {
                currentGame = g;
                movementCount = currentGame.getMovements().size();
                currentMovement = movementCount;
                shouldCreateNewGame = false;
                break;
            }
        }

        if (shouldCreateNewGame) {
            currentGame = new GameInProgress(user, kakuro);
        }

        usedValuesHelpIsActive = false;
        combinationsHelpIsActive = false;
        autoEraseHelpIsActive = false;
    }

    public String gameSetUp(GameScreenCtrl view) {
        this.viewCtrl = view;
        preprocessRows();
        preprocessCols();
        return currentGame.getBoard().toString();
    }

    public Pair<Integer, Integer> getBoardSize() {
        return new Pair<>(currentGame.getBoard().getHeight(), currentGame.getBoard().getWidth());
    }

    public int getCurrentMoveIdx() {
        return currentMovement;
    }
    public Pair<Integer, Integer> getCoordAtMove(int move) {
        if (move > movementCount) return null;
        Pair<Integer, Integer> coord = currentGame.getMovements().get(move-1).getCoordinates();
        return new Pair<>(coord.first, coord.second);
    }
    public boolean selectMove(int moveIdx) {
        if (moveIdx > movementCount) return false;
        if (moveIdx == currentMovement) return false;
        currentMovement = moveIdx;
        sendRebuiltBoardUpToMove(currentMovement);
        return true;
    }
    public ArrayList<Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>> getMovementList() {
        ArrayList<Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>> response = new ArrayList<>();
        for (Movement move : currentGame.getMovements()) {
            response.add(new Pair<>(move.getIndex(), new Pair<>(move.getCoordinates(), new Pair<>(move.getPrevious(), move.getNext()))));
        }
        return response;
    }

    private void preprocessRows() {
        int rows = currentGame.getBoard().getHeight();
        int columns = currentGame.getBoard().getWidth();
        totalNumberWhiteCells = 0;
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

    private void sendRebuiltBoardUpToMove(int toMove) {
        Board b = new Board(kakuro.getBoard());
        ArrayList<Movement> allMoves = currentGame.getMovements();
        for (int i = 0; i < toMove; i++) {
            Movement m = allMoves.get(i);
            if (m.getNext() == 0) b.clearCellValue(m.getCoordinates().first, m.getCoordinates().second);
            else b.setCellValue(m.getCoordinates().first, m.getCoordinates().second, m.getNext());
        }
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> message = new ArrayList<>();
        for (int i = 0; i < b.getHeight(); i++) {
            for (int j = 0; j < b.getWidth(); j++) {
                if (b.isWhiteCell(i, j) && !b.isEmpty(i,j)) message.add(new Pair<>(new Pair<>(i, j), b.getValue(i, j)));
            }
        }
        viewCtrl.setBoardToDisplay(message);
    }

    // returns if move was valid.
    public int playMove(int r, int c, int value) {
        if (!kakuro.getBoard().isEmpty(r, c) || currentGame.getBoard().isBlackCell(r, c) || value == 0) return -1;
        if (currentMovement < movementCount) recreateGameUpToCurrentMove();
        int rowID = rowIDs[r][c];
        int colID = colIDs[r][c];
        int previousValue = currentGame.getBoard().getValue(r, c);
        if (previousValue == value) {
            hintAtMove = -1;
            movementCount++;
            currentMovement = movementCount;
            currentGame.insertMovement(new Movement(currentMovement, value, 0, r, c));
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
            currentMovement = movementCount;
            hintAtMove = -1;
            currentGame.insertMovement(new Movement(currentMovement, previousValue, value, r, c));
            rowValuesUsed[rowID] |= (1<<(value-1));
            currentRowSums[rowID] += value;
            colValuesUsed[colID] |= (1<<(value-1));
            currentColSums[colID] += value;
            if (autoEraseHelpIsActive) autoEraseNotations(r, c);
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

    private void recreateGameUpToCurrentMove() {
        int rows = currentGame.getBoard().getHeight();
        int cols = currentGame.getBoard().getWidth();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (currentGame.getBoard().isWhiteCell(i, j) && kakuro.getBoard().isEmpty(i, j)) {
                    currentGame.getBoard().clearCellValue(i, j);
                }
            }
        }

        preprocessRows();
        preprocessCols();
        movementCount = 0;

        int currMove = currentMovement;
        currentMovement = 0;
        if (currMove == 0) return;
        ArrayList<Movement> allMoves = (ArrayList<Movement>) currentGame.getMovements().clone();
        for (int i = 0; i < currMove; i++) {
            Movement m = allMoves.get(i);
            playMove(m.getCoordinates().first, m.getCoordinates().second, m.getNext());
        }
    }

    public boolean undoMove() {
        if (currentMovement <= 0) return false;
        currentMovement--;
        sendRebuiltBoardUpToMove(currentMovement);
        return true;
    }

    public boolean redoMove() {
        if (currentMovement >= movementCount) return false;
        currentMovement++;
        sendRebuiltBoardUpToMove(currentMovement);
        return true;
    }

    public void resetGame() {
        currentMovement = 0;
        recreateGameUpToCurrentMove();
        currentGame.getMovements().clear();
        sendRebuiltBoardUpToMove(currentMovement);
    }

    public int toggleNotation(int r, int c, int value) {
        if (currentGame.getBoard().isBlackCell(r, c)) return -1;
        boolean hadNotation = currentGame.getBoard().cellHasNotation(r, c, value);
        currentGame.getBoard().setCellNotation(r, c, value, !hadNotation);
        return currentGame.getBoard().getCellNotations(r, c);
    }

    public boolean clearWhiteCell(int r, int c) {
        if (currentMovement < movementCount) recreateGameUpToCurrentMove();
        if (currentGame.getBoard().isBlackCell(r, c)) return false;
        // if it's a forced initial value don't clear it
        if (!kakuro.getBoard().isEmpty(r, c)) return false;
        if (currentGame.getBoard().isEmpty(r, c)) return false; // already cleared
        int prevValue = currentGame.getBoard().getValue(r, c);
        movementCount++;
        currentMovement = movementCount;
        currentGame.insertMovement(new Movement(currentMovement, currentGame.getBoard().getValue(r, c), 0, r, c));
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

    public void setUsedValuesHelpIsActive (boolean active) {
        usedValuesHelpIsActive = active;
    }

    public void setCombinationsHelpIsActive (boolean active) {
        combinationsHelpIsActive = active;
    }

    public void setAutoEraseHelpIsActive (boolean active) {
        autoEraseHelpIsActive = active;
    }

    public void getHelpOptionsAtSelect(int r, int c) {
        if (usedValuesHelpIsActive) sendUsedValuesHelp(r, c);
        if (combinationsHelpIsActive) sendCombinationsHelp(r, c);
    }

    private void sendUsedValuesHelp(int r, int c) {
        viewCtrl.markButtonPanelInRed(rowValuesUsed[rowIDs[r][c]] | colValuesUsed[colIDs[r][c]], currentGame.getBoard().getValue(r, c));
    }

    private void sendCombinationsHelp(int r, int c) {
        int rowID = rowIDs[r][c];
        int colID = colIDs[r][c];
        int rowSum = currentGame.getBoard().getHorizontalSum(firstRowCoord[rowID].first, firstRowCoord[rowID].second-1);
        ArrayList<Integer> rowCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[rowID], rowSum, rowValuesUsed[rowID]);
        int colSum = currentGame.getBoard().getVerticalSum(firstColCoord[colID].first-1, firstColCoord[colID].second);
        ArrayList<Integer> colCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[colID], colSum, colValuesUsed[colID]);
        String rowResponse = "";
        for (int rCase : rowCases) {
            String partRowCase = "";
            for (int i = 0; i < 9; i++) if ((rCase & (1<<i)) != 0) partRowCase+=""+(i+1);
            if (rowResponse.length() > 0) rowResponse += ", ";
            rowResponse += partRowCase;
        }
        String colResponse = "";
        for (int cCase : colCases) {
            String partColCase = "";
            for (int i = 0; i < 9; i++) if ((cCase & (1<<i)) != 0) partColCase+=""+(i+1);
            if (colResponse.length() > 0) colResponse += ", ";
            colResponse += partColCase;
        }
        viewCtrl.setShowCombinations(rowResponse, colResponse);
    }

    private void autoEraseNotations(int r, int c) {
        int value = currentGame.getBoard().getValue(r, c);
        if (value == 0) return; // no values to be erased
        int rowID = rowIDs[r][c];
        int colID = colIDs[r][c];
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> message = new ArrayList<>();
        // Check in row
        for (int it = firstRowCoord[rowID].second; it < firstRowCoord[rowID].second+rowSize[rowID]; it++)
            if (currentGame.getBoard().cellHasNotation(r, it, value)) {
                currentGame.getBoard().setCellNotation(r, it, value, false);
                message.add(new Pair<>(new Pair<>(r, it), currentGame.getBoard().getCellNotations(r, it)));
            }
        // Check in column
        for (int it = firstColCoord[colID].first; it < firstColCoord[colID].first+colSize[colID]; it++)
            if (currentGame.getBoard().cellHasNotation(it, c, value)) {
                currentGame.getBoard().setCellNotation(it, c, value, false);
                message.add(new Pair<>(new Pair<>(it, c), currentGame.getBoard().getCellNotations(it, c)));
            }
        viewCtrl.setNotations(message);
    }

    public Pair<Pair<Integer, Integer>, Integer> getHint() {
        currentMovement = movementCount; // always give hint for most advanced move
        if (hintAtMove != -1 && hintAtMove == currentMovement) {
            // We already asked for a hint, just write the value
            if (lastHint.second != -1) { // write the value if it wasn't a move problem
                playMove(lastHint.first.first, lastHint.first.second, lastHint.second);
            }
            return lastHint;
        }
        hintAtMove = currentMovement;

        // Check if it has solution from current board;
        Solver solver = new Solver(currentGame.getBoard());
        boolean hasSolution = solver.solve() != 0;

        if (hasSolution) {
            // hint next move
            lastHint = trivialFinder();
            if (lastHint.second == -1) lastHint = simulateHintFinder();
        } else {
            // hint number of movement where it went wrong, set lastHint.second to -1
            int badMove = binarySearchBadMove(new Board(kakuro.getBoard()),1, currentMovement);
            lastHint.first.first = -1;
            lastHint.first.second = badMove;
            lastHint.second = -1;
        }
        return new Pair<>(lastHint.first,-1);
    }

    private int binarySearchBadMove(Board testingBoard, int left, int right) {
        if (right <= left) return left;
        int mid = (left+right)/2;
        ArrayList<Movement> moves = currentGame.getMovements();
        for (int i = left; i <= mid; i++) {
            Movement m = moves.get(i-1);
            if (m.getNext() == 0) testingBoard.clearCellValue(m.getCoordinates().first, m.getCoordinates().second);
            else testingBoard.setCellValue(m.getCoordinates().first, m.getCoordinates().second, m.getNext());
        }
        Solver s = new Solver(testingBoard);
        int response = s.solve();
        if (response == 0) { //No solution
            Board newTestingBoard = new Board(kakuro.getBoard());
            for (int i = 1; i < left; i++) {
                Movement m = moves.get(i-1);
                if (m.getNext() == 0) newTestingBoard.clearCellValue(m.getCoordinates().first, m.getCoordinates().second);
                else newTestingBoard.setCellValue(m.getCoordinates().first, m.getCoordinates().second, m.getNext());
            }
            return binarySearchBadMove(newTestingBoard, left, mid);
        } else {
            return binarySearchBadMove(testingBoard, mid+1, right);
        }
    }

    private Pair<Pair<Integer, Integer>, Integer> trivialFinder() {
        for (int r = 0; r < rowLineSize; r++) {
            if (Integer.bitCount(rowValuesUsed[r]) == rowSize[r]-1) { //find which is left to assign
                for (int it = firstRowCoord[r].second; it < firstRowCoord[r].second + rowSize[r]; it++) {
                    if (currentGame.getBoard().isEmpty(firstRowCoord[r].first, it)) {
                        return new Pair<>(new Pair<>(firstRowCoord[r].first, it), currentGame.getBoard().getHorizontalSum(firstRowCoord[r].first, firstRowCoord[r].second-1) - currentRowSums[r]);
                    }
                }
            }
        }
        for (int c = 0; c < colLineSize; c++) {
            if (Integer.bitCount(colValuesUsed[c]) == colSize[c]-1) { //find which is left to assign
                for (int it = firstColCoord[c].first; it < firstColCoord[c].first + colSize[c]; it++) {
                    if (currentGame.getBoard().isEmpty(it, firstColCoord[c].second)) {
                        return new Pair<>(new Pair<>(it, firstColCoord[c].second), currentGame.getBoard().getVerticalSum(firstColCoord[c].first-1, firstColCoord[c].second) - currentColSums[c]);
                    }
                }
            }
        }
        return new Pair<>(new Pair<>(-1, -1),-1);
    }

    private Pair<Pair<Integer, Integer>, Integer> simulateHintFinder() {
        Board testingBoard = new Board(kakuro.getBoard());
        int rows = testingBoard.getHeight();
        int columns = testingBoard.getWidth();
        // all white cells to 0 and full possibilities
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> forcedValues = new ArrayList();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (testingBoard.isBlackCell(r, c)) continue;
                if (!currentGame.getBoard().isEmpty(r, c)) forcedValues.add(new Pair<>(new Pair<>(r, c), currentGame.getBoard().getValue(r, c)));
                testingBoard.setCell(new WhiteCell(true), r, c);
            }
        }
        SwappingCellQueue testingCellQueue = new SwappingCellQueue(testingBoard);

        int[] testingRowSum = new int[rowLineSize];
        int[] testingColSum = new int[colLineSize];

        int[] testingRowValuesUsed = new int[rowLineSize];
        int[] testingColValuesUsed = new int[colLineSize];

        // prepare new row dataStructures
        for (int r = 0; r < rowLineSize; r++) {
            testingRowSum[r] = 0;
            testingRowValuesUsed[r] = 0;
        }
        // prepare new col dataStructures
        for (int c = 0; c < colLineSize; c++) {
            testingColSum[c] = 0;
            testingColValuesUsed[c] = 0;
        }

        KakuroFunctions testingFunctions = new KakuroFunctions(new KakuroFunctions.KakuroFunctionsMaster() {
            @Override
            public int getRowID(int r, int c) { return rowIDs[r][c]; }

            @Override
            public int getColID(int r, int c) { return colIDs[r][c]; }

            @Override
            public int getRowLineSize(int r, int c) { return rowLineSize; }

            @Override
            public int getColLineSize(int r, int c) { return colLineSize; }

            @Override
            public int getRowSum(int r, int c) { return testingRowSum[rowIDs[r][c]]; }

            @Override
            public int getColSum(int r, int c) { return testingColSum[colIDs[r][c]]; }

            @Override
            public void setRowSum(int r, int c, int value) { testingRowSum[rowIDs[r][c]] = value; }

            @Override
            public void setColSum(int r, int c, int value) { testingColSum[colIDs[r][c]] = value; }

            @Override
            public int getRowSize(int r, int c) { return rowSize[rowIDs[r][c]]; }

            @Override
            public int getColSize(int r, int c) { return colSize[colIDs[r][c]]; }

            @Override
            public int getRowValuesUsed(int r, int c) { return testingRowValuesUsed[rowIDs[r][c]]; }

            @Override
            public int getColValuesUsed(int r, int c) { return testingColValuesUsed[colIDs[r][c]]; }

            @Override
            public void setRowValuesUsed(int r, int c, int values) { testingRowValuesUsed[rowIDs[r][c]] = values; }

            @Override
            public void setColValuesUsed(int r, int c, int values) { testingColValuesUsed[colIDs[r][c]] = values; }

            @Override
            public Pair<Integer, Integer> getFirstRowCoord(int r, int c) { return firstRowCoord[rowIDs[r][c]]; }

            @Override
            public Pair<Integer, Integer> getFirstColCoord(int r, int c) { return firstColCoord[colIDs[r][c]]; }

            @Override
            public Board getWorkingBoard() { return testingBoard; }

            @Override
            public SwappingCellQueue getNotationsQueue() { return testingCellQueue; }
        });

        final Pair<Integer, Integer> currentAssignation = new Pair<>(-1, -1);
        final Pair<Pair<Integer, Integer>, Integer> finalHint = new Pair<>(new Pair<>(-1, -1), -1);

        testingFunctions.setAssignationEventListener(new KakuroFunctions.AssignationEventListener() {
            @Override
            public void onCellValueAssignation(Pair<Pair<Integer, Integer>, Integer> coord_value) {
                if (!currentGame.getBoard().isEmpty(coord_value.first.first, coord_value.first.second)) return;
                finalHint.first.first = coord_value.first.first;
                finalHint.first.second = coord_value.first.second;
                finalHint.second = coord_value.second;
                testingFunctions.abortOperation();
            }

            @Override
            public void onCellNotationsChanged(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> coord_prev_post) {}
            @Override
            public void onRowSumAssignation(Pair<Pair<Integer, Integer>, Integer> coord_value) {}
            @Override
            public void onColSumAssignation(Pair<Pair<Integer, Integer>, Integer> coord_value) {}
            @Override
            public void onCellNoValuesLeft(Pair<Integer, Integer> coord) {}
            @Override
            public void onRowNoValuesLeft(Pair<Integer, Integer> coord) {}
            @Override
            public void onColNoValuesLeft(Pair<Integer, Integer> coord) {}
        });

        for (Pair<Pair<Integer, Integer>, Integer> f : forcedValues) {
            currentAssignation.first = f.first.first;
            currentAssignation.second = f.first.second;
            testingFunctions.cellValueAssignation(f.first.first, f.first.second, f.second);
            if (finalHint.second != -1) return finalHint;
        }

        for (int rowID = 0; rowID < rowLineSize; rowID++) {
            int r = firstRowCoord[rowID].first;
            int c = firstRowCoord[rowID].second-1;
            currentAssignation.first = r;
            currentAssignation.second = c+1;
            testingFunctions.rowSumAssignation(r, c+1, testingBoard.getHorizontalSum(r, c));
            if (finalHint.second != -1) return finalHint;
        }

        // Make it appear more randomly distributed just for user experience
        Random random = new Random(currentMovement);
        ArrayList<Integer> distributedColIDs = new ArrayList<>();
        for (int colID = 0; colID < colLineSize; colID++) distributedColIDs.add(colID);
        Collections.shuffle(distributedColIDs, random);
        for (int idx = 0; idx < colLineSize; idx++) {
            int colID = distributedColIDs.get(idx);
            int r = firstColCoord[colID].first-1;
            int c = firstColCoord[colID].second;
            currentAssignation.first = r+1;
            currentAssignation.second = c;
            testingFunctions.colSumAssignation(r+1, c, testingBoard.getVerticalSum(r, c));
            if (finalHint.second != -1) return finalHint;
        }

        // if it reaches this point it looks like there is no hint to be given
        return new Pair<>(new Pair<>(-1, -1), -1);
    }

    public Pair<Boolean, String> exportKakuro(String file) {
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(currentGame.getBoard().toString() + "\n");
            myWriter.close();
            return new Pair<>(true, null);
        } catch(IOException e) {
            return new Pair<>(false, e.getMessage());
        }
    }

    private void validateKakuro() {
        Solver solver = new Solver(currentGame.getBoard());

        if(solver.solve() == 1)
            viewCtrl.kakuroSolvedCorrectly();
    }
}
