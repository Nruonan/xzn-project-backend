package com.example.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName db_activity
 */
@TableName(value ="db_activity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDO implements Serializable {
    /**
     * 
     */
    @TableId(type= IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String location;

    /**
     * 
     */
    private String picture;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date time;

    /**
     * 创建者ID
     */
    private Integer uid;


}