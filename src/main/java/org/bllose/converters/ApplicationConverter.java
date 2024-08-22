package org.bllose.converters;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import de.codecentric.boot.admin.server.domain.entities.Application;
import de.codecentric.boot.admin.server.domain.values.BuildVersion;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ApplicationConverter {
    public static List<Application> ofList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return of(JSONArray.parseArray(json));
    }

    public static Application of(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return of(JSONObject.parseObject(json));
    }

    public static Application of(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        Application.Builder builder = Application.builder()
            .name(obj.getString("name"))
            .status(obj.getString("status"))
            .instances(InstanceConverter.of(obj.getJSONArray("instances")))
            .statusTimestamp(Instant.parse(obj.getString("statusTimestamp")));

        if (obj.containsKey("buildVersion")) {
            String buildVersion = obj.getString("buildVersion");
            if (buildVersion != null && !buildVersion.trim().isEmpty()) {
                builder.buildVersion(BuildVersion.valueOf(buildVersion));
            }
        }

        return builder.build();
    }

    public static List<Application> of(JSONArray array) {
        List<Application> list = new ArrayList<>();

        if (array == null) {
            return list;
        }

        for (Object obj : array) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof JSONObject) {
                JSONObject json = (JSONObject) obj;
                list.add(of(json));
            }
        }
        return list;
    }

}
