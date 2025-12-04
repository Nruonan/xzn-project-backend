package com.example.entity.dto.resp;

import lombok.Data;

/**
 * 积分统计响应DTO
 */
@Data
public class PointStatisticsRespDTO {
    
    /**
     * 当前积分
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
}