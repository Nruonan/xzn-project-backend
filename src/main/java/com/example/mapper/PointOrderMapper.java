package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.PointOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分订单Mapper接口
 * @author Nruonan
 */
@Mapper
public interface PointOrderMapper extends BaseMapper<PointOrderDO> {
}