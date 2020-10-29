package src.controllers;

import src.domain.BlackCell;
import src.domain.Board;
import src.domain.Cell;
import src.domain.WhiteCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Reader {
    private String fileName;

    public Reader(String fileName) {
        this.fileName = fileName;
    }

    public Board read() {
        try {
            File f = new File(fileName);
            Scanner s = new Scanner(f);

            String[] line1 = s.nextLine().split(",");
            int rows = Integer.parseInt(line1[0].trim());
            int cols = Integer.parseInt(line1[1].trim());

            Board b = new Board(rows, cols);

            for (int i = 0; i < rows; i++) {
                String[] line = s.nextLine().split(",");

                for (int j = 0; j < cols; j++) {
                    Cell c;
                    String word = line[j].trim();

                    if (word.equals("*")) {
                        c = new BlackCell();
                    }
                    else if (word.equals("?")) {
                        c = new WhiteCell();
                    }
                    else if (Pattern.compile("C([0-9]*)F([0-9]*)").matcher(word).matches()){
                        String[] values = word.split("[CF]"); // FIXME: ion know if this regex works
                        int col = Integer.parseInt(values[1].trim());
                        int row = Integer.parseInt(values[2].trim());
                        c = new BlackCell(col, row);
                    }
                    else if (Pattern.compile("C([0-9]*)").matcher(word).matches()){
                        String[] values = word.split("C");
                        int col = Integer.parseInt(values[1].trim());
                        c = new BlackCell(col, 0);
                    }
                    else if (Pattern.compile("F([0-9]*)").matcher(word).matches()){
                        String[] values = word.split("F");
                        int row = Integer.parseInt(values[1].trim());
                        c = new BlackCell(0, row);
                    }
                    else {
                        int val = Integer.parseInt(word);
                        c = new WhiteCell(val);
                    }

                    b.setCell(c, i, j);
                }
            }
            s.close();

            return b;
        } catch (FileNotFoundException e) {
            System.err.printf("File '%s' not found\n", fileName);
            e.printStackTrace();
        }
        return new Board(); //FIXME: return something else if function crashes
    }

}
