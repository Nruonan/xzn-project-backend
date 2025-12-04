package com.example.entity.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分日志响应DTO
 * @author Nruonan
 */
@Data
public class PointLogRespDTO {
    
    /**
     * 日志ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer uid;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 类型: exchange(兑换), post(发帖), sign(签到)
     */
    private String type;
    
    /**
     * 类型描述
     */
    private String typeDesc;
    
    /**
     * 变动积分: +10 或 -100
     */
    private Integer score;
    
    /**
     * 关联业务ID (订单ID 或 帖子ID)
     */
    private String refId;
    
    /**
     * 备注 (如: 兑换XX商品)
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}