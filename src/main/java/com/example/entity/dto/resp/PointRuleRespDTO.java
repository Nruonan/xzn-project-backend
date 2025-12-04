package com.example.entity.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分规则响应DTO
 * @author Nruonan
 */
@Data
public class PointRuleRespDTO {
    
    /**
     * 规则ID
     */
    private Integer id;
    
    /**
     * 动作: post, comment, like
     */
    private String type;
    
    /**
     * 动作描述
     */
    private String typeDesc;
    
    /**
     * 积分: +10 或 -10
     */
    private Integer score;
    
    /**
     * 每日次数限制, 0不限
     */
    private Integer dayLimit;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}