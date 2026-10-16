package cn.iocoder.yudao.module.study.dal.mysql;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudyTaskCompletionLogMapper {

    // task_id 唯一索引让重复投递只记录一次。
    @Insert("INSERT IGNORE INTO study_task_completion_log (task_id, consumed_at) VALUES (#{taskId}, NOW())")
    int insertIgnore(@Param("taskId") Long taskId);
}
