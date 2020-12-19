package src;

import src.domain.algorithms.Generator;
import src.domain.algorithms.Solver;
import src.domain.controllers.Reader;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.presentation.controllers.PresentationCtrl;
import src.presentation.views.KakuroView;
import src.repository.BoardRepository;
import src.repository.BoardRepositoryDB;
import src.repository.DB;

import javax.swing.*;
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
		/*BoardRepository br = new BoardRepositoryDB(new DB());
		Board b1 = Reader.fromFile("data/kakuros/generated/20_20_extreme_unique.kak");
		Board b2 = Reader.fromFile("data/kakuros/generated/30_30_hard_unique_clear.txt");
		Board b3 = Reader.fromFile("data/kakuros/generated/40_40_hard_unique.kak");
		Board b4 = Reader.fromFile("data/kakuros/generated/60_60_easy_unique_clear.txt");
		Board b5 = Reader.fromFile("data/kakuros/generated/150_150_easy_unique.kak");

		br.saveBoard(b1);
		br.saveBoard(b2);
		br.saveBoard(b3);
		br.saveBoard(b4);
		br.saveBoard(b5);*/
		// From here on are testing and debugging purposes

		// Program to visualize a board

		// EASY real 1 sol:
		// 7x7 -8858449551121823241
		// 9x9 -4625736275950176293
		// 12x12 8702915438493554245

		// MEDIUM real 1 sol:
		// 7x7 -8528620179486954430
		// 9x9 2983276301678302414
		// 12x12 -4974726975096362887

		// HARD real 1 sol:
		// 7x7 4968171675600747215
		// 9x9 4360066047828370670
		// 12x12 -3589869560657460313

		// EXTREME real 1 sol:
		// 7x7 3323012031544810870
		// 9x9 6443636693436442265
		// 12x12 2541328425778467360

		// to investigate: extreme 15*15 -5432280936471879465 , has a starting value in a 1 length col

/*
		Generator gen = new Generator(10, 10, Difficulty.HARD,  true);
		gen.generate();

		Solver solver = new Solver(gen.getGeneratedBoard());
		System.out.println("Solutions: "+solver.solve());
		System.out.println("Seed: " + gen.getUsedSeed());

		JFrame frame = new JFrame();
		KakuroView kak = new KakuroView(gen.getGeneratedBoard().toString(), true);
		kak.setSize(800, 800);
		frame.add(kak);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

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


// package src;

// import com.sun.jdi.ArrayReference;
// import src.controllers.Solver;
// import src.controllers.Reader;
// import src.domain.*;

// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.util.ArrayList;
// import java.util.UUID;

// import src.repository.*;

// public class Main {
// 	public static void main(String[] args) throws IOException, NoSuchMethodException{

// 		GameRepository repo = new GameRepositoryDB(new DB());

// 		User xavi = new User("Larry");
// 		Board b = new Board(9, 7);
// 		Kakuro k = new Kakuro(Difficulty.EASY, b, xavi);

// 		Game g = new GameFinished(xavi, k, b);
// 		//Game j = new GameInProgress();

// 		repo.saveGame(g);

// 		//System.out.println(xavi.toString());
// 		/*
// 		Board b = Reader.fromFile("data/kakuros/unsolved/twsvro-sol.kak");
// 		printBoard(b);

// 		Solver solver = new Solver(b);
// 		int s = solver.solve();
// 		System.out.println("\nSolutions found: " + s + "\n");

// 		for (int i = 0; i < s; i++) {
// 			Board solution = solver.getSolutions().get(i);
// 			printBoard(solution);
// 			System.out.println();

// 			try {
// 				PrintWriter serializer = new PrintWriter("data/kakuros/solved/two-sol-" + (i+1) + ".kak");
// 				serializer.println(solution.toString());
// 				serializer.close();
// 			} catch(FileNotFoundException e) {
// 				System.err.println("An error occurred while writing to file");
// 			}
// 		}
// 		*/
// 	}


// 	// For debugging purposes only
// 	public static void printBoard(Board b) {
// 		for (int i = 0; i<b.getHeight() ; i++) {
// 			for(int j = 0; j<b.getWidth() ; j++) {
// 				/**/ if (b.isBlackCell(i, j)) System.out.printf("[F%2d, C%2d] ", b.getHorizontalSum(i, j), b.getVerticalSum(i, j));
// 				else if (b.isWhiteCell(i, j)) System.out.printf("[   %2d   ] ", b.getValue(i, j));
// 				else {
// 					System.out.print("[  ????  ] ");
// 				}
// 			}
// 			System.out.println();
// 		}
// 	}
// }
