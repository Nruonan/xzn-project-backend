package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.PointLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分日志Mapper接口
 * @author Nruonan
 */
@Mapper
public interface PointLogMapper extends BaseMapper<PointLogDO> {
}