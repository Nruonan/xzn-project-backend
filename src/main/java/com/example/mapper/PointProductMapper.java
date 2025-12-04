package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.PointProductDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分商品Mapper接口
 * @author Nruonan
 */
@Mapper
public interface PointProductMapper extends BaseMapper<PointProductDO> {
}