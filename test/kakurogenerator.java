import src.domain.algorithms.Generator;
import src.domain.entities.Difficulty;

public class kakurogenerator {
    public static void main(String[] args) {
        int numRows = Integer.parseInt(args[0]);
        int numCols = Integer.parseInt(args[1]);
        int diff = Integer.parseInt(args[2]);
        int seed = Integer.parseInt(args[3]);

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
