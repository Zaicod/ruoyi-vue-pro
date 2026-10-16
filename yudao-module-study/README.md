# 学习任务与 AI 助手

这是在芋道 `master-jdk17` 上新增的独立业务模块。保留原有的系统登录和基础设施模块，通过一条小业务链路练习 Spring Boot、Spring MVC、MyBatis-Plus/MyBatis、MySQL、Redis、RabbitMQ、Jetty、分布式幂等和 Spring AI。

## 从哪里开始

1. 先按项目根目录 `README.md` 启动原有 MySQL、Redis 并导入芋道基础 SQL。随后在相同数据库执行 [`sql/mysql/study_task.sql`](../sql/mysql/study_task.sql)。
2. 用 JDK 17 或更高版本构建并启动 `yudao-server`。本分支的 Web starter 已改为嵌入式 Jetty，Spring MVC 的 Controller 保持不变。
3. 登录原有后台，带 `Authorization: Bearer <登录令牌>` 请求 `http://localhost:48080/admin-api/study/task`。创建任务示例：`POST` 请求体 `{"title":"复习 Spring MVC","plannedAt":"2026-10-20T20:00:00"}`。
4. `GET /admin-api/study/task` 看自己的任务，`GET /admin-api/study/task/pending-count` 看缓存的待办数，`PUT /admin-api/study/task/{id}/complete` 完成任务。其他用户的任务无法通过 ID 查询或完成。

所有接口默认需要登录。RabbitMQ 和 AI 助手默认关闭，任务基础链路只需要 MySQL 与 Redis。

## 逐层学习

| 步骤 | 观察代码 | 练习重点 |
| --- | --- | --- |
| 1 | `StudyTaskController` → `StudyTaskService` → `StudyTaskMapper` | Spring MVC 参数校验、分层、MyBatis SQL、事务、MySQL 索引 |
| 2 | `countPending` 与 `invalidatePendingCount` | Redis 缓存、TTL、提交后失效、缓存一致性 |
| 3 | 开启 RabbitMQ 后看 `mq` 包与 `study_task_completion_log` | 事务提交后发消息、竞争消费、重复投递与唯一键幂等 |
| 4 | 同时运行两个 `yudao-server` 实例 | 比较原子 SQL 更新与 Redis 分布式锁的用途，观察消息只被一个消费者处理 |
| 5 | 开启 AI 后看 `StudyAssistantController` | Spring AI 工具调用、用户隔离、模型与业务方法分工 |

### 启用 RabbitMQ

先准备 RabbitMQ，并核对 `yudao-server/src/main/resources/application-local.yaml` 中的连接配置。设置环境变量 `STUDY_RABBIT_ENABLED=true` 后启动。完成任务时发送 `study.task.completed` 消息；消费者把任务 ID 插入消费记录表，`task_id` 唯一键让重复消息只产生一条记录。

这一版在事务提交后直接发消息。若进程在数据库提交后、发消息前退出，消息仍可能丢失。后续可自己实现事务 Outbox 表、重试发布、死信队列和监控，比较可靠性差异。

### 启用 AI 助手

配置 `STUDY_AI_ENABLED=true`、`STUDY_AI_API_KEY` 和 `STUDY_AI_MODEL`，需要兼容 OpenAI Chat API 的服务时可另设 `STUDY_AI_BASE_URL`。密钥只放环境变量或未跟踪的本地配置。启用后调用 `POST /admin-api/study/assistant/ask`，例如 `{"question":"我有哪些未完成任务？"}`。

工具对象在每次请求时绑定当前登录用户 ID，只提供查询功能。模型负责选择查询工具和组织回答，MySQL 仍是任务状态的来源。

## 当前范围

本模块目前没有前端页面；先用接口工具练习业务闭环。待办数使用 1 分钟缓存，存在并发读写下短暂旧值的可能。需要严格一致的场景可改为直接查库或增加版本控制。Jetty 替换发生在仓库的共用 Web starter，因此其他模块一起启动时也使用 Jetty。
