package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分日志实体类
 * @author Nruonan
 */
@Data
@TableName("db_point_log")
public class PointLogDO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer uid;
    
    /**
     * 类型: exchange(兑换), post(发帖), sign(签到)
     */
    private String type;
    
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
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}