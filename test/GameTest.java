package test;

import org.junit.jupiter.api.Test;
import src.domain.controllers.GameCtrl;
import src.domain.entities.*;
import src.repository.GameRepository;
import src.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameTest {
    @Test
    public void testGetGameHistory() throws Exception {
        GameRepository gameRepositoryMock = mock(GameRepository.class);
        UserRepository userRepositoryMock = mock(UserRepository.class);

        GameCtrl gameCtrl = new GameCtrl(gameRepositoryMock, userRepositoryMock);

        User user = new User("Alex");
        Kakuro kakuroInProgress = new Kakuro("inProgress", Difficulty.EASY, new Board(5, 5), "seedInProgress", 0);
        Game gameInProgress = new GameInProgress(user, kakuroInProgress);
        Kakuro kakuroFinished = new Kakuro("finished", Difficulty.MEDIUM, new Board(10, 10), "seedFinished", 1);
        Game gameFinished = new GameFinished(new GameInProgress(user, kakuroFinished), false);
        Kakuro kakuroSurrendered = new Kakuro("surrendered", Difficulty.HARD, new Board(10, 10), "seedSurrendered", 1);
        Game gameSurrendered = new GameFinished(new GameInProgress(user, kakuroSurrendered), true);
        ArrayList<Game> userGames = new ArrayList<>(Arrays.asList(gameInProgress, gameFinished, gameSurrendered));

        when(gameRepositoryMock.getAllGamesByUser(user.getName())).thenReturn(userGames);

        ArrayList<Map<String, Object>> expectedResponse = new ArrayList<>(Arrays.asList(
                new HashMap<>() {{
                    put("board", gameInProgress.getKakuro().getBoard());
                    put("color", gameInProgress.getKakuro().getColorCode());
                    put("name", gameInProgress.getKakuro().getName());
                    put("width", gameInProgress.getKakuro().getBoard().getWidth());
                    put("height", gameInProgress.getKakuro().getBoard().getHeight());
                    put("difficulty", gameInProgress.getKakuro().getDifficulty().toString());
                    put("timeSpent", (int)gameInProgress.getTimeSpent());
                    put("lastPlayed", ((GameInProgress)gameInProgress).getLastPlayed());
                    put("state", "unfinished");
                }},
                new HashMap<>() {{
                    put("board", gameFinished.getKakuro().getBoard());
                    put("color", gameFinished.getKakuro().getColorCode());
                    put("name", gameFinished.getKakuro().getName());
                    put("width", gameFinished.getKakuro().getBoard().getWidth());
                    put("height", gameFinished.getKakuro().getBoard().getHeight());
                    put("difficulty", gameFinished.getKakuro().getDifficulty().toString());
                    put("score", ((GameFinished)gameFinished).getScore());
                    put("timeSpent", (int)gameFinished.getTimeSpent());
                    put("lastPlayed", ((GameFinished)gameFinished).getTimeFinished());
                    put("state", "finished");
                }},
                new HashMap<>() {{
                    put("board", gameSurrendered.getKakuro().getBoard());
                    put("color", gameSurrendered.getKakuro().getColorCode());
                    put("name", gameSurrendered.getKakuro().getName());
                    put("width", gameSurrendered.getKakuro().getBoard().getWidth());
                    put("height", gameSurrendered.getKakuro().getBoard().getHeight());
                    put("difficulty", gameSurrendered.getKakuro().getDifficulty().toString());
                    put("score", ((GameFinished)gameSurrendered).getScore());
                    put("timeSpent", (int)gameSurrendered.getTimeSpent());
                    put("lastPlayed", ((GameFinished)gameSurrendered).getTimeFinished());
                    put("state", "finished");
                }}
        ));

        ArrayList<Map<String, Object>> response = gameCtrl.getGameHistory("Alex");

        assertEquals(String.valueOf(response), String.valueOf(expectedResponse));
    }
}
