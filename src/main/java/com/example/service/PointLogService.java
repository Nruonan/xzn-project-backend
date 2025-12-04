package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.PointLogDO;
import com.example.entity.dto.resp.PointLogRespDTO;

/**
 * 积分日志Service接口
 * @author Nruonan
 */
public interface PointLogService extends IService<PointLogDO> {
    
    /**
     * 获取积分日志列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param type 类型（可选）
     * @param username 用户名（可选）
     * @return 分页结果
     */
    Page<PointLogRespDTO> getLogList(int pageNum, int pageSize, String type, String username);
    
    /**
     * 记录积分变动
     * @param uid 用户ID
     * @param type 类型
     * @param score 积分变动
     * @param refId 关联ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean recordPointChange(Integer uid, String type, Integer score, String refId, String remark);
}