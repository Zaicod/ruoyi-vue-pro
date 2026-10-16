package cn.iocoder.yudao.module.study.service;

import cn.iocoder.yudao.module.study.dal.dataobject.StudyTaskDO;
import cn.iocoder.yudao.module.study.dal.mysql.StudyTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StudyTaskServiceTest {

    private final StudyTaskMapper mapper = mock(StudyTaskMapper.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final StudyTaskService service = new StudyTaskService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "taskMapper", mapper);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
        ReflectionTestUtils.setField(service, "redisTemplate", mock(StringRedisTemplate.class));
    }

    @Test
    void userCannotReadAnotherUsersTask() {
        when(mapper.selectForUser(7L, 22L)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> service.get(7L, 22L));
        verify(mapper).selectForUser(7L, 22L);
    }

    @Test
    void repeatedCompletionDoesNotPublishAnotherMessage() {
        StudyTaskDO alreadyCompleted = new StudyTaskDO();
        alreadyCompleted.setId(7L);
        alreadyCompleted.setUserId(22L);
        alreadyCompleted.setStatus(1);
        when(mapper.complete(eq(7L), eq(22L), any(LocalDateTime.class))).thenReturn(0);
        when(mapper.selectForUser(7L, 22L)).thenReturn(alreadyCompleted);

        assertFalse(service.complete(7L, 22L));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void successfulCompletionPublishesExactlyOneEvent() {
        when(mapper.complete(eq(7L), eq(22L), any(LocalDateTime.class))).thenReturn(1);

        assertTrue(service.complete(7L, 22L));
        verify(eventPublisher).publishEvent(new StudyTaskChangedEvent(7L, 22L, true));
        verifyNoMoreInteractions(eventPublisher);
    }
}
