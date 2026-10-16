package cn.iocoder.yudao.module.study.service;

import cn.iocoder.yudao.module.study.controller.admin.task.StudyTaskCreateReqVO;
import cn.iocoder.yudao.module.study.dal.dataobject.StudyTaskDO;
import cn.iocoder.yudao.module.study.dal.mysql.StudyTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyTaskService {

    private static final String PENDING_KEY_PREFIX = "study:pending:";

    @Resource
    private StudyTaskMapper taskMapper;
    @Resource
    private StringRedisTemplate redisTemplate;
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long create(Long userId, StudyTaskCreateReqVO request) {
        StudyTaskDO task = new StudyTaskDO();
        task.setUserId(userId);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setPlannedAt(request.plannedAt());
        task.setStatus(0);
        taskMapper.insert(task);
        eventPublisher.publishEvent(new StudyTaskChangedEvent(task.getId(), userId, false));
        return task.getId();
    }

    public List<StudyTaskDO> list(Long userId) {
        return taskMapper.selectForUser(userId);
    }

    public StudyTaskDO get(Long id, Long userId) {
        StudyTaskDO task = taskMapper.selectForUser(id, userId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "学习任务不存在");
        }
        return task;
    }

    @Transactional
    public boolean complete(Long id, Long userId) {
        if (taskMapper.complete(id, userId, LocalDateTime.now()) == 0) {
            get(id, userId); // 区分不存在和已经完成
            return false;
        }
        eventPublisher.publishEvent(new StudyTaskChangedEvent(id, userId, true));
        return true;
    }

    public long countPending(Long userId) {
        String key = PENDING_KEY_PREFIX + userId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Long.parseLong(cached);
        }
        long count = taskMapper.countPending(userId);
        redisTemplate.opsForValue().set(key, Long.toString(count), Duration.ofMinutes(1));
        return count;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void invalidatePendingCount(StudyTaskChangedEvent event) {
        redisTemplate.delete(PENDING_KEY_PREFIX + event.userId());
    }
}
