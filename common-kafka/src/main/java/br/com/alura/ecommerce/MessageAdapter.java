package br.com.alura.ecommerce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("type", message.getPayload().getClass().getName());
        object.add("payload", jsonSerializationContext.serialize(message.getPayload()));
        object.add("correlationId", jsonSerializationContext.serialize(message.getId()));
        return object;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            var object = jsonElement.getAsJsonObject();
            String payloadType = object.get("type").getAsString();
            CorrelationId correlationId = (CorrelationId) jsonDeserializationContext.deserialize(object.get("correlationId"), CorrelationId.class);
            Object payload = jsonDeserializationContext.deserialize(object.get("payload"), Class.forName(payloadType));
            return new Message(correlationId, payload);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
