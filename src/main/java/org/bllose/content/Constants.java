package org.bllose.content;

public class Constants {

    public static final String MY_AGENT = "MyAgent:";

    public static final String DISCOVERY_ENV = "agent.discovery.env";

    public static final String TARGET_PATH_ROOT = "agent.path.root";
    public static final String XXLJOB_ENABLED = "agent.xxljob.enabled";

    public static final String RABBITMQ_ENABLED = "agent.rabbitmq.enabled";

    public static final String KAFKAMQ_ENABLED = "agent.kafkamq.enabled";

    public static final String SCHEDULED_ENABLED = "agent.scheduled.enabled";

    public static final String LIST_OF_SERVERS_CONFIG_PREFIX = "agent.discovery.listOfServers.";

    public static final String ANNOTATION_SPRING_BOOT_APP = "org.springframework.boot.autoconfigure.SpringBootApplication";

    public static final String ANNOTATION_RABBIT_LISTENER = "org.springframework.amqp.rabbit.annotation.RabbitListener";

    public static final String ANNOTATION_KAFKA_LISTENER = "org.springframework.kafka.annotation.KafkaListener";

    public static final String ANNOTATION_XXLJOB = "com.xxl.job.core.handler.annotation.XxlJob";

    public static final String ANNOTATION_FEIGN = "org.springframework.cloud.openfeign.FeignClient";

    public static final String CLASS_XXLJOB_EXECUTOR = "com.xxl.job.core.executor.impl.XxlJobSpringExecutor";

    public static final String XXLJOB_CODE_WRAPPER = CLASS_XXLJOB_EXECUTOR + " var0 = new " + CLASS_XXLJOB_EXECUTOR + "();" + "\n"
                                                        + "return var0;";

    public static final String METHOD_RENAME_SUFFIX = "$myagent";

    public static final String COMMA = ",";

}
