package com.example.entity.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分订单响应DTO
 * @author Nruonan
 */
@Data
public class PointOrderRespDTO {
    
    /**
     * 订单ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private Integer uid;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 收货地址
     */
    private String address;
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 实付积分
     */
    private Integer payScore;
    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 状态: 0待处理, 1已完成, 2已取消
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}