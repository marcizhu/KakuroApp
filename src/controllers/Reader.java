package src.controllers;

import src.domain.BlackCell;
import src.domain.Board;
import src.domain.Cell;
import src.domain.WhiteCell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
    // This regex matches a literal "C" followed by a number, OR a literal "F" followed by a number OR
    // a literal "C" followed by a number and then a literal "F" followed by a number.
    private static final Pattern pattern = Pattern.compile("^C(\\d+)$|^F(\\d+)$|^C(\\d+)F(\\d+)$");

    public static Board fromFile(String path) throws IOException {
        String data = Files.readString(Path.of(path));
        return fromString(data);
    }

    public static Board fromString(String input) {
        Matcher m = pattern.matcher("");
        String[] rows = input.split("\\n");
        String[] line1 = rows[0].split(",");

        int r = Integer.parseInt(line1[0].trim());
        int c = Integer.parseInt(line1[1].trim());

        Board board = new Board(r, c);
        assert(rows.length - 1 == r);

        for(int i = 0; i < r; i++) {
            String[] cols = rows[i + 1].split(",");
            assert(cols.length == c);

            for (int j = 0; j < c; j++) {
                cols[j] = cols[j].trim();
                m.reset(cols[j]);
                Cell cell;

                /**/ if(cols[j].equals("*")) cell = new BlackCell();
                else if(cols[j].equals("?")) cell = new WhiteCell();
                else if(m.find()) {
                    int col = 0;
                    int row = 0;

                    /**/ if(m.group(1) != null) col = Integer.parseInt(m.group(1));
                    else if(m.group(2) != null) row = Integer.parseInt(m.group(2));
                    else if(m.group(3) != null && m.group(4) != null) {
                        col = Integer.parseInt(m.group(3));
                        row = Integer.parseInt(m.group(4));
                    }

                    cell = new BlackCell(col, row);
                } else {
                    int val = Integer.parseInt(cols[j]);
                    cell = new WhiteCell(val);
                }

                board.setCell(cell, i, j);
            }
        }

        return board;
    }
}
