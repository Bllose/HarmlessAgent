package org.bllose.converters;

import com.alibaba.fastjson.JSONObject;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;

public class StatusInfoConverter {
    public static StatusInfo of(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return of(JSONObject.parseObject(json));
    }

    public static StatusInfo of(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        return StatusInfo.valueOf(obj.getString("status"),
                obj.getJSONObject("details").getInnerMap());
    }
}
