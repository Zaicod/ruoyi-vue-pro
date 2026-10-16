package cn.iocoder.yudao.module.study.controller.admin.task;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.study.service.StudyTaskService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/study/task")
@PreAuthorize("isAuthenticated()")
public class StudyTaskController {

    @Resource
    private StudyTaskService taskService;

    @PostMapping
    public CommonResult<Long> create(@Valid @RequestBody StudyTaskCreateReqVO request) {
        return success(taskService.create(getLoginUserId(), request));
    }

    @GetMapping
    public CommonResult<List<StudyTaskRespVO>> list() {
        return success(taskService.list(getLoginUserId()).stream().map(StudyTaskRespVO::from).toList());
    }

    @GetMapping("/{id}")
    public CommonResult<StudyTaskRespVO> get(@PathVariable Long id) {
        return success(StudyTaskRespVO.from(taskService.get(id, getLoginUserId())));
    }

    @PutMapping("/{id}/complete")
    public CommonResult<Boolean> complete(@PathVariable Long id) {
        return success(taskService.complete(id, getLoginUserId()));
    }

    @GetMapping("/pending-count")
    public CommonResult<Long> pendingCount() {
        return success(taskService.countPending(getLoginUserId()));
    }
}
