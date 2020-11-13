package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import src.domain.Board;
import src.controllers.Reader;
import src.controllers.Solver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

public class SolverTest {

    @ParameterizedTest
    @MethodSource("testArguments")
    public void testSolveSample(String inputFile, String expectedOutputFile) throws IOException {
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

    private static Stream<Arguments> testArguments() {
        return Stream.of(
            // Arguments.of("data/unsolved/cpu_burner.kak", "data/solved/cpu_burner.kak"),
            Arguments.of("data/unsolved/evil.kak",       "data/solved/evil.kak"      ),
            Arguments.of("data/unsolved/one-sol.kak",    "data/solved/one-sol.kak"   ),
            Arguments.of("data/unsolved/sample.kak",     "data/solved/sample.kak"    ),
            Arguments.of("data/unsolved/sample2.kak",    "data/solved/sample2.kak"   )
        );
    }
}

