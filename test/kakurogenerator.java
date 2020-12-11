import src.domain.algorithms.Generator;
import src.domain.entities.Difficulty;

import java.util.Scanner;

public class kakurogenerator {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        int numRows = inputScanner.nextInt();
        int numCols = inputScanner.nextInt();
        int diff = inputScanner.nextInt();
        int seed = inputScanner.nextInt();

        Difficulty difficulty;

        /**/ if (diff == 1) difficulty = Difficulty.EASY;
        else if (diff == 2) difficulty = Difficulty.MEDIUM;
        else if (diff == 3) difficulty = Difficulty.HARD;
        else difficulty = Difficulty.EXTREME;

        Generator generator = new Generator(numRows, numCols, difficulty, seed);
        generator.generate();

        System.out.println(generator.getGeneratedBoard().toString());
    }
}
