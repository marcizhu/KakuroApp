package src;

import src.controllers.Solver;
import src.controllers.Reader;
import src.domain.Board;
import src.domain.WhiteCell;
import src.domain.Cell;
import src.domain.BlackCell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) {
		Reader r = new Reader("data/sample.kak");
		Board b = r.read();

		printBoard(b);

		// TODO: Debug solver
		Solver solver = new Solver(b);
		solver.solve();

		int s = solver.getSolutions().size();
		System.out.println("\nSolutions found: " + s + "\n");

		for (int i = 0; i < s; i++) {
			Board solution = solver.getSolutions().get(i);
			printBoard(solution);
			System.out.println();

			try {
				PrintWriter serializer = new PrintWriter("data/solved" + i + ".kak");
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
				Cell c = b.getCell(i, j);
				if (c instanceof BlackCell) {
					System.out.printf("[F%2d, C%2d] ", ((BlackCell) c).getHorizontalSum(), ((BlackCell) c).getVerticalSum());
				}
				else if (c instanceof WhiteCell){
					System.out.printf("[   %2d   ] ", c.getValue());
				}
				else {
					System.out.print("[  ????  ] ");
				}
			}
			System.out.println();
		}
	}
}
