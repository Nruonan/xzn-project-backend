package com.example.entity.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 积分规则更新请求DTO
 * @author Nruonan
 */
@Data
public class PointRuleUpdateReqDTO {
    
    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
    private Long id;
    
    /**
     * 动作: post, comment, like
     */
    private String type;
    
    /**
     * 积分: +10 或 -10
     */
    private Integer score;
    
    /**
     * 每日次数限制, 0不限
     */
    @Min(value = 0, message = "每日次数限制不能小于0")
    private Integer dayLimit;
}