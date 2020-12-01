import src.domain.controllers.Reader;
import src.domain.algorithms.Solver;
import src.domain.entities.Board;

import java.util.Scanner;

public class kakurosolver {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in).useDelimiter("\\A");
        String input = inputScanner.hasNext() ? inputScanner.next() : "";

        Board b = Reader.fromString(input);

        Solver solver = new Solver(b);
        solver.solve();

        int s = solver.getSolutions().size();
        System.out.println(s);
        if (s > 0) {
            Board solution = solver.getSolutions().get(0);
            System.out.println(solution);
        }
    }
}
