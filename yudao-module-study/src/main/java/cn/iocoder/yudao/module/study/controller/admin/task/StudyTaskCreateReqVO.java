package cn.iocoder.yudao.module.study.controller.admin.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record StudyTaskCreateReqVO(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 2000) String description,
        LocalDateTime plannedAt) {
}
