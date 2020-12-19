package src.domain.controllers;

import src.domain.entities.BlackCell;
import src.domain.entities.Board;
import src.domain.entities.Cell;
import src.domain.entities.WhiteCell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kakuro Singleton Reader.
 * Reads a kakuro from a string or file and returns a new board
 *
 * @version 0.1.0 (17/11/2020)
 */

public class Reader {
    // This regex matches a literal "C" followed by a number, OR a literal "F" followed by a number OR
    // a literal "C" followed by a number and then a literal "F" followed by a number.
    private static final Pattern pattern = Pattern.compile("^C(\\d+)$|^F(\\d+)$|^C(\\d+)F(\\d+)$");

    /**
     * Read a board from file
     * @param path Path of the file to read from
     * @return the board represented by the contents of the given file
     * @throws IOException if the file could not be opened
     */
    public static Board fromFile(String path) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(path)));
        return fromString(data);
    }

    /**
     * Read a board from string
     * @param input String representing the board
     * @return the board represented by the contents of the string
     */
    public static Board fromString(String input) {
        return fromString(UUID.randomUUID(), null, input);
    }

    /**
     * Read a board from string, initializing its Id and notations
     * @param boardId UUID representing the board identifier
     * @param notations String representing the notations of the cells
     * @param input String representing the board
     * @return the board represented by the Id, notations and the contents of the string
     */
    public static Board fromString(UUID boardId, String notations, String input) {
        Matcher m = pattern.matcher("");
        String[] rows = input.split("\\n");
        String[] line1 = rows[0].split(",");

        int height = Integer.parseInt(line1[0].trim());
        int width  = Integer.parseInt(line1[1].trim());

        Board board = new Board(boardId, width, height);
        assert(rows.length - 1 == height);

        for(int i = 0; i < height; i++) {
            String[] cols = rows[i + 1].split(",");
            assert(cols.length == width);

            for (int j = 0; j < width; j++) {
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
                    int notation = 0;
                    if (notations != null) notation = Character.getNumericValue(notations.charAt(width*i + j));
                    cell = new WhiteCell(i, j, val, notation);
                }

                board.setCell(cell, i, j);
            }
        }

        return board;
    }
}
