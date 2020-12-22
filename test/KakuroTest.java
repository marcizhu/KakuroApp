package test;

import org.junit.jupiter.api.Test;
import src.domain.controllers.KakuroCtrl;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.GameRepository;
import src.repository.KakuroRepository;
import src.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class KakuroTest {
    @Test
    public void testGetKakuroListByDifficulty() throws Exception {
        KakuroRepository kakuroRepositoryMock = mock(KakuroRepository.class);
        UserRepository userRepositoryMock = mock(UserRepository.class);
        GameRepository gameRepositoryMock = mock(GameRepository.class);

        KakuroCtrl kakuroCtrl = new KakuroCtrl(kakuroRepositoryMock, userRepositoryMock, gameRepositoryMock);

        User user = new User("Alex");
        Kakuro kakuroA = new Kakuro("A", Difficulty.EASY, new Board(5, 5), "seedA", 0);
        Kakuro kakuroB = new Kakuro("B", Difficulty.EASY, new Board(10, 10), "seedB", 1);
        Kakuro kakuroC = new Kakuro("C", Difficulty.EASY, new Board(5, 5), "seedC", 2);
        ArrayList<Kakuro> easyKakuros = new ArrayList<>(Arrays.asList(kakuroA, kakuroB, kakuroC));

        when(kakuroRepositoryMock.getAllKakurosByDifficulty(Difficulty.EASY)).thenReturn(easyKakuros);

        ArrayList<Map<String, Object>> expectedResponse = new ArrayList<>(Arrays.asList(
            new HashMap<>() {{
                put("board", kakuroA.getBoard().toString());
                put("seed", kakuroA.getSeed());
                put("color", kakuroA.getColorCode());
                put("name", kakuroA.getName());
                put("difficulty", kakuroA.getDifficulty().toString());
                put("timesPlayed", 0);
                put("createdBy", "System");
                put("createdAt", kakuroA.getCreatedAt());
                put("bestTime", -1);
                put("state", "neutral");
            }},
            new HashMap<>() {{
                put("board", kakuroB.getBoard().toString());
                put("seed", kakuroB.getSeed());
                put("color", kakuroB.getColorCode());
                put("name", kakuroB.getName());
                put("difficulty", kakuroB.getDifficulty().toString());
                put("timesPlayed", 0);
                put("createdBy", "System");
                put("createdAt", kakuroB.getCreatedAt());
                put("bestTime", -1);
                put("state", "neutral");
            }},
            new HashMap<>() {{
                put("board", kakuroC.getBoard().toString());
                put("seed", kakuroC.getSeed());
                put("color", kakuroC.getColorCode());
                put("name", kakuroC.getName());
                put("difficulty", kakuroC.getDifficulty().toString());
                put("timesPlayed", 0);
                put("createdBy", "System");
                put("createdAt", kakuroC.getCreatedAt());
                put("bestTime", -1);
                put("state", "neutral");
            }}
        ));
        assertEquals(expectedResponse, kakuroCtrl.getKakuroListByDifficulty(Difficulty.EASY, user));
    }
}
