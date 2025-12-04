package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.AccountDO;
import com.example.entity.dao.PointLogDO;
import com.example.entity.dto.resp.PointLogRespDTO;
import com.example.mapper.PointLogMapper;
import com.example.service.AccountService;
import com.example.service.PointLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分日志Service实现类
 * @author Nruonan
 */
@Service
public class PointLogServiceImpl extends ServiceImpl<PointLogMapper, PointLogDO> implements PointLogService {
    
    @Resource
    private AccountService accountService;
    
    @Override
    public Page<PointLogRespDTO> getLogList(int pageNum, int pageSize, String type, String username) {
        Page<PointLogDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(type)) {
            queryWrapper.eq("type", type);
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<PointLogDO> logPage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointLogRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(logPage, respPage, "records");
        
        List<PointLogRespDTO> respList = logPage.getRecords().stream().map(log -> {
            PointLogRespDTO respDTO = new PointLogRespDTO();
            BeanUtils.copyProperties(log, respDTO);
            
            // 设置类型描述
            switch (log.getType()) {
                case "exchange":
                    respDTO.setTypeDesc("兑换商品");
                    break;
                case "post":
                    respDTO.setTypeDesc("发布文章");
                    break;
                case "comment":
                    respDTO.setTypeDesc("发表评论");
                    break;
                case "like":
                    respDTO.setTypeDesc("点赞");
                    break;
                case "sign":
                    respDTO.setTypeDesc("签到");
                    break;
                default:
                    respDTO.setTypeDesc(log.getType());
            }
            
            // 获取用户名
            AccountDO account = accountService.getById(log.getUid().longValue());
            if (account != null) {
                respDTO.setUsername(account.getUsername());
            }
            
            return respDTO;
        }).collect(Collectors.toList());
        
        // 如果指定了用户名，进行过滤
        if (StringUtils.hasText(username)) {
            respList = respList.stream()
                    .filter(resp -> resp.getUsername() != null && resp.getUsername().contains(username))
                    .collect(Collectors.toList());
        }
        
        respPage.setRecords(respList);
        return respPage;
    }
    
    @Override
    public boolean recordPointChange(Integer uid, String type, Integer score, String refId, String remark) {
        PointLogDO log = new PointLogDO();
        log.setUid(uid);
        log.setType(type);
        log.setScore(score);
        log.setRefId(refId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        return this.save(log);
    }
}