package com.example.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.resp.ActivityRespDTO;
import com.example.service.ActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动控制器 - 客户端访问
 * @author Nruonan
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    
    @Resource
    private ActivityService activityService;

    /**
     * 获取最新活动
     */
    @GetMapping("/latest")
    public RestBean<ActivityRespDTO> getLatestActivity() {
        return RestBean.success(activityService.getLatestActivity());
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/detail")
    public RestBean<ActivityRespDTO> getActivityDetail(@RequestParam("id") Integer id) {
        return RestBean.success(activityService.getActivityDetail(id));
    }
    
    /**
     * 获取活动列表（分页）
     */
    @GetMapping("/list")
    public RestBean<Page<ActivityRespDTO>> getActivityList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="title", required = false) String title) {
        return RestBean.success(activityService.getActivityList(pageNum, pageSize, title));
    }
}