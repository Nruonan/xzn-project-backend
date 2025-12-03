package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.ActivityDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Nruonan
 * @description 针对表【db_activity(活动表)】的数据库操作Mapper
 * @createDate 2025-11-03 21:07:00
 */
@Mapper
public interface ActivityMapper extends BaseMapper<ActivityDO> {

}