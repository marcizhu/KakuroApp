package src.repository;

import src.domain.entities.Board;
import src.domain.entities.User;
import src.repository.serializers.BoardDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class BoardRepositoryDB implements BoardRepository {

    private final DB driver;
    private final BoardDeserializer deserializer;

    public BoardRepositoryDB (DB driver) {
        this.deserializer = new BoardDeserializer();
        this.driver = driver;
    }

    @Override
    public ArrayList<Board> getAllBoards() throws IOException {
        return (ArrayList<Board>)(ArrayList<?>) driver.readAll(Board.class, deserializer);
    }

    @Override
    public Board getBoard(UUID id) throws IOException {
        for (Board b : getAllBoards()) if (b.getId().equals(id)) return b;
        return  null;
    }

    @Override
    public Board getBoard(Board board) throws IOException {
        return getBoard(board.getId());
    }

    @Override
    public void deleteBoard(UUID id) throws IOException {
        ArrayList<Board> boardsList = this.getAllBoards();

        for (int i = 0; i<boardsList.size(); i++) {
            if (boardsList.get(i).getId().equals(id)) {
                boardsList.remove(i);
                driver.writeToFile(boardsList, "Board");
                return;
            }
        }

    }

    @Override
    public void deleteBoard(Board board)  throws IOException {
        deleteBoard(board.getId());
    }

    @Override
    public void saveBoard(Board board)  throws IOException {
        ArrayList<Board> boardsList = this.getAllBoards();

        for (int i = 0; i<boardsList.size(); i++) {
            if (boardsList.get(i).getId().equals(board.getId())) {
                boardsList.set(i, board);
                driver.writeToFile(boardsList, "Board");
                return;
            }
        }

        boardsList.add(board);
        driver.writeToFile(boardsList, "Board");
    }
}
