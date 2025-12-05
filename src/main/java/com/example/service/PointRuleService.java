package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.PointRuleDO;
import com.example.entity.dto.req.PointRuleCreateReqDTO;
import com.example.entity.dto.req.PointRuleUpdateReqDTO;
import com.example.entity.dto.resp.PointRuleRespDTO;
import com.example.entity.dto.resp.UserPointRuleStatusRespDTO;

/**
 * 积分规则Service接口
 * @author Nruonan
 */
public interface PointRuleService extends IService<PointRuleDO> {
    
    /**
     * 创建积分规则
     * @param reqDTO 创建请求DTO
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean createRule(PointRuleCreateReqDTO reqDTO, Integer adminId);
    
    /**
     * 更新积分规则
     * @param reqDTO 更新请求DTO
     * @return 是否成功
     */
    boolean updateRule(PointRuleUpdateReqDTO reqDTO);
    
    /**
     * 删除积分规则
     * @param id 规则ID
     * @return 是否成功
     */
    boolean deleteRule(Integer id);
    
    /**
     * 获取积分规则列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param type 类型（可选）
     * @return 分页结果
     */
    Page<PointRuleRespDTO> getRuleList(int pageNum, int pageSize, String type);
    
    /**
     * 获取积分规则详情
     * @param id 规则ID
     * @return 规则详情
     */
    PointRuleRespDTO getRuleDetail(Integer id);
    
    /**
     * 根据类型获取积分规则
     * @param type 类型
     * @return 积分规则
     */
    PointRuleDO getRuleByType(String type);
    
    /**
     * 获取用户积分规则完成情况
     * @param uid 用户ID
     * @return 用户积分规则完成情况
     */
    UserPointRuleStatusRespDTO getUserPointRuleStatus(Integer uid);
}