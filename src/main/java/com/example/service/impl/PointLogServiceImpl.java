package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.AccountDO;
import com.example.entity.dao.AccountDetailsDO;
import com.example.entity.dao.PointLogDO;
import com.example.entity.dto.resp.PointLogRespDTO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.PointLogMapper;
import com.example.service.AccountService;
import com.example.service.PointLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
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

    @Resource
    private AccountDetailsMapper accountDetailsMapper;

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
    public Page<PointLogRespDTO> getUserLogList(int pageNum, int pageSize, Integer type, Integer uid) {
        Page<PointLogDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.eq("uid", uid);
        
        if (type != null) {
            if (type == 1) {
                queryWrapper.eq("type", "interact")
                    .or().eq("type", "post")
                    .or().eq("type", "like")
                    .or().eq("type", "comment");
            } else if (type == 2) {
                queryWrapper.eq("type", "exchange");
            }
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<PointLogDO> logPage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointLogRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(logPage, respPage, "records");
        
        List<PointLogRespDTO> respList = logPage.getRecords().stream().map(log -> {
            PointLogRespDTO respDTO = new PointLogRespDTO();
            BeanUtils.copyProperties(log, respDTO);
            

            // 获取用户名
            AccountDO account = accountService.getById(log.getUid().longValue());
            if (account != null) {
                respDTO.setUsername(account.getUsername());
            }
            
            return respDTO;
        }).collect(Collectors.toList());
        
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

    @Override
    public Integer getUserPoint(Integer uid) {
        AccountDetailsDO accountDetails = accountDetailsMapper.selectById(uid);
        return accountDetails.getScore() != null ? accountDetails.getScore() : 0;
    }

    @Override
    public Integer getTodayEarnedPoints(Integer uid) {
        // 获取今天的开始和结束时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        
        // 查询今天获得的积分（正分数表示获得）
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.gt("score", 0); // 获得记录为正分
        queryWrapper.between("create_time", todayStart, todayEnd);
        
        List<PointLogDO> earnedLogs = this.list(queryWrapper);
        
        // 计算总获得积分
        return earnedLogs.stream()
                .mapToInt(log -> log.getScore())
                .sum();
    }

    @Override
    public Integer getMonthEarnedPoints(Integer uid) {
        // 获取本月的开始和结束时间
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // 查询本月获得的积分（正分数表示获得）
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.gt("score", 0); // 获得记录为正分
        queryWrapper.between("create_time", monthStart, monthEnd);
        
        List<PointLogDO> earnedLogs = this.list(queryWrapper);
        
        // 计算总获得积分
        return earnedLogs.stream()
                .mapToInt(log -> log.getScore())
                .sum();
    }

    @Override
    public Integer getTotalConsumedPoints(Integer uid) {
        // 查询所有消费的积分（负分数表示消费）
        QueryWrapper<PointLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.lt("score", 0); // 消费记录为负分
        
        List<PointLogDO> consumedLogs = this.list(queryWrapper);
        
        // 计算总消费积分（取绝对值）
        return consumedLogs.stream()
                .mapToInt(log -> Math.abs(log.getScore()))
                .sum();
    }
}