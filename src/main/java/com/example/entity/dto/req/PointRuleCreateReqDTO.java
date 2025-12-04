package com.example.entity.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 积分规则创建请求DTO
 * @author Nruonan
 */
@Data
public class PointRuleCreateReqDTO {
    
    /**
     * 动作: post, comment, like
     */
    @NotBlank(message = "动作类型不能为空")
    private String type;
    
    /**
     * 积分: +10 或 -10
     */
    @NotNull(message = "积分值不能为空")
    private Integer score;
    
    /**
     * 每日次数限制, 0不限
     */
    @Min(value = 0, message = "每日次数限制不能小于0")
    private Integer dayLimit;
}