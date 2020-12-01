package app;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import src.domain.controllers.Reader;
import src.domain.algorithms.Solver;
import src.domain.entities.Board;

public class SolverApp {
    private static final Scanner scanner = new Scanner(new BufferedInputStream(System.in));

    public static String stdin() {
        if (!scanner.hasNextLine()) return "";
        return scanner.useDelimiter("\\A").next();
    }

    public static void main(String[] args) {
        String input = stdin();

        Board board = Reader.fromString(input);
        Solver solver = new Solver(board);
        int nSolutions = solver.solve();

        ArrayList<Board> solutions = solver.getSolutions();

        System.out.println(nSolutions);
        if (nSolutions > 0) System.out.println(solutions.get(0).toString());
    }
}
