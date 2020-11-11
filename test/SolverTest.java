package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.io.TempDir;
import src.controllers.Reader;
import src.controllers.Solver;
import src.domain.Board;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class SolverTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void beforeEach() {
        assertTrue(Files.isDirectory(this.tempDir));
    }

    @Test
    void SolveSample() throws IOException {
        String inputFile = "data/sample.kak";
        String expectedOutputFile = "data/sample_solution0.kak";
        
        Reader r = new Reader(inputFile);
        Board b = r.read();

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
