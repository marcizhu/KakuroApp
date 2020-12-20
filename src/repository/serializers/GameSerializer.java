package src.repository.serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import src.domain.entities.Game;
import src.domain.entities.GameFinished;
import src.domain.entities.GameInProgress;
import src.domain.entities.Movement;

import java.lang.reflect.Type;
import java.util.List;

public class GameSerializer implements JsonSerializer<Game> {

    @Override
    public JsonElement serialize(Game game, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", game.getId().toString());
        obj.addProperty("playerName", game.getPlayerName());
        obj.addProperty("kakuroName", game.getKakuro().getName());
        obj.addProperty("timeSpent", game.getTimeSpent());
        obj.addProperty("startTime", game.getStartTime().toString());

        if (game instanceof GameFinished) return serializeGameFinished((GameFinished) game, obj);
        return serializeGameInProgress((GameInProgress) game, obj);
    }

    private JsonObject serializeGameInProgress (GameInProgress g, JsonObject obj) {
        obj.addProperty("inProgress", true);
        obj.addProperty("boardId", g.getBoardId().toString());
        obj.addProperty("lastPlayed", g.getLastPlayed().toString());
        obj.addProperty("hints", g.getNumberOfHints());

        Type listOfTestObject = new TypeToken<List<Movement>>(){}.getType();
        Gson gson = new Gson();
        //String s = gson.toJson(g.getMovements(), listOfTestObject);
        obj.addProperty("movements", gson.toJson(g.getMovements(), listOfTestObject));

        return obj;
    }

    private JsonObject serializeGameFinished (GameFinished g, JsonObject obj) {
        obj.addProperty("inProgress", false);
        obj.addProperty("score", g.getScore());
        obj.addProperty("timeFinished", g.getTimeFinished().toString());
        obj.addProperty("surrendered", g.isSurrendered());

        return obj;
    }
}
