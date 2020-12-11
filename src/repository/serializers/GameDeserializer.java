package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.*;
import src.repository.*;

import javax.management.timer.TimerMBean;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class GameDeserializer implements JsonDeserializer<Game> {

    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();

        DB databseDriver = new DB();
        UserRepository userRepo = new UserRepositoryDB(databseDriver);
        KakuroRepository kakuroRepo = new KakuroRepositoryDB(databseDriver);

        Boolean inProgress = obj.get("inProgress").getAsBoolean();
        UUID id = UUID.fromString(obj.get("id").getAsString());
        Timestamp startTime = Timestamp.valueOf(obj.get("startTime").getAsString());
        float timeSpent = obj.get("timeSpent").getAsFloat();
        String playerName = obj.get("playerName").getAsString();
        UUID kakuroId = UUID.fromString(obj.get("kakuroId").getAsString());

        User player;
        try {
            player = userRepo.getUser(playerName);
        } catch (IOException e) {
            System.err.println("Error getting user " + playerName + " from database");
            player = null;
        }

        Kakuro kakuro;
        try {
            kakuro = kakuroRepo.getKakuro(kakuroId);
        } catch (IOException e) {
            System.err.println("Error getting kakuro " + kakuroId.toString() + " from database");
            kakuro = null;
        }

        if (inProgress) return deserializeGameInProgress(obj, id, startTime, timeSpent, player, kakuro);

        return deserializeGameFinished(obj, id, startTime, timeSpent, player, kakuro);

    }

    private Game deserializeGameInProgress (JsonObject obj, UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro) {
        UUID boardId = UUID.fromString(obj.get("boardId").getAsString());
        Board board = new BoardRepositoryDB(new DB()).getBoard(boardId);
        ArrayList<Movement> movements = new ArrayList();//obj.get("movements").; // TODO: implement !!// FIXME: bug potential ??

        return new GameInProgress(id, startTime, timeSpent, player, kakuro, board, movements);
    }

    private Game deserializeGameFinished (JsonObject obj, UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro) {
        float score = obj.get("score").getAsFloat();
        Timestamp timeFinished = Timestamp.valueOf(obj.get("timeFinished").getAsString());

        return new GameFinished(id, startTime, timeSpent, player, kakuro, score, timeFinished);
    }

}
