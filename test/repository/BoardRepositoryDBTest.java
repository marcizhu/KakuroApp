package test.repository;

import org.junit.jupiter.api.Test;
import src.domain.entities.Board;
import src.domain.entities.User;
import src.repository.*;
import src.repository.serializers.BoardDeserializer;
import src.repository.serializers.BoardSerializer;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BoardRepositoryDBTest {
    private final DB dbMock = mock(DB.class);

    @Test
    public void testGetBoard() throws IOException {
        Board expectedBoard = new Board(5, 5);
        ArrayList<Object> expectedBoards = new ArrayList<>();
        expectedBoards.add(expectedBoard);
        expectedBoards.add(new Board(10, 10));

        when(dbMock.readAll(any(Board.class.getClass()), any(BoardDeserializer.class))).thenReturn(expectedBoards);

        BoardRepository repo = new BoardRepositoryDB(dbMock);
        Board board = repo.getBoard(expectedBoard.getId());


        assertTrue(expectedBoard.equals(board));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Board.class.getClass()), any(BoardDeserializer.class));
    }

    @Test
    public void testGetAllBoards() throws IOException {
        Board expectedBoard = new Board(5, 5);
        ArrayList<Object> expectedBoards = new ArrayList<>();
        expectedBoards.add(expectedBoard);
        expectedBoards.add(new Board(10, 10));

        when(dbMock.readAll(any(Board.class.getClass()), any(BoardDeserializer.class))).thenReturn(expectedBoards);

        BoardRepository repo = new BoardRepositoryDB(dbMock);
        ArrayList<Board> boards = repo.getAllBoards();

        for(int i = 0; i < boards.size(); i++) assertTrue(boards.get(i).equals(expectedBoards.get(i)));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Board.class.getClass()), any(BoardDeserializer.class));

    }

    @Test
    public void testDeleteBoard() throws IOException { // TODO!!
        DB dbMock = mock(DB.class);
        Board board = new Board(5, 5);

        ArrayList<Object> expectedBoards = new ArrayList<>();
        expectedBoards.add(board);

        when(dbMock.readAll(any(Board.class.getClass()), any(BoardDeserializer.class))).thenReturn(expectedBoards);

        BoardRepository repo = new BoardRepositoryDB(dbMock);
        repo.deleteBoard(board);


        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Board.class.getClass()), any(BoardDeserializer.class));
        verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(BoardSerializer.class), any(Board.class.getClass()));
    }

    @Test
    public void testSaveBoard() throws IOException {
        DB dbMock = mock(DB.class);
        Board board = new Board(5, 5);

        ArrayList<Object> expectedBoards = new ArrayList<>();
        expectedBoards.add(board);

        when(dbMock.readAll(any(Board.class.getClass()), any(BoardDeserializer.class))).thenReturn(new ArrayList<>());

        BoardRepository repo = new BoardRepositoryDB(dbMock);
        repo.saveBoard(board);


        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Board.class.getClass()), any(BoardDeserializer.class));
        verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(BoardSerializer.class), any(Board.class.getClass()));
    }

}
