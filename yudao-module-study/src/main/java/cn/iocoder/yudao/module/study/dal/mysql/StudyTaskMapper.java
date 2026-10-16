package cn.iocoder.yudao.module.study.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.study.dal.dataobject.StudyTaskDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudyTaskMapper extends BaseMapperX<StudyTaskDO> {

    default List<StudyTaskDO> selectForUser(Long userId) {
        return selectList(new LambdaQueryWrapper<StudyTaskDO>()
                .eq(StudyTaskDO::getUserId, userId)
                .orderByDesc(StudyTaskDO::getId));
    }

    default StudyTaskDO selectForUser(Long id, Long userId) {
        return selectOne(new LambdaQueryWrapper<StudyTaskDO>()
                .eq(StudyTaskDO::getId, id)
                .eq(StudyTaskDO::getUserId, userId));
    }

    default long countPending(Long userId) {
        return selectCount(new LambdaQueryWrapper<StudyTaskDO>()
                .eq(StudyTaskDO::getUserId, userId)
                .eq(StudyTaskDO::getStatus, 0));
    }

    default int complete(Long id, Long userId, LocalDateTime completedAt) {
        return update(null, new LambdaUpdateWrapper<StudyTaskDO>()
                .eq(StudyTaskDO::getId, id)
                .eq(StudyTaskDO::getUserId, userId)
                .eq(StudyTaskDO::getStatus, 0)
                .eq(StudyTaskDO::getDeleted, false)
                .set(StudyTaskDO::getStatus, 1)
                .set(StudyTaskDO::getCompletedAt, completedAt)
                .set(StudyTaskDO::getUpdateTime, completedAt));
    }
}
