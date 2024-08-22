package org.bllose.converters;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.values.Endpoints;
import de.codecentric.boot.admin.server.domain.values.Info;
import de.codecentric.boot.admin.server.domain.values.InstanceId;

import java.util.ArrayList;
import java.util.List;

public class InstanceConverter {
    public static Instance of(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return of(JSONObject.parseObject(json));
    }

    public static Instance of(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        Instance instance = Instance.create(InstanceId.of(obj.getString("id")));

        if (obj.containsKey("registration")) {
            instance = instance.register(RegistrationConverter.of(obj.getJSONObject("registration")));
        }

        if (obj.containsKey("statusInfo")) {
            instance = instance.withStatusInfo(StatusInfoConverter.of(obj.getJSONObject("statusInfo")));
        }

        if (obj.containsKey("info")) {
            instance = instance.withInfo(Info.from(obj.getJSONObject("info").getInnerMap()));
        }

        if (obj.containsKey("endpoints")) {
            instance = instance.withEndpoints(Endpoints.of(EndpointConverter.of(obj.getJSONArray("endpoints"))));
        }

        return instance;
    }

    public static List<Instance> of(JSONArray array) {
        List<Instance> list = new ArrayList<>();

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
