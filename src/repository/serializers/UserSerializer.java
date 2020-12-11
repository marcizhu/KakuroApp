package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.User;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", user.getName());
        obj.addProperty("score", user.getScore());
        return obj;
    }
}
