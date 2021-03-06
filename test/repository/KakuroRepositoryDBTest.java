package test.repository;

import com.google.gson.JsonDeserializer;
import org.junit.jupiter.api.Test;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.*;
import src.repository.serializers.KakuroDeserializer;
import src.repository.serializers.KakuroSeializer;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class KakuroRepositoryDBTest {
    private final DB dbMock = mock(DB.class);

    @Test
    public void testGetKakuro () throws IOException {
        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"), "", 0);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.MEDIUM, new Board(1, 1), new User("Kanye"), "", 0));

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        Kakuro kakuro = repo.getKakuro(expectedKakuro.getName());

        assertTrue(expectedKakuro.equals(kakuro));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }

    @Test
    public void testDeleteKakuro () throws IOException {
        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"), "", 0);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.deleteKakuro(expectedKakuro);

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
        verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(KakuroSeializer.class), any(Object.class.getClass()));
    }

    @Test
    public void testSaveKakuro () throws IOException {
        Kakuro kakuro = new Kakuro("test", Difficulty.EASY, new Board(5, 5), "", 0);

        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(kakuro);

        when(dbMock.readAll(any(Kakuro.class.getClass()), any(KakuroDeserializer.class))).thenReturn(new ArrayList<>());

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.saveKakuro(kakuro);


        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(Kakuro.class.getClass()), any(KakuroDeserializer.class));
        verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(KakuroSeializer.class), any(Kakuro.class.getClass()));
    }

    @Test
    public void testGetAllKakuros () throws IOException {
        Kakuro expectedKakuro1 = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"), "", 0);
        Kakuro expectedKakuro2 = new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye"), "", 0);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro1);
        expectedKakuros.add(expectedKakuro2);

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> allKakuros = repo.getAllKakuros();

        verify(dbMock).readAll(any(), any(JsonDeserializer.class));

        assertTrue(expectedKakuros.equals(allKakuros));

        // Assert that the database driver was called properly

    }

    @Test
    public void testGetAllKakurosByUser () throws IOException {
        User larry = new User("Larry");
        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), larry, "", 0);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye"), "", 0));

        when(dbMock.readAll(any(), any(JsonDeserializer.class)
        )).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByUser(larry);

        expectedKakuros.remove(1);

        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }

    @Test
    public void testGetAllKakurosByDifficulty () throws IOException {
        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"), "", 0);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye"), "", 0));

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByDifficulty(Difficulty.EXTREME);

        expectedKakuros.remove(1);
        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }
}
