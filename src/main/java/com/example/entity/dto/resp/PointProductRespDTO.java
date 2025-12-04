package com.example.entity.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分商品响应DTO
 * @author Nruonan
 */
@Data
public class PointProductRespDTO {
    
    /**
     * 商品ID
     */
    private Integer id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 兑换积分
     */
    private Integer score;
    
    /**
     * 当前剩余库存
     */
    private Integer stock;
    
    /**
     * 总库存(初始投放量)
     */
    private Integer totalStock;
    
    /**
     * 销量 (总库存 - 当前库存)
     */
    private Integer sales;
    
    /**
     * 商品图片URL
     */
    private String image;
    
    /**
     * 商品详情描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}