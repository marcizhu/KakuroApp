package src.controllers;

import src.domain.BlackCell;
import src.domain.Cell;
import src.domain.Board;
import src.domain.WhiteCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Solver {
	private Board board;
	private ArrayList<Board> solutions;
	private int[][] rowSums;
    private int[][] colSums;
    private int[][] rowSize;
    private int[][] colSize;

	public Solver(Board b) {
		this.board = b;
        this.solutions = new ArrayList<>();
        this.rowSums = new int[board.getHeight()][board.getWidth()];
        this.colSums = new int[board.getHeight()][board.getWidth()];
        this.rowSize = new int[board.getHeight()][board.getWidth()];
        this.colSize = new int[board.getHeight()][board.getWidth()];
    }

    public void solve() {
	    preprocessRowSums();
	    preprocessColSums();
	    preprocessRowSize();
        preprocessColSize();

        solve(0, 0, 0);
    }

    private void preprocessRowSums() {
	    // Calculate sums for each row
	    for(int i = 0; i < board.getHeight(); i++) {
	        for(int j = 0; j < board.getWidth(); j++) {
	            if(board.isBlackCell(i, j)) {
	                rowSums[i][j] = ((BlackCell)board.getCell(i, j)).getHorizontalSum();
                } else {
	                // TODO: Check for out-of-bounds access.
                    rowSums[i][j] = rowSums[i][j - 1];
                }
            }
        }
    }

    private void preprocessRowSize() {
	    int size = 0;
        for(int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    for(int k = size; k > 0; k--)
                        rowSize[i][j - k] = size;
                    size = 0;
                } else {
                    size++;
                }
            }

            for(int k = size; k > 0; k--)
                rowSize[i][board.getWidth() - k] = size;
            size = 0;
        }
    }

    private void preprocessColSums() {
        for(int i = 0; i < board.getHeight(); i++) {
            for(int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    colSums[i][j] = ((BlackCell)board.getCell(i, j)).getVerticalSum();
                } else {
                    // TODO: Check for out-of-bounds access.
                    colSums[i][j] = colSums[i - 1][j];
                }
            }
        }
    }

    private void preprocessColSize() {
        int size = 0;
        for (int j = 0; j < board.getWidth(); j++) {
            for(int i = 0; i < board.getHeight(); i++) {
                if(board.isBlackCell(i, j)) {
                    for(int k = size; k > 0; k--)
                        colSize[i - k][j] = size;
                    size = 0;
                } else {
                    size++;
                }
            }

            for(int k = size; k > 0; k--)
                colSize[board.getHeight() - k][j] = size;
            size = 0;
        }
    }

    private ArrayList<Integer> getPossibleValuesAlternative(int row, int col) {
	    // Horizontal
        int hSpaces = 1;
        int hSum = -1;
        boolean[] hUsedValues = {false,false,false,false,false,false,false,false,false};
        // recorrer la fila i veure quins nombres s'han utilitzat així com l'espai i la suma total cap a l'esquerra
        int it = col-1;
        while(hSum == -1 && it >= 0) {
            Cell cell = board.getCell(row, it);
            if (cell instanceof BlackCell) {
                // la primera cell negre cap a l'esquerra hauria de tenir el valor de la suma de la fila (a la dreta no té perquè)
                //i sempre hi haurà una cell a l'esquerra obligatòriament
                hSum = ((BlackCell)cell).getHorizontalSum();
            } else {
                hSpaces++;
                int v = ((WhiteCell)cell).getValue();
                if (v!=0) hUsedValues[v-1] = true;
                it--;
            }
        }
        //recorrem cap a la dreta fins trobar una negre o el final del board
        it = col+1;
        while(it < board.getWidth()) {
            Cell cell = board.getCell(row, it);
            if (cell instanceof BlackCell) break; //hem trobat el final de la línia.
            else {
                hSpaces++;
                int v = ((WhiteCell)cell).getValue();
                if (v!=0) hUsedValues[v-1] = true;
                it++;
            }
        }

        //Vertical
        int vSpaces = 1;
        int vSum = -1;
        boolean[] vUsedValues = {false,false,false,false,false,false,false,false,false};
        // recorrer la columna i veure quins nombres s'han utilitzat així com l'espai i la suma total cap a l'esquerra
        it = row-1;
        while(vSum == -1 && it >= 0) {
            Cell cell = board.getCell(it, col);
            if (cell instanceof BlackCell) {
                // la primera cell negre cap amunt hauria de tenir el valor de la suma de la columna (a sota no té perquè)
                // i sempre hi haurà una cell amunt obligatòriament
                vSum = ((BlackCell)cell).getVerticalSum();
            } else {
                vSpaces++;
                int v = ((WhiteCell)cell).getValue();
                if (v!=0) vUsedValues[v-1] = true;
                it--;
            }
        }
        //recorrem cap a la dreta fins trobar una negre o el final del board
        it = row+1;
        while(it < board.getHeight()) {
            Cell cell = board.getCell(it, col);
            if (cell instanceof BlackCell) break; //hem trobat el final de la columna.
            else {
                vSpaces++;
                int v = ((WhiteCell)cell).getValue();
                if (v!=0) vUsedValues[v-1] = true;
                it++;
            }
        }

        //ara tenim tota la info que ens fa falta havent recorregur la "creu" on es troba el punt només un cop
        // veiem quines possibilitats tenim vertical i horitzontalment
        ArrayList<ArrayList<Integer>> hOptions = KakuroConstants.INSTANCE.getPossibleCases(hSpaces, hSum);
        ArrayList<ArrayList<Integer>> vOptions = KakuroConstants.INSTANCE.getPossibleCases(vSpaces, vSum);

        // ara veiem quins nombres coincideixen en alguna llista horitzontal i alguna llista vertical
        // això sembla molt lleig i ineficient però cap dels arrays té més de 12 arrays de ints i un cop hem trobat un ja no el tornem a buscar
        // una altra opció és implementar al KakuroConstants que li puguis passar quins nombres has vist i faci ell la tria
        boolean[] availableValues = {false,false,false,false,false,false,false,false,false};
        try {
            for (ArrayList<Integer> hOpt : hOptions)
                for (Integer i : hOpt)
                    if (!availableValues[i - 1])
                        for (ArrayList<Integer> vOpt : vOptions)
                            for (Integer j : vOpt)
                                if (i.intValue() == j.intValue()) availableValues[i - 1] = true;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            if (hOptions == null) System.out.println("hOptions is null, hSpaces: "+hSpaces+" , hSum: "+hSum);
            if (vOptions == null) System.out.println("vOptions is null, vSpaces: "+vSpaces+" , vSum: "+vSum);
            //System.out.println(hOptions.size()+" horizontal options, vertical options: "+vOptions.size());
        }

        //fem la intersecció i retornem el resultat.
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (availableValues[i] && !hUsedValues[i] && !vUsedValues[i]) result.add(i+1);
        }
        return result;
	}

    // TODO: reimplement using KakuroConstants to get possible combinations
    private ArrayList<Integer> getPossibleValues(int row, int col, int rowSum) {
        ArrayList<Integer> possibleValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        int horizontalSum = rowSums[row][col];
        int horizontalCurrentSum = rowSum;

        // TODO: Should be fixed but it's not tested
        int finalHorizontalCurrentSum = horizontalCurrentSum;
        possibleValues.removeIf(n -> ((n + finalHorizontalCurrentSum) > horizontalSum));

        int verticalSum = colSums[row][col];
        int verticalCurrentSum = 0;
        int verticalEmptyCount = 1;
        for (int r = row - 1; r > 0; r--) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.removeIf(val -> val == value);
                }
                else verticalEmptyCount++;
            }
        }
        for (int r = row + 1; r < board.getHeight(); r++) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.removeIf(val -> val == value);
                }
                else verticalEmptyCount++;
            }
        }

        int finalVerticalCurrentSum = verticalCurrentSum;
        possibleValues.removeIf(n -> ((n + finalVerticalCurrentSum) > verticalSum));

        return possibleValues;
    }

    private void solve(int row, int col, int rowSum) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(new Board(board));
            return;
        }

        if (col >= board.getWidth()) {
            solve(row + 1, 0, 0);
            return;
        }

        if (board.isBlackCell(row, col)) {
            solve(row, col + 1, 0); // If cell type is black, continue solving
            return;
        }

        if (!board.isEmpty(row, col)) {
            solve(row, col + 1, 0); // If cell has a value, continue solving
            return;
        }

        //ArrayList<Integer> possibleValues = getPossibleValues(row, col, rowSum);
        ArrayList<Integer> possibleValues = getPossibleValuesAlternative(row, col);

        for (int i : possibleValues) {
            board.setCellValue(row, col, i);
            solve(row, col + 1, rowSum + i);
            board.clearCellValue(row, col);
            if (solutions.size() > 1) return;
        }
    }

	public ArrayList<Board> getSolutions() {
		return solutions;
    }
}
