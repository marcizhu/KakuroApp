package src;

import src.domain.entities.Board;
import src.presentation.controllers.PresentationCtrl;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		// Actually the main function should do this
		javax.swing.SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						PresentationCtrl ctrl = new PresentationCtrl();
						ctrl.initializePresentationCtrl();
					}
				}
		);//*/
		// From here on are testing and debugging purposes

		// Program to generate a board and send it to solver to check the solutions
		// Zero solutions generated with param: 100, 100, HARD, -1003457041328273474, true... pendant to investigate
		// Prepared executions for DEMO: Unique solutions:
		// 10 x 10 EXTREME 8675010256527143921l
		// 11 x 11 EXTREME 373967908810262340l
		// 20 x 20 EXTREME 740358272252751180l
		// 40 x 40 HARD 2425207246797915929l
		// 60 x 60 MEDIUM -880248852092571402l
		// 100 x 100 EASY -1669241887847317670l
		// 150 x 150 EASY 734881683158643490l
		// REAL UNIQUENESS KAKUROS
		// 20 20 EXTREME -6603847958217156480l
		// 30 30 HARD -4159141052615465705l
		// 40 40 MEDIUM -6235487559481337477l
		// 60 60 EASY -4148994457283065265l --- not very good
		/*
		long seed = 8675010256527143921l;
		Generator gen = new Generator(10, 10, Difficulty.EXTREME, seed,  true);
		long t = System.currentTimeMillis();
		gen.generate();
		t = System.currentTimeMillis() - t;
		System.out.println("Uses seed: " + gen.getUsedSeed() + " and it was generated in: " + t + " ms");
		System.out.println(gen.getGeneratedBoard().toString());

		Solver qSolver = new Solver(gen.getGeneratedBoard());
		long t2 = System.currentTimeMillis();
		int sol = qSolver.solve();
		t2 = System.currentTimeMillis() - t2;
		System.out.println("Found " + sol + " solutions, in time: " + t2 + " ms");
		if (sol > 0) {
			System.out.println(qSolver.getSolutions().get(0).toString());
			System.out.println("\n"+gen.getGeneratedBoard().toString()+"\n");
		}
		*/

		// This is to make sure UniqueCrossValues generates the correct unique values, could be useful to rethink Difficulty implementation
		/*
		for (int i = 2; i <= 9; i++) {
			for (int j = i; j <= 9; j++) {
				ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(i, j, Difficulty.EASY); // returns [] of {rowSum, colSum, valueInCommon}
				if (uniqueCrossValues.size() > 0) System.out.println("Unique for sizes: " + i + ", " + j);
				for (int[] u : uniqueCrossValues) {
					System.out.println("i value: " + u[0] + ", j value: " + u[1] + ", unique in common: " + u[2]);
				}
			}
		} */

		// Program to test performance of generating N kakuros;
		/*
		final int N = 40;
		long time = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			Generator gen = new Generator(9, 9, Difficulty.EXTREME);
			gen.generate();
		}
		System.out.println("Generated " + N + " EXTREME difficulty kakuros in: " + (System.currentTimeMillis()-time) + " ms");
		*/


		// Program to read a kakuro from a file and send it to solve, then save it to file.
		/*
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
		}*/
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
