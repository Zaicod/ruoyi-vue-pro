package cn.iocoder.yudao.module.study.service;

public record StudyTaskChangedEvent(Long taskId, Long userId, boolean completed) {
}
