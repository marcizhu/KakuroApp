package app;

import src.controllers.Generator;
import src.domain.Board;
import src.domain.Difficulty;

public class GeneratorApp {
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Kakuro generator tool v1.0.0\n");
            System.out.println("Usage: java GeneratorApp <width> <height> <param>");
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
