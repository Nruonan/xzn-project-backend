package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.AccountDO;
import com.example.entity.dao.ActivityDO;
import com.example.entity.dto.req.ActivityCreateReqDTO;
import com.example.entity.dto.req.ActivityUpdateReqDTO;
import com.example.entity.dto.resp.ActivityRespDTO;
import com.example.mapper.AccountMapper;
import com.example.mapper.ActivityMapper;
import com.example.service.ActivityService;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
* @author Nruonan
* @description 针对表【db_activity(活动表)】的数据库操作Service实现
* @createDate 2025-11-03 21:07:00
*/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, ActivityDO>
    implements ActivityService {
    @Resource
    private AccountMapper accountMapper;

    @Override
    public boolean createActivity(ActivityCreateReqDTO reqDTO, int authorId) {
        ActivityDO activity = new ActivityDO();
        activity.setTitle(reqDTO.getTitle());
        activity.setUrl(reqDTO.getUrl());
        activity.setLocation(reqDTO.getLocation());
        activity.setPicture(reqDTO.getPicture());
        activity.setStartTime(reqDTO.getStartTime());
        activity.setEndTime(reqDTO.getEndTime());
        activity.setTime(new Date());
        activity.setUid(authorId);
        return this.save(activity);
    }

    @Override
    public boolean updateActivity(ActivityUpdateReqDTO reqDTO) {
        ActivityDO activity = this.getById(reqDTO.getId());
        if (activity == null) {
            return false;
        }

        activity.setTitle(reqDTO.getTitle());
        activity.setUrl(reqDTO.getUrl());
        activity.setLocation(reqDTO.getLocation());
        activity.setPicture(reqDTO.getPicture());
        activity.setStartTime(reqDTO.getStartTime());
        activity.setEndTime(reqDTO.getEndTime());
        return this.updateById(activity);
    }

    @Override
    public boolean deleteActivity(Integer id) {
        return this.removeById(id);
    }

    @Override
    public Page<ActivityRespDTO> getActivityList(int pageNum, int pageSize, String title) {
        Page<ActivityDO> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ActivityDO> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            wrapper.like(ActivityDO::getTitle, title)
                .orderByDesc(ActivityDO::getTime);
        } else {
            wrapper.orderByDesc(ActivityDO::getTime);
        }
        IPage<ActivityDO> result = this.page(page, wrapper);

        Page<ActivityRespDTO> respPage = new Page<>();
        respPage.setCurrent(result.getCurrent());
        respPage.setSize(result.getSize());
        respPage.setTotal(result.getTotal());
        respPage.setRecords(result.getRecords().stream()
            .map(item -> {
                ActivityRespDTO respDTO = new ActivityRespDTO();
                BeanUtil.copyProperties(item, respDTO);
                // 设置作者信息
                AccountDO author = accountMapper.selectById(item.getUid());
                if (author != null) {
                    respDTO.setUsername(author.getUsername());
                }
                return respDTO;
            })
            .collect(Collectors.toList()));

        return respPage;
    }

    @Override
    public ActivityRespDTO getActivityDetail(Integer id) {
        ActivityDO activity = this.getById(id);
        if (activity == null) {
            return null;
        }

        return convertToRespDTO(activity);
    }

    @Override
    public ActivityRespDTO getLatestActivity() {
        LambdaQueryWrapper<ActivityDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ActivityDO::getTime).last("LIMIT 1");
        ActivityDO activity = this.getOne(wrapper);
        if (activity == null) {
            return null;
        }
        return convertToRespDTO(activity);
    }

    private ActivityRespDTO convertToRespDTO(ActivityDO activity) {
        ActivityRespDTO respDTO = new ActivityRespDTO();
        BeanUtil.copyProperties(activity, respDTO);
        
        // 设置作者信息
        AccountDO author = accountMapper.selectById(activity.getUid());
        if (author != null) {
            respDTO.setUsername(author.getUsername());
        }
        
        return respDTO;
    }
}