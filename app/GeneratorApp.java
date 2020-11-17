package app;

//import src.controllers.Generator;

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

        //Board board = Generator.generate(width, height, param);

        //System.out.println(board.toString());
    }
}

