package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分商品实体类
 * @author Nruonan
 */
@Data
@TableName("db_point_product")
public class PointProductDO {
    @TableId(type = IdType.AUTO)
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