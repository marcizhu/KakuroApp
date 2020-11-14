package src.controllers;

import src.domain.Board;
import src.domain.WhiteCell;
import src.domain.Difficulty;

import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private Board generatedBoard;

    private int rows;
    private int columns;
    private Difficulty difficulty;

    private Random random;

    private WhiteCell[] orderedCells; // Contains all the WhiteCells in increasing order of number of anotations
    private int[] startPos;           // 9 pointers to the first position with corresponding number of anotations
    private int endElement;           // should coincide with the size of the array
    private int firstElement;         // points to first element not yet used

    public Generator(int rows, int columns, Difficulty difficulty) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public Generator(int rows, int columns, Difficulty difficulty, long seed) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        this.random = new Random(seed);
    }

    private Board prepareWorkingBoard() {
        /* TODO: Generate the black cells on a board of size columns x rows for a given difficulty
        *   - The orderedCells size is rows*columns - numberOfBlackCells, it only contains WhiteCells
        *   - startPos is an array of "pointers" of size 9, startingPos[x-1] has the position in orderedCells
        *       where there is the first WhiteCell with x anotated values
        * */
        return new Board();
    }


    // TODO: THE THREE RECURSIVE ASSIGNATION METHODS BELOW COULD POSSIBLY FAIL AN ASSIGNATION SO THEY MUST RETURN A
    //  BOOLEAN INDICATING IF THEY WERE SUCCESSFUL, ALSO, IF THEY WEREN'T THEY SHOULD BE ABLE TO ROLLBACK ANY CHANGES
    //  TO VALUES, SUMS AND NOTATIONS THAT THEY HAVE CAUSED, WHICH MEANS THAT IN THE CASE THAT AN ASSIGNATION
    //  PROVOQUES MORE THAN ONE OTHER ASSIGNATION (RECURSIVELY), IF ANY OF THEM FAIL (AS THEY NEED TO SUCCEED TO MAKE
    //  THE CURRENT ASSIGNATION SUCCESSFUL), ALL OF THEM SHOULD ROLLBACK AND THE CURRENT ONE SHOULD PRESERVE THE VALUE,
    //  NOTATIONS, SUMS ETC. THAT IT HAD IN THE BEGINNING AND RETURN FALSE SO THAT THE RESPONSIBLE FOR ITS CALL
    //  KNOWS IT HAS FAILED.
    private boolean rowSumAssignation(int r, int c, int value) {
        // TODO: should update the row sum for a given coordinates to the value
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        return false;
    }

    private boolean colSumAssignation(int r, int c, int value) {
        // TODO: should update the col sum for a given coordinates to the value
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        return false;
    }

    private boolean cellValueAssignation(int r, int c, int value) {
        // TODO: should update the assignation for that cell, remove the notations, update the
        //  orderedCells data structure and its pointers, etc.
        //  Could call other assignations recursively
        return false;
    }

    public void generate() {
        // Fill the black cells in an empty board, all white cells should have all 9 values in anotations, should fill
        // the data structure to keep white cells ordered increasingly by number of anotations.
        Board workingBoard = prepareWorkingBoard();

        // TODO: probably should preprocess row and column spaces

        // Select some random white cells to begin the assignations, depending on difficulty
        int numOfStartingPoints = 0;
        switch(difficulty) {
            case EASY:
                numOfStartingPoints += 2; // EASY will have 6 starting points
            case MEDIUM:
                numOfStartingPoints += 2; // MEDIUM will have 4 starting points
            case HARD:
                numOfStartingPoints ++;   // HARD will have 2 starting points
            case EXTREME:
                numOfStartingPoints ++;   // EXTREME will have 1 starting point
        }
        for (int start = 0; start < numOfStartingPoints; start++) {
            int coordRow = random.nextInt(rows);
            int coordCol = random.nextInt(columns);

            if (workingBoard.getValue(coordRow, coordCol) == 0) {
                int rowSpace = 0, colSpace = 0; // TODO: this is where we assign according to precomputed space values
                ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSpace, colSpace, difficulty); // returns [] of {rowSum, colSum, valueInCommon}
                for (int[] uniqueValue : uniqueCrossValues) {
                    // TODO: assign uniqueValue[0] to the row sum, uniqueValue[1] to de column sum
                    //  and uniqueValue[2] to de WhiteCell value in [coordRow][coordCol]
                    //  these assignments will take care to modify the notations and call other
                    //  assignments recursively as well as change the pointers to orderedCells accordingly
                    //  , if an assignment is successful we shouldn't call the next ones;
                }
            }
        }

        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous

        while (firstElement < endElement) {
            // then we have elements with no known value so we must do an assignation
            WhiteCell candidate = orderedCells[firstElement];
            firstElement ++;

            // if one or both of the sums are not assigned, we should choose the value in
            // its notations that given the current values in the row and column there is a unique value for a
            // certain sum assignation

            // if both sums are already assigned, then that means there can still go more than one option in this
            // cell, so we choose and the repercutions of the choice hopefully will make it a unique choice in a
            // posterior sum assignment that affects other positions in the same row/col.
        }

        // when we get out of the while loop we should have a filled board generated,
        // maybe we want to send it to the solver to check if it's unique or not or check for permutations, etc.
    }
}
