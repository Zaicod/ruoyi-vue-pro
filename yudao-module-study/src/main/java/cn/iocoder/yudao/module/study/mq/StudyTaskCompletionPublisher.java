package cn.iocoder.yudao.module.study.mq;

import cn.iocoder.yudao.module.study.service.StudyTaskChangedEvent;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(prefix = "study.rabbit", name = "enabled", havingValue = "true")
public class StudyTaskCompletionPublisher {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(StudyTaskChangedEvent event) {
        if (event.completed()) {
            rabbitTemplate.convertAndSend(StudyRabbitConfiguration.COMPLETED_QUEUE, event.taskId().toString());
        }
    }
}
