package src;

import src.controllers.Solver;
import src.controllers.Reader;
import src.controllers.Writer;
import src.domain.Board;
import src.domain.WhiteCell;
import src.domain.Cell;
import src.domain.BlackCell;

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

			//Writer writer = new Writer("data/solved"+i+".kak");
			//writer.write(solution);
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
