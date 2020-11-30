package test;

import org.junit.jupiter.api.Test;
import src.domain.controllers.Generator;
import src.domain.controllers.Solver;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {
    @Test
    public void testDifficultyEasy() {
        Generator generator = new Generator(9, 9, Difficulty.EASY);
        generator.generate();

        Board toSolve = generator.getGeneratedBoard();

        Solver solver = new Solver(toSolve);
        solver.solve();

        assertTrue(solver.getSolutions().size() > 0);
    }

    @Test
    public void testDifficultyMedium() {
        Generator generator = new Generator(9, 9, Difficulty.MEDIUM);
        generator.generate();

        Board toSolve = generator.getGeneratedBoard();

        Solver solver = new Solver(toSolve);
        solver.solve();

        assertTrue(solver.getSolutions().size() > 0);
    }

    @Test
    public void testDifficultyHard() {
        Generator generator = new Generator(9, 9, Difficulty.HARD);
        generator.generate();

        Board toSolve = generator.getGeneratedBoard();

        Solver solver = new Solver(toSolve);
        solver.solve();

        assertTrue(solver.getSolutions().size() > 0);
    }

    @Test
    public void testDifficultyExtreme() {
        Generator generator = new Generator(9, 9, Difficulty.EXTREME);
        generator.generate();

        Board toSolve = generator.getGeneratedBoard();

        Solver solver = new Solver(toSolve);
        solver.solve();

        assertTrue(solver.getSolutions().size() > 0);
    }

    @Test
    public void testSeedBasedGeneration() {
        final long SEED = 876539892l;

        Generator generator = new Generator(9, 9, Difficulty.EASY, SEED);
        generator.generate();

        Generator generator2 = new Generator(9, 9, Difficulty.EASY, SEED);
        generator2.generate();

        assertTrue(generator.getGeneratedBoard().toString().equals(generator2.getGeneratedBoard().toString()));
    }
}
