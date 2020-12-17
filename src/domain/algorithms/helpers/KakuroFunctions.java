package src.domain.algorithms.helpers;

import src.domain.entities.Board;
import src.domain.entities.WhiteCell;
import src.utils.Pair;

import java.util.ArrayList;

public class KakuroFunctions {

    private final KakuroFunctionsMaster master;
    private AssignationEventListener assignationEventListener;
    private boolean abort;

    public interface KakuroFunctionsMaster {
        int getRowID(int r, int c); //id between 0 and number of rowLines-1
        int getColID(int r, int c); //id between 0 and number of columnLines-1

        int getRowLineSize(int r, int c);
        int getColLineSize(int r, int c);

        int getRowSum(int r, int c);
        int getColSum(int r, int c);
        void setRowSum(int r, int c, int value);
        void setColSum(int r, int c, int value);

        int getRowSize(int r, int c);
        int getColSize(int r, int c);

        int getRowValuesUsed(int r, int c);
        int getColValuesUsed(int r, int c);
        void setRowValuesUsed(int r, int c, int values);
        void setColValuesUsed(int r, int c, int values);

        Pair<Integer, Integer> getFirstRowCoord(int r, int c);
        Pair<Integer, Integer> getFirstColCoord(int r, int c);

        Board getWorkingBoard();
        SwappingCellQueue getNotationsQueue();
    }

    public interface AssignationEventListener {
        // Non-confirmed modifications, only acceptable if operation is successful.
        // Called right before change is updated.
        void onCellValueAssignation (Pair<Pair<Integer, Integer>, Integer> coord_value);
        void onCellNotationsChanged (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> coord_prev_post);
        void onRowSumAssignation (Pair<Pair<Integer, Integer>, Integer> coord_value);
        void onColSumAssignation (Pair<Pair<Integer, Integer>, Integer> coord_value);

        // Confirmed conflicting events, cause the operation to fail.
        void onCellNoValuesLeft (Pair<Integer, Integer> coord);
        void onRowNoValuesLeft (Pair<Integer, Integer> coord);
        void onColNoValuesLeft (Pair<Integer, Integer> coord);

        //Note: Row and col related pass coordinates of some cell inside the row/col.
    }

    public KakuroFunctions(KakuroFunctionsMaster m) {
        master = m;
        abort = false;
    }

    public void setAssignationEventListener(AssignationEventListener listener) {
        assignationEventListener = listener;
    }
    public void abortOperation() {
        abort = true;
    }

