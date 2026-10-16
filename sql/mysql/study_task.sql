-- 在已经导入芋道 MySQL 基础库后执行；使用当前数据库。
CREATE TABLE IF NOT EXISTS `study_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学习任务编号',
  `user_id` bigint NOT NULL COMMENT '所属管理员用户',
  `title` varchar(100) NOT NULL COMMENT '任务标题',
  `description` varchar(2000) DEFAULT NULL COMMENT '任务说明',
  `planned_at` datetime DEFAULT NULL COMMENT '计划学习时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0 待完成、1 已完成',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_study_task_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习任务';

CREATE TABLE IF NOT EXISTS `study_task_completion_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '学习任务编号，唯一键用于消息消费幂等',
  `consumed_at` datetime NOT NULL COMMENT '消费时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_study_task_completion_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习任务完成消息消费记录';
