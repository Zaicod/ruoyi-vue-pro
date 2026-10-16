package cn.iocoder.yudao.module.study.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "study.rabbit", name = "enabled", havingValue = "true")
public class StudyRabbitConfiguration {

    public static final String COMPLETED_QUEUE = "study.task.completed";

    @Bean
    public Queue studyTaskCompletedQueue() {
        return new Queue(COMPLETED_QUEUE, true);
    }
}
