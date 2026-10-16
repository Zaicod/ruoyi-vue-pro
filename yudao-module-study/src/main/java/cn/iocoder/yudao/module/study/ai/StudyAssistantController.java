package cn.iocoder.yudao.module.study.ai;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.study.dal.dataobject.StudyTaskDO;
import cn.iocoder.yudao.module.study.service.StudyTaskService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/study/assistant")
@PreAuthorize("isAuthenticated()")
@ConditionalOnProperty(prefix = "study.ai", name = "enabled", havingValue = "true")
public class StudyAssistantController {

    @Resource
    private ChatClient studyChatClient;
    @Resource
    private StudyTaskService taskService;

    public record AskRequest(@NotBlank @Size(max = 1000) String question) {
    }

    @PostMapping("/ask")
    public CommonResult<String> ask(@Valid @RequestBody AskRequest request) {
        Long userId = getLoginUserId();
        String answer = studyChatClient.prompt()
                .system("你是学习任务助手。回答当前用户任务问题时必须调用工具查询，不要猜测任务。只读，不要声称已修改任务。")
                .user(request.question())
                .tools(new UserTaskTools(taskService, userId))
                .call()
                .content();
        return success(answer);
    }

    public static class UserTaskTools {
        private final StudyTaskService taskService;
        private final Long userId;

        public UserTaskTools(StudyTaskService taskService, Long userId) {
            this.taskService = taskService;
            this.userId = userId;
        }

        @Tool(description = "查询当前登录用户的学习任务以及完成状态")
        public String listMyTasks() {
            return taskService.list(userId).stream()
                    .map(this::format)
                    .reduce("", (left, right) -> left + right + "\n");
        }

        private String format(StudyTaskDO task) {
            return task.getId() + " | " + task.getTitle() + " | "
                    + (task.getStatus() == 1 ? "已完成" : "待完成") + " | " + task.getPlannedAt();
        }
    }
}