    public boolean cellValueAssignation(int r, int c, int value) {
        if (value == 0) return false;
        abort = false;
        ArrayList<Pair<Integer, Integer>> rowSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> colSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> cellValueRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
        boolean[] modifiedRows = new boolean[master.getRowLineSize(r, c)]; //default to false
        boolean[] modifiedCols = new boolean[master.getColLineSize(r, c)]; //default to false
        boolean success = cellValueAssignation(r, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
        return success;
    }


    public boolean rowSumAssignation(int r, int c, int value) { return initRowSumAssignation(r, c, value, false); }
    public boolean rowSumAssignationAssertCellValueAssigned(int r, int c, int value) { return initRowSumAssignation(r, c, value, true); }
    private boolean initRowSumAssignation(int r, int c, int value, boolean assertRCValueAssigned) {
        if (value == 0) return false;
        abort = false;
        ArrayList<Pair<Integer, Integer>> rowSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> colSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> cellValueRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
        boolean[] modifiedRows = new boolean[master.getRowLineSize(r, c)]; //default to false
        boolean[] modifiedCols = new boolean[master.getColLineSize(r, c)]; //default to false
        boolean success = rowSumAssignation(r, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        if (!success || (assertRCValueAssigned && master.getWorkingBoard().isEmpty(r, c))) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
        return success;
    }

    public boolean colSumAssignation(int r, int c, int value) { return initColSumAssignation(r, c, value, false); }
    public boolean colSumAssignationAssertCellValueAssigned(int r, int c, int value) { return initColSumAssignation(r, c, value, true); }
    private boolean initColSumAssignation(int r, int c, int value, boolean assertRCValueAssigned) {
        if (value == 0) return false;
        abort = false;
        ArrayList<Pair<Integer, Integer>> rowSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> colSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> cellValueRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
        boolean[] modifiedRows = new boolean[master.getRowLineSize(r, c)]; //default to false
        boolean[] modifiedCols = new boolean[master.getColLineSize(r, c)]; //default to false
        boolean success = colSumAssignation(r, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        if (!success || (assertRCValueAssigned && master.getWorkingBoard().isEmpty(r, c))) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
        return success;
    }

    public boolean bothRowColSumAssignation(int r, int c, int rowValue, int colValue) { return initBothRowColSumAssignation(r, c, rowValue, colValue, false); }
    public boolean bothRowColSumAssignationAssertCellValueAssigned(int r, int c, int rowValue, int colValue) { return initBothRowColSumAssignation(r, c, rowValue, colValue, true); }
    private boolean initBothRowColSumAssignation(int r, int c, int rowValue, int colValue, boolean assertRCValueAssigned) {
        if (rowValue == 0 || colValue == 0) return false;
        abort = false;
        ArrayList<Pair<Integer, Integer>> rowSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> colSumRollBack = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> cellValueRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
        ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
        boolean[] modifiedRows = new boolean[master.getRowLineSize(r, c)]; //default to false
        boolean[] modifiedCols = new boolean[master.getColLineSize(r, c)]; //default to false
        boolean success = rowSumAssignation(r, c, rowValue, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        success = success && colSumAssignation(r, c, colValue, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        if (!success || (assertRCValueAssigned && master.getWorkingBoard().isEmpty(r, c))) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
        return success;
    }

    // Generator also need to use this independently of an assignation process
    public boolean isCombinationPossible(ArrayList<Integer> comb, ArrayList<WhiteCell> cells) {
        if (cells.size() < comb.size() || comb.size() == 0) return false;
        if (comb.size() == 1) {
            int digit = comb.get(0);
            for (WhiteCell c : cells) {
                if ((!c.isEmpty() && c.getValue() == digit) || (c.isEmpty() && c.isNotationChecked(digit))) return true;
            }
            return false;
        }

        WhiteCell c = cells.get(0);
        cells.remove(0);
        boolean success = false;
        if (c.isEmpty()) {
            for (int j = comb.size()-1; !success && j >= 0; j--) {
                int digit = comb.get(j);
                if (c.isNotationChecked(digit)) {
                    comb.remove(j);
                    success = isCombinationPossible(comb, cells);
                    comb.add(j, digit);
                }
            }
        } else {
            for (int j = 0; j < comb.size(); j++) {
                if (comb.get(j) == c.getValue()) {
                    comb.remove(j);
                    success = isCombinationPossible(comb, cells);
                    comb.add(j, c.getValue());
                    cells.add(0, c);
                    return success;
                }
            }
        }
        cells.add(0, c);

        return success;
    }

    // THE THREE RECURSIVE ASSIGNATION METHODS BELOW COULD POSSIBLY FAIL AN ASSIGNATION SO THEY MUST RETURN A
    //  BOOLEAN INDICATING IF THEY WERE SUCCESSFUL, ALSO, IF THEY WEREN'T THE RESPONSIBLE FOR THE CALL
    //  SHOULD BE ABLE TO ROLLBACK ANY CHANGES TO VALUES, SUMS AND NOTATIONS THAT THEY HAVE CAUSED, WHICH MEANS
    //  THAT IN THE CASE THAT AN ASSIGNATION PROVOQUES MORE THAN ONE OTHER ASSIGNATION (RECURSIVELY),
    //  IF ANY OF THEM FAIL (AS THEY ALL NEED TO SUCCEED TO MAKE THE FIRST ASSIGNATION SUCCESSFUL),
    //  ALL OF THEM SHOULD ROLLBACK AND THE CURRENT ONE SHOULD PRESERVE THE VALUE, NOTATIONS, SUMS ETC. THAT
    //  IT HAD IN THE BEGINNING AND RETURN FALSE SO THAT THE RESPONSIBLE FOR ITS CALL
    //  KNOWS IT HAS FAILED AND CAN DO THE ROLLBACK.
    // RollBack structures are to add any change we do, we don't do rollback in these functions
    // because we only change absolutely necessary cases that depend only on the first assignation call,
    // thus the responsible for the rollback is the first caller
    private boolean rowSumAssignation(int r, int c, int value, ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        if (abort) return false;
        // Should update the row sum for a given coordinates to the value and add row to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        if (master.getRowSum(r, c) != 0) {
            if (master.getRowSum(r, c) == value) return true; //the assignation has already happened, no problem
            return false; //already has a sum value assigned
        }
        if (assignationEventListener != null) assignationEventListener.onRowSumAssignation(new Pair<>(new Pair<>(r, c), value));
        master.setRowSum(r, c, value);
        modifiedRows[master.getRowID(r, c)] = true;
        rowSumRollBack.add(new Pair<>(r, c));
        return updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
    }

    private boolean colSumAssignation(int r, int c, int value, ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        if (abort) return false;
        // Should update the col sum for a given coordinates to the value, and add column to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        if (master.getColSum(r, c) != 0) {
            if (master.getColSum(r, c) == value) return true; //the assignation has already happened, no problem
            return false; //already has a sum value assigned
        }
        if (assignationEventListener != null) assignationEventListener.onColSumAssignation(new Pair<>(new Pair<>(r, c), value));
        master.setColSum(r, c, value);
        modifiedCols[master.getColID(r, c)] = true;
        colSumRollBack.add(new Pair<>(r, c));
        return updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
    }

    private boolean cellValueAssignation(int r, int c, int value, ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        if (abort) return false;
        // Should update the assignation for that cell, set the value, update the orderedCells data structure
        //  and its pointers with removeOrderedCell, update valuesUsed for row and col, and add it to rollback.
        //  Could call other assignations recursively
        if ((master.getRowValuesUsed(r, c) & (1<<(value-1))) != 0 ||  (master.getColValuesUsed(r, c) & (1<<(value-1))) != 0 || !master.getWorkingBoard().isEmpty(r, c)){
            if ((master.getRowValuesUsed(r, c) & (1<<(value-1))) != 0 && (master.getColValuesUsed(r, c) & (1<<(value-1))) != 0 && master.getWorkingBoard().getValue(r, c) == value) {
                return true; // assignation is redundant, we already had it assigned so we give it as correct
            }
            return false;
        }

        //check that adding this value doesn't make the whole sum greater than it has to be for assigned sums
        if (master.getRowSum(r, c) != 0 || master.getColSum(r, c) != 0) {
            int rowS = 0, colS = 0;
            for (int i = 0; i < 9; i++) {
                if ((master.getRowValuesUsed(r, c) & (1<<i)) != 0) rowS += (i+1);
                if ((master.getColValuesUsed(r, c) & (1<<i)) != 0) colS += (i+1);
            }
            if ((master.getRowSum(r, c) != 0 && rowS+value > master.getRowSum(r, c)) || (master.getColSum(r, c) != 0 && colS+value > master.getColSum(r, c)))
                return false; //new sum would be greater than it should
        }

        cellValueRollBack.add(new Pair<>(r, c)); //if rollback we clear these coordinates and insert in notationsQueue
        if (master.getWorkingBoard().getCellNotationSize(r, c) > 1) { //cell notations should be removed (important in ambiguity checking), this won't be checked before then.
            int cellNotations = master.getWorkingBoard().getCellNotations(r, c);
            if (master.getNotationsQueue().isHiding(r, c)) hidingCellNotationsRollBack.add(new RollbackNotations(r, c, cellNotations));
            else cellNotationsRollBack.add(new RollbackNotations(r, c, cellNotations));
            master.getNotationsQueue().eraseNotationsFromCell(r, c, (cellNotations & ~(1<<(value-1))));
        }
        if (assignationEventListener != null) assignationEventListener.onCellValueAssignation(new Pair<>(new Pair<>(r, c), value));
        master.getNotationsQueue().removeOrderedCell(r, c); // removes it from queue but notations are maintained
        master.getWorkingBoard().setCellValue(r, c, value);
        master.setRowValuesUsed(r, c, master.getRowValuesUsed(r, c) | 1 << (value-1));
        master.setColValuesUsed(r, c, master.getColValuesUsed(r, c) | 1 << (value-1));
        modifiedRows[master.getRowID(r, c)] = true;
        modifiedCols[master.getColID(r, c)] = true;
        boolean success = true;
        success = success && updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        success = success && updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        return success;
    }

    private boolean updateRowNotations(int r, int c, ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        if (abort) return false;
        //updates the notations of the row and can cause assignations, returns whether the update was successful
        ArrayList<Integer> affectedColumns = new ArrayList<>();

        ArrayList<Integer> possibleCases;

        if (master.getRowSum(r, c) != 0) { // row sum is assigned
            // get possible cases for the row
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(master.getRowSize(r, c), master.getRowSum(r, c), master.getRowValuesUsed(r, c));

        } else { // row sum is NOT assigned
            // get possible cases for the row
            ArrayList<Pair<Integer, Integer>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(master.getRowSize(r, c), master.getRowValuesUsed(r, c));
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, Integer> p : multiplePossibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                possibleCases.add(p.second);
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                return rowSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            }
        }

        // validate combinations or erase if not valid
        for (int i = possibleCases.size()-1; i >= 0; i--) {
            int pCase = possibleCases.get(i);
            ArrayList<Integer> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
            ArrayList<WhiteCell> containingCells = new ArrayList<>();
            for (int it = master.getFirstRowCoord(r, c).second; it < master.getFirstRowCoord(r, c).second+master.getRowSize(r, c); it++) {
                for (int digit : p) {
                    if ((!master.getWorkingBoard().isEmpty(r, it) && master.getWorkingBoard().getValue(r, it) == digit) || master.getWorkingBoard().cellHasNotation(r, it, digit)) {
                        containingCells.add((WhiteCell)master.getWorkingBoard().getCell(r, it));
                        break;
                    }
                }
            }
            if (!isCombinationPossible(p, containingCells)) possibleCases.remove(i);
        }

        if (possibleCases.size() == 0) {
            if (assignationEventListener != null) assignationEventListener.onRowNoValuesLeft(new Pair<>(r, c));
            return false; //No possible assignations for row.
        }

        int rowOptions = 0;
        for(int p : possibleCases) rowOptions |= p;

        // subtract the already used values
        int commonRowNotations = rowOptions & ~master.getRowValuesUsed(r, c);
        boolean superPermissive = commonRowNotations == 0b111111111;

        ArrayList<Integer> allToErase = new ArrayList<>();
        int minRowNotSize = 10;
        for(int it = 0; it < master.getRowSize(r, c); it++) {
            allToErase.add(it, 0);
            if (master.getWorkingBoard().isEmpty(r, it+master.getFirstRowCoord(r, c).second)){
                int s = master.getWorkingBoard().getCellNotationSize(r, it+master.getFirstRowCoord(r, c).second);
                if (s < minRowNotSize) minRowNotSize = s;
            }
        }
        if (minRowNotSize < master.getRowSize(r, c)) {
            int num_cells = master.getRowSize(r, c);
            int[] size = new int[num_cells];
            int[] notations = new int[num_cells];
            int[] insertPtrs = new int[num_cells];
            for(int it = 0; it < num_cells; it++) {
                insertPtrs[it] = it;
                size[it] = 0;
                notations[it] = 0;
                for (int i = 0; i < 9; i++) {
                    if (master.getWorkingBoard().cellHasNotation(r, it + master.getFirstRowCoord(r, c).second, i+1)) {
                        notations[it] |= (1<<i);
                        size[it]++;
                    }
                }
            }
            deepNotationAnalysis(num_cells, size, notations, insertPtrs, allToErase);
            for(int it = 0; superPermissive && it < master.getRowSize(r, c); it++)
                superPermissive = allToErase.get(it) == 0;
        }

        // check for each non-set white-cell if its notations have some notation that is not in commonRowNotations
        // if so, erase notations, mark column as affected, add cell notations to rollback

        for(int it = master.getFirstRowCoord(r, c).second; !superPermissive && it < master.getFirstRowCoord(r, c).second+master.getRowSize(r, c); it++) {
            if (master.getWorkingBoard().isEmpty(r, it)) { //value not set
                int cellNotations = master.getWorkingBoard().getCellNotations(r, it);
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                int valuesToErase = (cellNotations & ~commonRowNotations) | allToErase.get(it - master.getFirstRowCoord(r, c).second);

                if (valuesToErase != 0) { // we need to erase some notations
                    if (assignationEventListener != null) assignationEventListener.onCellNotationsChanged(new Pair<>(new Pair<>(r, it), new Pair<>(cellNotations, cellNotations & ~valuesToErase)));
                    modifiedRows[master.getRowID(r, c)] = true;
                    modifiedCols[master.getColID(r, it)] = true;
                    affectedColumns.add(it);
                    if (master.getNotationsQueue().isHiding(r, it)) hidingCellNotationsRollBack.add(new RollbackNotations(r, it, cellNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(r, it, cellNotations));
                    master.getNotationsQueue().eraseNotationsFromCell(r, it, valuesToErase);
                }
            }
        }

        boolean success = true;
        if (affectedColumns.size() == 0) modifiedRows[master.getRowID(r, c)] = false; //the cells on this row were not modified.
        for (int affected : affectedColumns) {
            int notationSize = master.getWorkingBoard().getCellNotationSize(r, affected);
            if (notationSize == 0) {
                if (assignationEventListener != null) assignationEventListener.onCellNoValuesLeft(new Pair<>(r, affected));
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                int cellNotations = master.getWorkingBoard().getCellNotations(r, affected);
                for (int i = 0; value == -1 && i < 9; i++) if((cellNotations&(1<<i)) != 0) value = i+1;
                success = success && cellValueAssignation(r, affected, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateColNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
                if (modifiedRows[master.getRowID(r, c)]) success = success && updateRowNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
            }

            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean updateColNotations(int r, int c, ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        if (abort) return false;
        //updates the notations of the column and can cause assignations, returns whether the update was successful
        ArrayList<Integer> affectedRows = new ArrayList<>();

        ArrayList<Integer> possibleCases;

        if (master.getColSum(r, c) != 0) { // col sum is assigned
            // get possible cases for the col
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(master.getColSize(r, c), master.getColSum(r, c), master.getColValuesUsed(r, c));

        } else { // col sum is NOT assigned
            // get possible cases for the col
            ArrayList<Pair<Integer, Integer>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(master.getColSize(r, c), master.getColValuesUsed(r, c));
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, Integer> p : multiplePossibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                possibleCases.add(p.second);
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                return colSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            }
        }

        // validate combinations or erase if not valid
        for (int i = possibleCases.size()-1; i >= 0; i--) {
            int pCase = possibleCases.get(i);
            ArrayList<Integer> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
            ArrayList<WhiteCell> containingCells = new ArrayList<>();
            for (int it = master.getFirstColCoord(r, c).first; it < master.getFirstColCoord(r, c).first+master.getColSize(r, c); it++) {
                for (int digit : p) {
                    if ((!master.getWorkingBoard().isEmpty(it, c) && master.getWorkingBoard().getValue(it, c) == digit) || master.getWorkingBoard().cellHasNotation(it, c, digit)) {
                        containingCells.add((WhiteCell)master.getWorkingBoard().getCell(it, c));
                        break;
                    }
                }
            }
            if (!isCombinationPossible(p, containingCells)) possibleCases.remove(i);
        }

        if (possibleCases.size() == 0) {
            if (assignationEventListener != null) assignationEventListener.onColNoValuesLeft(new Pair<>(r, c));
            return false; //No possible assignations for col.
        }

        int colOptions = 0;
        for(int p : possibleCases) colOptions |= p;

        // subtract the already used values
        int commonColNotations = colOptions & ~master.getColValuesUsed(r, c);
        boolean superPermissive = commonColNotations == 0b111111111;

        ArrayList<Integer> allToErase = new ArrayList<>();
        int minColNotSize = 10;
        for(int it = 0; it < master.getColSize(r, c); it++) {
            allToErase.add(it, 0);
            if (master.getWorkingBoard().isEmpty(it + master.getFirstColCoord(r, c).first, c)){
                int s = master.getWorkingBoard().getCellNotationSize(it + master.getFirstColCoord(r, c).first, c);
                if (s < minColNotSize) minColNotSize = s;
            }
        }
        if (minColNotSize < master.getColSize(r, c)) {
            int num_cells = master.getColSize(r, c);
            int[] size = new int[num_cells];
            int[] notations = new int[num_cells];
            int[] insertPtrs = new int[num_cells];
            for(int it = 0; it < master.getColSize(r, c); it++) {
                insertPtrs[it] = it;
                size[it] = master.getWorkingBoard().getCellNotationSize(it + master.getFirstColCoord(r, c).first, c);
                notations[it] = master.getWorkingBoard().getCellNotations(it + master.getFirstColCoord(r, c).first, c);
            }
            deepNotationAnalysis(num_cells, size, notations, insertPtrs, allToErase);
            for(int it = 0; superPermissive && it < master.getColSize(r, c); it++)
                superPermissive = allToErase.get(it) == 0;
        }

        for(int it = master.getFirstColCoord(r, c).first; !superPermissive && it < master.getFirstColCoord(r, c).first+master.getColSize(r, c); it++) {
            if (master.getWorkingBoard().isEmpty(it, c)) { //value not set
                int cellNotations = master.getWorkingBoard().getCellNotations(it, c);
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                int valuesToErase = (cellNotations & ~commonColNotations) | allToErase.get(it - master.getFirstColCoord(r, c).first);

                if (valuesToErase != 0) { // we need to erase some notations
                    if (assignationEventListener != null) assignationEventListener.onCellNotationsChanged(new Pair<>(new Pair<>(it, c), new Pair<>(cellNotations, cellNotations & ~valuesToErase)));
                    modifiedRows[master.getRowID(it, c)] = true;
                    modifiedCols[master.getColID(r, c)] = true;
                    affectedRows.add(it);
                    if (master.getNotationsQueue().isHiding(it, c)) hidingCellNotationsRollBack.add(new RollbackNotations(it, c, cellNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(it, c, cellNotations));
                    master.getNotationsQueue().eraseNotationsFromCell(it, c, valuesToErase);
                }
            }
        }

        boolean success = true;
        if (affectedRows.size() == 0) modifiedCols[master.getColID(r, c)] = false; //the cells on this column were not modified.
        for (int affected : affectedRows) {
            int notationSize = master.getWorkingBoard().getCellNotationSize(affected, c);
            if (notationSize == 0) {
                if (assignationEventListener != null) assignationEventListener.onCellNoValuesLeft(new Pair<>(affected, c));
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                int cellNotations = master.getWorkingBoard().getCellNotations(affected, c);
                for (int i = 0; value == -1 && i < 9; i++) if((cellNotations&(1<<i)) != 0) value = i+1;
                success = success && cellValueAssignation(affected, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateRowNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
                if (modifiedCols[master.getColID(r, c)]) success = success && updateColNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
            }
            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    // Pre: size, notations, insertPtrs and result have size num_cells, all arrays in results are declared.
    private void deepNotationAnalysis(int num_cells, int[] size, int[] notations, int[] insertPtrs, ArrayList<Integer> result) {
        if (num_cells == 0) return;

        // we order the cell info by size.
        for (int i = 0; i < num_cells; i++) { //selection sort because maximum there are 9 cells
            int minPos = i;
            for (int j = i+1; j < num_cells; j++)
                if (size[j] < size[minPos]) minPos = j;
            if (minPos != i) { // swap
                int swpSize, swpNts, swpPtrs;
                swpSize = size[i]; swpNts = notations[i]; swpPtrs = insertPtrs[i];
                size[i] = size[minPos]; notations[i] = notations[minPos]; insertPtrs[i] = insertPtrs[minPos];
                size[minPos] = swpSize; notations[minPos] = swpNts; insertPtrs[minPos] = swpPtrs;
            }
        }

        int ini = 0;
        while(ini < num_cells && size[ini] < 2) ini++;

        if (ini < num_cells)
            for (int length = size[ini]; length < num_cells; length++) {
                int end = num_cells-1;
                while (end >= ini && size[end] > length) end--;

                if (end - ini +1 < length) continue;

                int[] indices = new int[length];
                for (int j = 0; j < length; j++) {
                    indices[j] = ini+j;
                }

                boolean atEnd = false;
                while (!atEnd) {
                    // consider this possibility
                    int poss = 0;
                    for (int j : indices)
                        poss |= notations[j];

                    int checkSize = poss, nSize = 0;
                    while (checkSize > 0) {
                        nSize += (checkSize & 1);
                        checkSize >>>= 1;
                    }

                    if (nSize == length) { // these |length| values have to be in the corresponding cells, not in others
                        ArrayList<Integer> toErase = new ArrayList<>();
                        for (int j = 0; toErase.size() < length && j < 9; j++) {
                            if ((poss & (1<<j)) > 0) toErase.add(j);
                        }
                        int minLengthChanged = -1;
                        int kIdx = 0;
                        for (int j = 0; j < num_cells; j++) {
                            if (kIdx < length-1 && j > indices[kIdx]) kIdx++;
                            if (j != indices[kIdx]) {
                                for (int val : toErase) {
                                    if ((notations[j] & (1<<val)) > 0) { // has a value to be erased
                                        result.set(insertPtrs[j], (result.get(insertPtrs[j]) | (1<<val))); //mark it to erase
                                        notations[j] &= ~(1<<val); // erase from notations to calc.
                                        size[j]--;
                                        if (minLengthChanged == -1 || size[j] < minLengthChanged) minLengthChanged = size[j];
                                    }
                                }
                            }
                        }

                        // if any changes, reorganize and check the new lengths again, some might have new unique values.
                        if (minLengthChanged >= 0) {
                            for (int i = 0; i < num_cells; i++) { //selection sort because maximum there are 9 cells
                                int minPos = i;
                                for (int j = i+1; j < num_cells; j++)
                                    if (size[j] < size[minPos]) minPos = j;
                                if (minPos != i) { // swap
                                    int swpSize, swpNts, swpPtrs;
                                    swpSize = size[i]; swpNts = notations[i]; swpPtrs = insertPtrs[i];
                                    size[i] = size[minPos]; notations[i] = notations[minPos]; insertPtrs[i] = insertPtrs[minPos];
                                    size[minPos] = swpSize; notations[minPos] = swpNts; insertPtrs[minPos] = swpPtrs;
                                }
                            }

                            if (minLengthChanged < 2) {
                                ini = 0;
                                while(ini < num_cells && size[ini] < 2) ini++;
                                length = size[ini];
                                break;
                            } else if (minLengthChanged < length) {
                                length = minLengthChanged;
                                break;
                            }
                        }
                    }

                    if (indices[0] == end - (length-1)) atEnd = true;
                    for (int i = 1; !atEnd && i < length; i++) {
                        if (indices[i] == end - ((length-1) - i)) {
                            indices[i-1]++;
                            indices[i] = indices[i-1] + 1;
                            if (indices[i] != end - ((length-1) - i)) {
                                // idx was moved, must take the rest with him
                                for (int j = i+1; j < length; j++) indices[j] = indices[j-1]+1;
                            }
                            break;
                        } else if (i == length-1) {
                            indices[i]++;
                        }
                    }
                }
            }
    }

    private void rollBack(ArrayList<Pair<Integer, Integer>> rowSumRollBack, ArrayList<Pair<Integer, Integer>> colSumRollBack, ArrayList<Pair<Integer, Integer>> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack) {
        // Row sums
        for (Pair<Integer, Integer> row : rowSumRollBack) {
            master.setRowSum(row.first, row.second, 0);
        }
        // Col sums
        for (Pair<Integer, Integer> col : colSumRollBack) {
            master.setColSum(col.first, col.second, 0);
        }
        // Cell value
        for (Pair<Integer, Integer> c : cellValueRollBack) {
            master.getNotationsQueue().insertOrderedCell(c.first, c.second); // adds it to queue with previous notations
            int value = master.getWorkingBoard().getValue(c.first, c.second);
            master.getWorkingBoard().clearCellValue(c.first, c.second);
            master.setRowValuesUsed(c.first, c.second, (master.getRowValuesUsed(c.first, c.second) & ~(1<<(value-1))));
            master.setColValuesUsed(c.first, c.second, (master.getColValuesUsed(c.first, c.second) & ~(1<<(value-1))));
        }
        // Cell notations
        // notice that it is important to first insert the cells if needed, because if we don't the datastructure
        // will not consider it as valid and it won't find it to add the notations
        for (RollbackNotations n : cellNotationsRollBack)
            master.getNotationsQueue().addNotationsToCell(n.coord.first, n.coord.second, n.notations);

        for (RollbackNotations n : hidingCellNotationsRollBack) {
            master.getNotationsQueue().addNotationsToCell(n.coord.first, n.coord.second, n.notations);
            master.getNotationsQueue().hideElement(n.coord.first, n.coord.second);
        }
    }

    private class RollbackNotations {
        public Pair<Integer, Integer> coord;
        public int notations;
        public RollbackNotations(int r, int c, int n) {
            coord = new Pair<>(r,c);
            notations = n;
        }
    }
}
