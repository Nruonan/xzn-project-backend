package com.example.entity.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 用户积分规则完成情况响应DTO
 * @author Nruonan
 */
@Data
public class UserPointRuleStatusRespDTO {
    
    /**
     * 用户当前积分
     */
    private Integer currentPoints;
    
    /**
     * 今日获得积分
     */
    private Integer todayEarnedPoints;
    
    /**
     * 本月获得积分
     */
    private Integer monthEarnedPoints;
    
    /**
     * 累计消费积分
     */
    private Integer totalConsumedPoints;
    
    /**
     * 积分规则列表
     */
    private List<PointRuleWithStatusDTO> pointRules;
    
    /**
     * 积分规则及完成状态
     */
    @Data
    public static class PointRuleWithStatusDTO {
        /**
         * 规则ID
         */
        private Integer id;
        
        /**
         * 动作类型: post, comment, like, sign
         */
        private String type;
        
        /**
         * 动作描述
         */
        private String typeDesc;
        
        /**
         * 可获得积分
         */
        private Integer score;
        
        /**
         * 每日次数限制, 0不限
         */
        private Integer dayLimit;
        
        /**
         * 今日已完成次数
         */
        private Integer todayCompletedCount;
        
        /**
         * 是否还能获得积分
         */
        private Boolean canEarnPoints;
    }
}