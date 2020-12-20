package src.repository.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import src.domain.entities.*;
import src.repository.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameDeserializer implements JsonDeserializer<Game> {

    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();

        DB databseDriver = new DB();
        UserRepository userRepo = new UserRepositoryDB(databseDriver);
        KakuroRepository kakuroRepo = new KakuroRepositoryDB(databseDriver);

        boolean inProgress = obj.get("inProgress").getAsBoolean();
        UUID id = UUID.fromString(obj.get("id").getAsString());
        Timestamp startTime = Timestamp.valueOf(obj.get("startTime").getAsString());
        float timeSpent = obj.get("timeSpent").getAsFloat();
        String playerName = obj.get("playerName").getAsString();
        String kakuroName = obj.get("kakuroName").getAsString();

        User player;
        try {
            player = userRepo.getUser(playerName);
        } catch (IOException e) {
            System.err.println("Error getting user " + playerName + " from database");
            player = null;
        }

        Kakuro kakuro;
        try {
            kakuro = kakuroRepo.getKakuro(kakuroName);
        } catch (IOException e) {
            System.err.println("Error getting kakuro " + kakuroName + " from database");
            kakuro = null;
        }

        if (inProgress) return deserializeGameInProgress(obj, id, startTime, timeSpent, player, kakuro);

        return deserializeGameFinished(obj, id, startTime, timeSpent, player, kakuro);

    }

    private Game deserializeGameInProgress (JsonObject obj, UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro) {
        UUID boardId = UUID.fromString(obj.get("boardId").getAsString());
        Timestamp lastPlayed = Timestamp.valueOf(obj.get("lastPlayed").getAsString());
        int numberOfHints = obj.get("hints").getAsInt();

        Board board = null;
        try {
            board =  new BoardRepositoryDB(new DB()).getBoard(boardId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type listOfTestObject = new TypeToken<List<Movement>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Movement> movements = gson.fromJson(obj.get("movements").getAsString(),  listOfTestObject);

        return new GameInProgress(id, startTime, timeSpent, player, kakuro, board, movements, lastPlayed, numberOfHints);
    }

    private Game deserializeGameFinished (JsonObject obj, UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro) {
        float score = obj.get("score").getAsFloat();
        boolean surrendered = obj.get("surrendered").getAsBoolean();
        Timestamp timeFinished = Timestamp.valueOf(obj.get("timeFinished").getAsString());

        return new GameFinished(id, startTime, timeSpent, player, kakuro, score, timeFinished, surrendered);
    }

}
