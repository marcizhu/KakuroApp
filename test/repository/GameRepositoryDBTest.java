package test.repository;

import org.junit.jupiter.api.Test;
import src.domain.entities.GameInProgress;
import src.repository.DB;


import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameRepositoryDBTest {
    private final DB dbMock = mock(DB.class);

    @Test
    public void testGetUser() throws IOException {
        /*
        DB dbMock = mock(DB.class);

        Game expectedGame = new GameInProgress( 5, 5);
        ArrayList<Object> expectedBoards = new ArrayList<>();
        expectedBoards.add(expectedBoard);
        expectedBoards.add(new Board(5, 5));

        when(dbMock.readAll(any(Board.class.getClass()), any(BoardDeserializer.class))).thenReturn(expectedBoards);

        BoardRepository repo = new BoardRepositoryDB(dbMock);
        Board board = repo.getBoard(expectedBoard.getId());


        assertTrue(expectedBoard.equals(board));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Board.class.getClass()), any(BoardDeserializer.class));
        */
    }

    @Test
    public void testDeleteUser() throws IOException {
        // TODO
        assert false;
    }

    @Test
    public void testSaveGame() throws IOException {
        // TODO
        assert false;
    }

    @Test
    public void testUpdateGame() throws IOException {
        // TODO
        assert false;
    }
}


