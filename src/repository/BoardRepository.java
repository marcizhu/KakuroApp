package src.repository;

import src.domain.entities.Board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface BoardRepository {
    ArrayList<Board> getAllBoards() throws IOException;
    Board getBoard (UUID id) throws IOException;
    Board getBoard (Board board) throws IOException;
    void deleteBoard (UUID id) throws IOException;
    void deleteBoard (Board board) throws IOException;
    void saveBoard(Board board) throws IOException;
}
