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
	    preprocessSums();
	    preprocessRowSize();
        preprocessColSize();

        solve(0, 0, 0, new int[board.getWidth()]);
    }

    private void preprocessSums() {
	    // Calculate sums for each row
	    for(int i = 0; i < board.getHeight(); i++) {
	        for(int j = 0; j < board.getWidth(); j++) {
	            if(board.isBlackCell(i, j)) {
	                rowSums[i][j] = ((BlackCell)board.getCell(i, j)).getHorizontalSum();
                    colSums[i][j] = ((BlackCell)board.getCell(i, j)).getVerticalSum();
                } else {
	                // TODO: Check for out-of-bounds access.
                    rowSums[i][j] = rowSums[i][j - 1];
                    colSums[i][j] = colSums[i - 1][j];
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

    private ArrayList<Integer> getPossibleValues(int row, int col) {
	    // Horizontal
        int hSpaces = rowSize[row][col];
        int hSum = rowSums[row][col];
        boolean[] hUsedValues = { false, false, false, false, false, false, false, false, false };

        // recorrer la fila i veure quins nombres s'han utilitzat
        for(int it = col-1; board.isWhiteCell(row, it) && it >= 0; it--) {
            int v = board.getValue(row, it);
            if (v != 0) hUsedValues[v-1] = true;
        }

        //recorrem cap a la dreta fins trobar una negre o el final del board
        for(int it = col+1; it < board.getWidth() && board.isWhiteCell(row, it); it++) {
            int v = board.getValue(row, it);
            if (v != 0) hUsedValues[v-1] = true;
        }

        //Vertical
        int vSpaces = colSize[row][col];
        int vSum = colSums[row][col];
        boolean[] vUsedValues = { false, false, false, false, false, false, false, false, false };

        // recorrer la columna i veure quins nombres s'han utilitzat
        for(int it = row-1; board.isWhiteCell(it, col) && it >= 0; it--) {
            int v = board.getValue(it, col);
            if (v != 0) vUsedValues[v - 1] = true;
        }

        //recorrem cap a la dreta fins trobar una negre o el final del board
        for(int it = row+1; it < board.getHeight() && board.isWhiteCell(it, col); it++) {
            int v = board.getValue(it, col);
            if (v!=0) vUsedValues[v-1] = true;
        }

        //ara tenim tota la info que ens fa falta havent recorregur la "creu" on es troba el punt només un cop
        // veiem quines possibilitats tenim vertical i horitzontalment
        ArrayList<ArrayList<Integer>> hOptions = KakuroConstants.INSTANCE.getPossibleCases(hSpaces, hSum);
        ArrayList<ArrayList<Integer>> vOptions = KakuroConstants.INSTANCE.getPossibleCases(vSpaces, vSum);

        // ara veiem quins nombres coincideixen en alguna llista horitzontal i alguna llista vertical
        // això sembla molt lleig i ineficient però cap dels arrays té més de 12 arrays de ints i un cop hem trobat un ja no el tornem a buscar
        // una altra opció és implementar al KakuroConstants que li puguis passar quins nombres has vist i faci ell la tria
        boolean[] availableValues = { false, false, false, false, false, false, false, false, false };
        try {
            for (ArrayList<Integer> hOpt : hOptions)
                for (Integer i : hOpt)
                    if (!availableValues[i - 1])
                        for (ArrayList<Integer> vOpt : vOptions)
                            for (Integer j : vOpt)
                                if (i.intValue() == j.intValue()) availableValues[i - 1] = true;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            if (hOptions == null) System.out.println("hOptions is null, hSpaces: " + hSpaces + " , hSum: " + hSum);
            if (vOptions == null) System.out.println("vOptions is null, vSpaces: " + vSpaces + " , vSum: " + vSum);
        }

        // fem la intersecció i retornem el resultat.
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (availableValues[i] && !hUsedValues[i] && !vUsedValues[i])
                result.add(i+1);
        }

        return result;
	}

    private void solve(int row, int col, int rowSum, int[] colSum) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            if(rowSum != rowSums[row][col - 1]) return;

            for(int i = 0; i < colSum.length; i++)
                if(colSum[i] != 0 && colSum[i] != colSums[row - 1][i]) return;

            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(new Board(board));
            return;
        }

        if (col >= board.getWidth()) {
            if(rowSum != rowSums[row][col - 1]) return; // if row sum is not correct, return

            solve(row + 1, 0, 0, colSum);
            return;
        }

        if (board.isBlackCell(row, col)) {
            if(col > 0 && rowSum != rowSums[row][col - 1]) return; // if row sum is not correct, return
            if(row > 0 && colSum[col] != colSums[row - 1][col]) return; // if col sum is not correct, return

            int colSumTemp = colSum[col];
            colSum[col] = 0;
            solve(row, col + 1, 0, colSum); // If cell type is black, continue solving
            colSum[col] = colSumTemp;
            return;
        }

        if (!board.isEmpty(row, col)) {
            solve(row, col + 1, 0, colSum); // If cell has a value, continue solving
            return;
        }

        ArrayList<Integer> possibleValues = getPossibleValues(row, col);

        for (int i : possibleValues) {
            board.setCellValue(row, col, i);
            colSum[col] += i;
            solve(row, col + 1, rowSum + i, colSum);
            colSum[col] -= i;
            board.clearCellValue(row, col);
            if (solutions.size() > 1) return;
        }
    }

	public ArrayList<Board> getSolutions() {
		return solutions;
    }
}
