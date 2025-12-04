package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分规则实体类
 * @author Nruonan
 */
@Data
@TableName("db_point_rule")
public class PointRuleDO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
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