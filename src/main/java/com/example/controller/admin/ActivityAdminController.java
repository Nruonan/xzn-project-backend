package com.example.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.req.ActivityCreateReqDTO;
import com.example.entity.dto.resp.ActivityRespDTO;
import com.example.service.ActivityService;
import com.example.service.ImageService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.example.entity.dto.req.ActivityUpdateReqDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 活动管理控制器
 * @author Nruonan
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/activity")
public class ActivityAdminController {
    @Resource
    ActivityService activityService;
    
    @Resource
    ImageService imageService;

    /**
     * 创建活动
     */
    @PostMapping("/create")
    public RestBean<Boolean> createActivity(@RequestBody ActivityCreateReqDTO reqDTO,
        @RequestAttribute(Const.ATTR_USER_ID) int id) { // 假设 userId 通过拦截器注入
        return RestBean.success(activityService.createActivity(reqDTO, id));
    }

    /**
     * 更新活动
     */
    @PostMapping("/update")
    public RestBean<Boolean> updateActivity(@RequestBody ActivityUpdateReqDTO reqDTO) {
        return RestBean.success(activityService.updateActivity(reqDTO));
    }

    /**
     * 删除活动
     */
    @GetMapping("/delete")
    public RestBean<Boolean> deleteActivity(@RequestParam("id") Integer id) {
        return RestBean.success(activityService.deleteActivity(id));
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

    /**
     * 获取单个活动详情
     */
    @GetMapping("/detail")
    public RestBean<ActivityRespDTO> getActivityDetail(@RequestParam("id") Integer id) {
        return RestBean.success(activityService.getActivityDetail(id));
    }
}