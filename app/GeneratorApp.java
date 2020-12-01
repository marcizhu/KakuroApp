package app;

import src.domain.algorithms.Generator;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;

public class GeneratorApp {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Kakuro generator tool v1.0.0\n");
            System.out.println("Usage: java GeneratorApp <width> <height> <difficulty>\n");
            System.out.println("Parameters:");
            System.out.println("width       The width of the board to generate");
            System.out.println("height      The height of the board to generate");
            System.out.println("difficulty  Difficulty of the board to generate given as a number");
            System.out.println("            1: Easy difficulty");
            System.out.println("            2: Medium difficulty");
            System.out.println("            3: Hard difficulty");
            System.out.println("            4: Extreme difficulty\n");
            System.out.println("Example: Use \"java GeneratorApp 10 12 2\" to generate a 10x12 kakuro with medium difficulty");
            return;
        }

        int width  = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        int param  = Integer.parseInt(args[2]);

        Difficulty diff = Difficulty.EASY; // Value by default

        switch(param)
        {
            case 1: diff = Difficulty.EASY; break;
            case 2: diff = Difficulty.MEDIUM; break;
            case 3: diff = Difficulty.HARD; break;
            case 4: diff = Difficulty.EXTREME; break;
        }

        Generator generator = new Generator(width, height, diff);
        generator.generate();

        Board board = generator.getGeneratedBoard();
        System.out.println(board.toString());
    }
}
