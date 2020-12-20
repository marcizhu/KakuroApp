package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.Game;
import src.domain.entities.GameFinished;
import src.domain.entities.GameInProgress;

import java.lang.reflect.Type;

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
        obj.addProperty("movements", g.getMovements().toString()); // FIXME: this dont work lmao
        obj.addProperty("lastPlayed", g.getLastPlayed().toString());
        obj.addProperty("hints", g.getNumberOfHints());

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
