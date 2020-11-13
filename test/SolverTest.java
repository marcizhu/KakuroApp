package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import src.domain.Board;
import src.controllers.Reader;
import src.controllers.Solver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

class SolverTest {
    @Test
    void SolveSample() throws IOException {
        String inputFile = "data/sample.kak";
        String expectedOutputFile = "data/sample_solution0.kak";
        
        Board b = Reader.fromFile(inputFile);
        Solver solver = new Solver(b);
        solver.solve();

        int s = solver.getSolutions().size();
        assertEquals(1, s);

        Board solution = solver.getSolutions().get(0);

        File expectedOutput = new File(expectedOutputFile);

        byte[] f1 = Files.readAllBytes(expectedOutput.toPath());
        byte[] f2 = (solution.toString() + "\n").getBytes();

        assertTrue(Arrays.equals(f1, f2));
    }
}
