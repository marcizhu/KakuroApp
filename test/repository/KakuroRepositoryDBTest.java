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

    @Test
    public void testGetKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.MEDIUM, new Board(1, 1), new User("Kanye")));

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        Kakuro kakuro = repo.getKakuro(expectedKakuro.getName());

        assertTrue(expectedKakuro.equals(kakuro));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }

    @Test
    public void testDeleteKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.deleteKakuro(expectedKakuro);

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
        //verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(KakuroSeializer.class), any()); // FIXME: fix bug with this line
    }

    @Test
    public void testSaveKakuro () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(new ArrayList<>());

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        repo.saveKakuro(expectedKakuro);


        // Assert that the database driver was called properly
        //verify(dbMock).readAll(any(), any(JsonDeserializer.class)); //FIXME: fix test !!!
        //verify(dbMock).writeToFile(any(ArrayList.class), any(String.class), any(KakuroSeializer.class), any()); // FIXME: fix bug with this line
    }

    @Test
    public void testGetAllKakuros () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro1 = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"));
        Kakuro expectedKakuro2 = new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye"));
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
        DB dbMock = mock(DB.class);

        User larry = new User("Larry");
        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), larry);
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye")));

        when(dbMock.readAll(any(), any(JsonDeserializer.class)
        )).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByUser(larry);

        expectedKakuros.remove(1);

        System.out.println(expectedKakuros);
        System.out.println(larryKakuros);

        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }

    @Test
    public void testGetAllKakurosByDifficulty () throws IOException {
        DB dbMock = mock(DB.class);

        Kakuro expectedKakuro = new Kakuro("Test", Difficulty.EXTREME, new Board(1, 1), new User("Larry"));
        ArrayList<Object> expectedKakuros = new ArrayList<>();
        expectedKakuros.add(expectedKakuro);
        expectedKakuros.add(new Kakuro("Test2", Difficulty.EASY, new Board(1, 1), new User("Kanye")));

        when(dbMock.readAll(any(), any(JsonDeserializer.class))).thenReturn(expectedKakuros);

        KakuroRepository repo = new KakuroRepositoryDB(dbMock);
        ArrayList<Kakuro> larryKakuros = repo.getAllKakurosByDifficulty(Difficulty.EXTREME);

        expectedKakuros.remove(1);
        assertTrue(expectedKakuros.equals(larryKakuros));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(any(), any(JsonDeserializer.class));
    }
}
