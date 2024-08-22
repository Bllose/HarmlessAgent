package org.bllose.converters;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import de.codecentric.boot.admin.server.domain.values.Endpoint;

import java.util.ArrayList;
import java.util.List;

public class EndpointConverter {
    public static Endpoint of(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return of(JSONObject.parseObject(json));
    }

    public static Endpoint of(JSONObject obj) {
        if (obj == null) {
            return null;
        }

        return Endpoint.of(obj.getString("id"), obj.getString("url"));
    }

    public static List<Endpoint> of(JSONArray array) {
        List<Endpoint> list = new ArrayList<>();

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
