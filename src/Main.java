package src;

import src.controllers.Solver;
import src.controllers.Reader;
import src.domain.Board;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		Board b = Reader.fromFile("data/kakuros/unsolved/two-sol.kak");
		printBoard(b);

		Solver solver = new Solver(b);
		int s = solver.solve();
		System.out.println("\nSolutions found: " + s + "\n");

		for (int i = 0; i < s; i++) {
			Board solution = solver.getSolutions().get(i);
			printBoard(solution);
			System.out.println();

			try {
				PrintWriter serializer = new PrintWriter("data/kakuros/solved/two-sol-" + (i+1) + ".kak");
				serializer.println(solution.toString());
				serializer.close();
			} catch(FileNotFoundException e) {
				System.err.println("An error occurred while writing to file");
			}
		}
	}

	// For debugging purposes only
	public static void printBoard(Board b) {
		for (int i = 0; i<b.getHeight() ; i++) {
			for(int j = 0; j<b.getWidth() ; j++) {
				/**/ if (b.isBlackCell(i, j)) System.out.printf("[F%2d, C%2d] ", b.getHorizontalSum(i, j), b.getVerticalSum(i, j));
				else if (b.isWhiteCell(i, j)) System.out.printf("[   %2d   ] ", b.getValue(i, j));
				else {
					System.out.print("[  ????  ] ");
				}
			}
			System.out.println();
		}
	}
}
