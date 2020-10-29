package src;

import src.controllers.Reader;
import src.controllers.Solver;
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

//		Writer writer = new Writer("data/out-test.kak");
//		writer.write(b);

		// TODO: Debug solver
		Solver solver = new Solver(b);
		solver.solve();

		System.out.print("Solutions found: ");
		System.out.println(solver.getSolutions().size());

		Board solution = solver.getSolutions().get(0);
		printBoard(solution);

		Writer writer = new Writer("data/solved.kak");
		writer.write(solution);
	}

	// For debugging purposes only
	public static void printBoard(Board b) {
		for (int i = 0; i<b.getHeight() ; i++) {
			for(int j = 0; j<b.getWidth() ; j++) {
				Cell c = b.getCell(i, j);
				if (c instanceof BlackCell) {
					System.out.print(String.format("(B %s, %s) ", ((BlackCell) c).getHorizontalSum(), ((BlackCell) c).getVerticalSum()));
				}
				else if (c instanceof WhiteCell){
					System.out.print(String.format("(W %s) ", c.getValue()));
				}
				else {
					System.out.print(String.format("(????) "));
				}
			}
			System.out.println();
		}
	}
}

/*





public int getHorizontalSum() {
		if (horizontalSum == null) return -1;
		return horizontalSum;
}
}

public static final class KakuroConstants {
	public static KakuroConstants instance;

	HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> CASES;
	
	// map[column_or_row_size][column_or_row_sum] returns vector of possible integer values.
	private KakuroConstants() {
		instance = new KakuroConstants();
		instance.createCases(); //TODO: crear tots els casos possibles (omplir els hashMaps).
}
}
*/