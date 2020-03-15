package messenger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonConverter {
    private ObjectMapper objectMapper = new ObjectMapper();
    public String mapToJson(Map<String, Object> map) {
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }
}
