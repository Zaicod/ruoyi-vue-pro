package cn.iocoder.yudao.module.study.mq;

import cn.iocoder.yudao.module.study.dal.mysql.StudyTaskCompletionLogMapper;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "study.rabbit", name = "enabled", havingValue = "true")
public class StudyTaskCompletionConsumer {

    @Resource
    private StudyTaskCompletionLogMapper completionLogMapper;

    @RabbitListener(queues = StudyRabbitConfiguration.COMPLETED_QUEUE)
    public void consume(String taskId) {
        completionLogMapper.insertIgnore(Long.valueOf(taskId));
    }
}
