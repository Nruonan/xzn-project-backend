package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.dao.ActivityDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.req.ActivityCreateReqDTO;
import com.example.entity.dto.req.ActivityUpdateReqDTO;
import com.example.entity.dto.resp.ActivityRespDTO;

/**
* @author Nruonan
* @description 针对表【db_activity(活动表)】的数据库操作Service
* @createDate 2025-11-03 21:07:00
*/
public interface ActivityService extends IService<ActivityDO> {
    /**
     * 创建活动
     */
    boolean createActivity(ActivityCreateReqDTO reqDTO, int authorId);

    /**
     * 更新活动
     */
    boolean updateActivity(ActivityUpdateReqDTO reqDTO);

    /**
     * 删除活动
     */
    boolean deleteActivity(Integer id);

    /**
     * 分页获取活动列表
     */
    Page<ActivityRespDTO> getActivityList(int pageNum, int pageSize, String title);

    /**
     * 获取活动详情
     */
    ActivityRespDTO getActivityDetail(Integer id);
    
    /**
     * 获取最新活动
     */
    ActivityRespDTO getLatestActivity();
}