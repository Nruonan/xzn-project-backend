package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.PointRuleDO;
import com.example.entity.dao.PointLogDO;
import com.example.entity.dto.req.PointRuleCreateReqDTO;
import com.example.entity.dto.req.PointRuleUpdateReqDTO;
import com.example.entity.dto.resp.PointRuleRespDTO;
import com.example.entity.dto.resp.UserPointRuleStatusRespDTO;
import com.example.mapper.PointRuleMapper;
import com.example.service.PointRuleService;
import com.example.service.PointLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分规则Service实现类
 * @author Nruonan
 */
@Service
public class PointRuleServiceImpl extends ServiceImpl<PointRuleMapper, PointRuleDO> implements PointRuleService {
    
    @Resource
    private PointLogService pointLogService;
    
    @Override
    public boolean createRule(PointRuleCreateReqDTO reqDTO, Integer adminId) {
        // 检查是否已存在相同类型的规则
        QueryWrapper<PointRuleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", reqDTO.getType());
        PointRuleDO existingRule = this.getOne(queryWrapper);
        if (existingRule != null) {
            return false;
        }
        
        // 检查总规则数量是否超过4个
        long ruleCount = this.count();
        if (ruleCount >= 4) {
            return false;
        }
        
        PointRuleDO rule = new PointRuleDO();
        BeanUtils.copyProperties(reqDTO, rule);
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        return this.save(rule);
    }
    
    @Override
    public boolean updateRule(PointRuleUpdateReqDTO reqDTO) {
        PointRuleDO rule = this.getById(reqDTO.getId());
        if (rule == null) {
            return false;
        }
        
        // 如果更新了类型，检查是否已存在相同类型的规则
        if (StringUtils.hasText(reqDTO.getType()) && !reqDTO.getType().equals(rule.getType())) {
            QueryWrapper<PointRuleDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type", reqDTO.getType());
            queryWrapper.ne("id", reqDTO.getId());
            PointRuleDO existingRule = this.getOne(queryWrapper);
            if (existingRule != null) {
                return false;
            }
        }
        
        BeanUtils.copyProperties(reqDTO, rule, "id");
        rule.setUpdateTime(LocalDateTime.now());
        return this.updateById(rule);
    }
    
    @Override
    public boolean deleteRule(Integer id) {
        return this.removeById(id);
    }
    
    @Override
    public Page<PointRuleRespDTO> getRuleList(int pageNum, int pageSize, String type) {
        Page<PointRuleDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointRuleDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(type)) {
            queryWrapper.like("type", type);
        }
        queryWrapper.orderByDesc("create_time");
        Page<PointRuleDO> rulePage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointRuleRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(rulePage, respPage, "records");
        
        java.util.List<PointRuleRespDTO> respList = rulePage.getRecords().stream().map(rule -> {
            PointRuleRespDTO respDTO = new PointRuleRespDTO();
            BeanUtils.copyProperties(rule, respDTO);
            
            // 设置类型描述
            switch (rule.getType()) {
                case "post":
                    respDTO.setTypeDesc("发布文章");
                    break;
                case "comment":
                    respDTO.setTypeDesc("发表评论");
                    break;
                case "like":
                    respDTO.setTypeDesc("点赞");
                    break;
                case "sign":
                    respDTO.setTypeDesc("签到");
                    break;
                default:
                    respDTO.setTypeDesc(rule.getType());
            }
            
            return respDTO;
        }).collect(java.util.stream.Collectors.toList());
        
        respPage.setRecords(respList);
        return respPage;
    }
    
    @Override
    public PointRuleRespDTO getRuleDetail(Integer id) {
        PointRuleDO rule = this.getById(id);
        if (rule == null) {
            return null;
        }
        
        PointRuleRespDTO respDTO = new PointRuleRespDTO();
        BeanUtils.copyProperties(rule, respDTO);
        
        // 设置类型描述
        switch (rule.getType()) {
            case "post":
                respDTO.setTypeDesc("发布文章");
                break;
            case "comment":
                respDTO.setTypeDesc("发表评论");
                break;
            case "like":
                respDTO.setTypeDesc("点赞");
                break;
            case "sign":
                respDTO.setTypeDesc("签到");
                break;
            default:
                respDTO.setTypeDesc(rule.getType());
        }
        
        return respDTO;
    }
    
    @Override
    public PointRuleDO getRuleByType(String type) {
        QueryWrapper<PointRuleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public UserPointRuleStatusRespDTO getUserPointRuleStatus(Integer uid) {
        // 创建响应对象
        UserPointRuleStatusRespDTO response = new UserPointRuleStatusRespDTO();
        
        // 获取用户积分信息
        response.setCurrentPoints(pointLogService.getUserPoint(uid));
        response.setTodayEarnedPoints(pointLogService.getTodayEarnedPoints(uid));
        response.setMonthEarnedPoints(pointLogService.getMonthEarnedPoints(uid));
        response.setTotalConsumedPoints(pointLogService.getTotalConsumedPoints(uid));
        
        // 获取所有积分规则
        List<PointRuleDO> allRules = this.list();
        
        // 转换为带状态的规则列表
        List<UserPointRuleStatusRespDTO.PointRuleWithStatusDTO> rulesWithStatus = allRules.stream().map(rule -> {
            UserPointRuleStatusRespDTO.PointRuleWithStatusDTO ruleWithStatus = 
                new UserPointRuleStatusRespDTO.PointRuleWithStatusDTO();
            
            // 复制基本信息
            ruleWithStatus.setId(rule.getId());
            ruleWithStatus.setType(rule.getType());
            ruleWithStatus.setScore(rule.getScore());
            ruleWithStatus.setDayLimit(rule.getDayLimit());
            
            // 设置类型描述
            switch (rule.getType()) {
                case "post":
                    ruleWithStatus.setTypeDesc("发布文章");
                    break;
                case "comment":
                    ruleWithStatus.setTypeDesc("发表评论");
                    break;
                case "like":
                    ruleWithStatus.setTypeDesc("点赞");
                    break;
                case "collect":
                    ruleWithStatus.setTypeDesc("收藏");
                    break;
                default:
                    ruleWithStatus.setTypeDesc(rule.getType());
            }
            
            // 获取今日完成次数
            int todayCompletedCount = getTodayCompletedCount(uid, rule.getType());
            ruleWithStatus.setTodayCompletedCount(todayCompletedCount);
            
            // 判断是否还能获得积分
            boolean canEarnPoints = rule.getDayLimit() == 0 || todayCompletedCount < rule.getDayLimit();
            ruleWithStatus.setCanEarnPoints(canEarnPoints);
            
            return ruleWithStatus;
        }).collect(Collectors.toList());
        
        response.setPointRules(rulesWithStatus);
        
        return response;
    }
    
    /**
     * 获取用户今日完成某类型任务的次数
     * @param uid 用户ID
     * @param type 任务类型
     * @return 完成次数
     */
    private int getTodayCompletedCount(Integer uid, String type) {
        // 获取今天的开始和结束时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        
        // 查询今日该类型的积分记录
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", type);
        queryWrapper.gt("score", 0); // 只计算获得积分的记录
        queryWrapper.between("create_time", todayStart, todayEnd);
        
        return (int) pointLogService.count(queryWrapper);
    }
}