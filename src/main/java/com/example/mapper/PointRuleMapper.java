package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dao.PointRuleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分规则Mapper接口
 * @author Nruonan
 */
@Mapper
public interface PointRuleMapper extends BaseMapper<PointRuleDO> {
}