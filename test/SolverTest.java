package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import src.domain.Board;
import src.controllers.Reader;
import src.controllers.Solver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class SolverTest {

    @ParameterizedTest
    @MethodSource("testArguments")
    public void testSolveSample(String inputFile, String[] expectedOutputFiles) throws IOException {
        Board b = Reader.fromFile(inputFile);
        Solver solver = new Solver(b);
        int numSolutions = solver.solve();
        int expectedNumSolutions = expectedOutputFiles.length;
        assertEquals(expectedNumSolutions, numSolutions);

        for (int i = 0; i < expectedNumSolutions; i++) {
            Board solution = solver.getSolutions().get(i);

            File expectedOutput = new File(expectedOutputFiles[i]);

            String s1 = Files.readString(expectedOutput.toPath()).replace("\r","");
            String s2 = solution.toString() + "\n";

            assertEquals(s1, s2);
        }
    }

    private static Stream<Arguments> testArguments() {
        return Stream.of(
            // Arguments.of("data/kakuros/unsolved/cpu_burner.kak", new String[]{"data/kakuros/solved/cpu_burner.kak"}),
            Arguments.of("data/kakuros/unsolved/evil.kak",       new String[]{"data/kakuros/solved/evil.kak"}),
            Arguments.of("data/kakuros/unsolved/one-sol.kak",    new String[]{"data/kakuros/solved/one-sol.kak"}),
            Arguments.of("data/kakuros/unsolved/sample.kak",     new String[]{"data/kakuros/solved/sample.kak"}),
            Arguments.of("data/kakuros/unsolved/sample2.kak",    new String[]{"data/kakuros/solved/sample2.kak"}),
            Arguments.of("data/kakuros/unsolved/no-sol.kak",     new String[]{}),
            Arguments.of("data/kakuros/unsolved/two-sol.kak",    new String[]{"data/kakuros/solved/two-sol-1.kak", "data/kakuros/solved/two-sol-2.kak"}),
            Arguments.of("data/kakuros/unsolved/jutge.kak",      new String[]{"data/kakuros/solved/jutge.kak"})
        );
    }
}

