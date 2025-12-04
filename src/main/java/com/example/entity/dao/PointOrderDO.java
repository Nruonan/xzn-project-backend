package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分订单实体类
 * @author Nruonan
 */
@Data
@TableName("db_point_order")
public class PointOrderDO {
    @TableId
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
    private Integer count;
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 实付积分
     */
    private Integer payScore;
    
    /**
     * 状态: 0待处理, 1已完成, 2已取消
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}