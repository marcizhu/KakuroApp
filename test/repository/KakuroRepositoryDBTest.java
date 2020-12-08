package test.repository;

import org.junit.jupiter.api.Test;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class KakuroRepositoryDBTest {

    @Test
    public void testGetKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro(Difficulty.EXTREME, new Board(), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro(Difficulty.MEDIUM, new Board(), new User("Kanye")));

        when(dbMock.readAll(Kakuro.class)).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        Kakuro kakuro = repo.getKakuro(expectedKakuro.getId());

        assertTrue(expectedKakuro.equals(kakuro));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
    }

    @Test
    public void testDeleteKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro(Difficulty.EXTREME, new Board(), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);

        when(dbMock.readAll(Kakuro.class)).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.deleteKakuro(expectedKakuro);

        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
        verify(dbMock).writeToFile(new ArrayList<>(), "kakuro");
    }

    @Test
    public void testSaveKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro(Difficulty.EXTREME, new Board(), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);

        when(dbMock.readAll(Kakuro.class)).thenReturn(new ArrayList<>());

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.saveKakuro(expectedKakuro);


        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
        verify(dbMock).writeToFile(expectedKakuros, "kakuro");
    }

    @Test
    public void testGetAllKakuros () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro1 = new Kakuro(Difficulty.EXTREME, new Board(), new User("Larry"));
        Kakuro expectedKakuro2 = new Kakuro(Difficulty.EASY, new Board(), new User("Kanye"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro1);
        expectedKakuros.add(expectedKakuro2);

        when(dbMock.readAll(Kakuro.class)).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> allKakuros = repo.getAllKakuros();

        assertTrue(expectedKakuros.equals(allKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
    }

    @Test
    public void testGetAllKakurosByUser () throws IOException {
        DB dbMock = mock(DB.class);

        User larry = new User("Larry");
        Kakuro expectedKakuro = new Kakuro(Difficulty.EXTREME, new Board(), larry);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro(Difficulty.EASY, new Board(), new User("Kanye")));

        when(dbMock.readAll(Kakuro.class)).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByUser(larry);

        expectedKakuros.remove(1);
        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
    }

    @Test
    public void testGetAllKakurosByDifficulty () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro(Difficulty.EXTREME, new Board(), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro(Difficulty.EASY, new Board(), new User("Kanye")));

        when(dbMock.readAll(Kakuro.class)).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByDifficulty(Difficulty.EXTREME);

        expectedKakuros.remove(1);
        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(Kakuro.class);
    }
}
