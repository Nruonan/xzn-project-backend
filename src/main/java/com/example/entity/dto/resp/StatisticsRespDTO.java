package com.example.entity.dto.resp;

import lombok.Data;

/**
 * @author Nruonan
 * @description 管理员统计数据响应DTO
 */
@Data
public class StatisticsRespDTO {
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 主题总数
     */
    private Long topicCount;
    
    /**
     * 评论总数
     */
    private Long commentCount;
    
    /**
     * 工单总数
     */
    private Long productCount;
    
    /**
     * 订单总数
     */
    private Long orderCount;
    
    /**
     * 公告总数
     */
    private Long noticeCount;
    
    /**
     * 活动总数
     */
    private Long activityCount;
    
    /**
     * 今日注册用户数
     */
    private Long todayRegisterCount;

    /**
     * 积分规则总数
     */
    private Integer ruleCount;
}