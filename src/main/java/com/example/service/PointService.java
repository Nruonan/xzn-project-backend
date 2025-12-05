package com.example.service;

import com.example.entity.dao.PointRuleDO;
import com.example.entity.dao.PointLogDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分服务接口
 * @author Nruonan
 */
public interface PointService {
    
    /**
     * 检查用户今天是否可以执行指定类型的操作
     * @param uid 用户ID
     * @param type 操作类型(post/comment/like/collect)
     * @return true表示可以执行，false表示已达到每日限制
     */
    boolean checkDailyLimit(Integer uid, String type);
    
    /**
     * 添加积分
     * @param uid 用户ID
     * @param type 操作类型(post/comment/like/collect)
     * @param refId 关联业务ID
     * @param remark 备注
     * @return true表示添加成功，false表示添加失败
     */
    boolean addPoint(Integer uid, String type, String refId, String remark);
    
    /**
     * 获取用户今天的积分日志
     * @param uid 用户ID
     * @param type 操作类型
     * @return 积分日志列表
     */
    List<PointLogDO> getTodayPointLogs(Integer uid, String type);
    
    /**
     * 获取积分规则
     * @param type 操作类型
     * @return 积分规则
     */
    PointRuleDO getPointRule(String type);
}