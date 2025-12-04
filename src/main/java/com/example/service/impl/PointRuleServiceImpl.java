package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.PointRuleDO;
import com.example.entity.dto.req.PointRuleCreateReqDTO;
import com.example.entity.dto.req.PointRuleUpdateReqDTO;
import com.example.entity.dto.resp.PointRuleRespDTO;
import com.example.mapper.PointRuleMapper;
import com.example.service.PointRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 积分规则Service实现类
 * @author Nruonan
 */
@Service
public class PointRuleServiceImpl extends ServiceImpl<PointRuleMapper, PointRuleDO> implements PointRuleService {
    
    @Override
    public boolean createRule(PointRuleCreateReqDTO reqDTO, Integer adminId) {
        // 检查是否已存在相同类型的规则
        QueryWrapper<PointRuleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", reqDTO.getType());
        PointRuleDO existingRule = this.getOne(queryWrapper);
        if (existingRule != null) {
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
}