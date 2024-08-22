package org.bllose.converters;

import com.alibaba.fastjson.JSONObject;
import de.codecentric.boot.admin.server.domain.values.Registration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationConverter {
    public static Registration of(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        return of(JSONObject.parseObject(json));
    }

    public static Registration of(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        Map<String, String> metadata = new HashMap<>();

        if (obj.containsKey("metadata")) {
            JSONObject metadataJson = obj.getJSONObject("metadata");
            if (metadataJson != null) {
                for (String key : metadataJson.keySet()) {
                    metadata.put(key, Objects.toString(metadataJson.get(key)));
                }
            }
        }

        return Registration.builder()
            .name(obj.getString("name"))
            .managementUrl(obj.getString("managementUrl"))
            .healthUrl(obj.getString("healthUrl"))
            .serviceUrl(obj.getString("serviceUrl"))
            .source(obj.getString("source"))
            .metadata(metadata)
            .build();
    }
}
