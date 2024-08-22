package org.bllose.discovery;

import de.codecentric.boot.admin.server.domain.entities.Application;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import org.bllose.content.Constants;
import org.bllose.converters.ApplicationConverter;
import org.bllose.tools.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerDiscover {
    private static final Logger log = LoggerFactory.getLogger(ServerDiscover.class);

    private static Map<String, Application> serverMap = new ConcurrentHashMap<>();

    public static String fetchByServerName(String name) {
        fetchServers();
        if(serverMap.keySet().contains(name)){
            List<Instance> instanceList = serverMap.get(name).getInstances();
            if(!CollectionUtils.isEmpty(instanceList)) {
                return instanceList.get(0).getRegistration().getServiceUrl();
            }
        }
        log.warn("Server:{} dose not exists!", name);
        return "";
    }

    public static synchronized void fetchServers() {

        if(CollectionUtils.isEmpty(serverMap)) {
            String env = System.getenv(Constants.DISCOVERY_ENV);
            if (env == null || env.isEmpty()) {
                env = "test1";
            }

            log.info("{}: {}", Constants.DISCOVERY_ENV, env);

            String appList = HttpUtil.discovery(env);

            List<Application> servers = ApplicationConverter.ofList(appList);

            Map<String, Application> innerServerMap = new ConcurrentHashMap<>();

            for (Application server : servers) {
                if (!"UP".equals(server.getStatus())) {
                    continue;
                }

                List<Instance> instances = server.getInstances().stream()
                        .filter(instance -> instance.getStatusInfo() != null)
                        .filter(instance -> "UP".equals(instance.getStatusInfo().getStatus()))
                        .collect(Collectors.toList());

                if (instances.isEmpty()) {
                    continue;
                }

                innerServerMap.put(server.getName(), server);
            }

            serverMap = innerServerMap;
        }
    }
}
