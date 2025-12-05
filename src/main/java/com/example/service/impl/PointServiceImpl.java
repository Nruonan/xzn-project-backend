package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.dao.AccountDetailsDO;
import com.example.entity.dao.PointLogDO;
import com.example.entity.dao.PointRuleDO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.PointLogMapper;
import com.example.service.PointService;
import com.example.service.PointRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分服务实现类
 * @author Nruonan
 */
@Slf4j
@Service
public class PointServiceImpl implements PointService {
    
    @Autowired
    private PointLogMapper pointLogMapper;
    
    @Autowired
    private AccountDetailsMapper accountDetailsMapper;
    
    @Autowired
    private PointRuleService pointRuleService;
    
    @Override
    public boolean checkDailyLimit(Integer uid, String type) {
        // 获取积分规则
        PointRuleDO rule = pointRuleService.getRuleByType(type);
        if (rule == null || rule.getDayLimit() == 0) {
            // 如果没有规则或者不限制次数，返回true
            return true;
        }
        
        // 查询今日该操作类型的积分记录数量
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                   .eq("type", type)
                   .ge("create_time", LocalDate.now().atStartOfDay())
                   .lt("create_time", LocalDate.now().plusDays(1).atStartOfDay());
        
        long count = pointLogMapper.selectCount(queryWrapper);
        return count < rule.getDayLimit();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoint(Integer uid, String type, String refId, String remark) {
        // 获取积分规则
        PointRuleDO rule = pointRuleService.getRuleByType(type);
        if (rule == null) {
            log.warn("未找到操作类型 {} 的积分规则", type);
            return false;
        }
        
        // 检查每日限制
        if (!checkDailyLimit(uid, type)) {
            log.warn("用户 {} 今日操作类型 {} 已达到每日限制", uid, type);
            return false;
        }
        
        // 添加积分记录
        PointLogDO pointLog = new PointLogDO();
        pointLog.setUid(uid);
        pointLog.setType(type);
        pointLog.setScore(rule.getScore());
        pointLog.setRefId(refId);
        pointLog.setRemark(remark);
        pointLog.setCreateTime(LocalDateTime.now());
        pointLog.setUpdateTime(LocalDateTime.now());
        
        int insertResult = pointLogMapper.insert(pointLog);
        if (insertResult <= 0) {
            log.error("添加积分记录失败");
            return false;
        }
        
        // 更新用户总积分
        AccountDetailsDO user = accountDetailsMapper.selectById(uid);
        if (user == null) {
            log.error("用户不存在: {}", uid);
            return false;
        }
        
        // 更新用户积分
        user.setScore(user.getScore() + rule.getScore());
        accountDetailsMapper.updateById(user);
        
        return true;
    }
    
    @Override
    public List<PointLogDO> getTodayPointLogs(Integer uid, String type) {
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                   .eq("type", type)
                   .ge("create_time", LocalDate.now().atStartOfDay())
                   .lt("create_time", LocalDate.now().plusDays(1).atStartOfDay())
                   .orderByDesc("create_time");
        
        return pointLogMapper.selectList(queryWrapper);
    }
    
    @Override
    public PointRuleDO getPointRule(String type) {
        return pointRuleService.getRuleByType(type);
    }
}