package cn.iocoder.yudao.module.study.controller.admin.task;

import cn.iocoder.yudao.module.study.dal.dataobject.StudyTaskDO;

import java.time.LocalDateTime;

public record StudyTaskRespVO(Long id, String title, String description,
                              LocalDateTime plannedAt, Integer status, LocalDateTime completedAt) {
    public static StudyTaskRespVO from(StudyTaskDO task) {
        return new StudyTaskRespVO(task.getId(), task.getTitle(), task.getDescription(),
                task.getPlannedAt(), task.getStatus(), task.getCompletedAt());
    }
}
