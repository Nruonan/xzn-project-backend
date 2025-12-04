package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.controller.exception.ServiceException;
import com.example.entity.dao.AccountDO;
import com.example.entity.dao.AccountDetailsDO;
import com.example.entity.dao.PointOrderDO;
import com.example.entity.dao.PointProductDO;
import com.example.entity.dto.req.PointOrderCreateReqDTO;
import com.example.entity.dto.resp.PointOrderRespDTO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.PointOrderMapper;
import com.example.service.AccountService;
import com.example.service.PointLogService;
import com.example.service.PointOrderService;
import com.example.service.PointProductService;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 积分订单Service实现类
 * @author Nruonan
 */
@Service
public class PointOrderServiceImpl extends ServiceImpl<PointOrderMapper, PointOrderDO> implements PointOrderService {
    
    @Resource
    private PointProductService pointProductService;
    
    @Resource
    private AccountService accountService;
    
    @Resource
    private PointLogService pointLogService;
    
    @Resource
    private AccountDetailsMapper accountDetailsMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(PointOrderCreateReqDTO reqDTO, Integer uid) {
        // 定义Redis锁的key
        String lockKey = "point_order_lock:" + reqDTO.getProductId();
        String lockValue = UUID.randomUUID().toString();
        
        try {
            // 尝试获取分布式锁，最多等待5秒，锁自动过期时间10秒
            Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(
                lockKey, lockValue, 10, TimeUnit.SECONDS);
            
            if (!locked) {
                throw new ServiceException("商品已被其他请求处理，请稍后重试");
            }
            
            // 查询商品信息
            PointProductDO product = pointProductService.getById(reqDTO.getProductId());
            if (product == null) {
                throw new ServiceException("商品不存在");
            }
            
            // 检查库存
            if (product.getStock() <= 0) {
                throw new ServiceException("商品库存不足");
            }
            
            // 查询用户信息
            AccountDetailsDO account = accountDetailsMapper.selectById(uid.longValue());
            if (account == null) {
                throw new ServiceException("用户不存在");
            }
            
            // 检查用户积分是否足够
            if (account.getScore() < reqDTO.getPayScore()) {
                throw new ServiceException("用户积分不足");
            }
            
            // 创建订单
            PointOrderDO order = new PointOrderDO();
            BeanUtils.copyProperties(reqDTO, order, "productId");
            order.setUid(uid);
            order.setProductId(reqDTO.getProductId());
            order.setPayScore(reqDTO.getPayScore());
            order.setCount(reqDTO.getCount());
            order.setStatus(0); // 待处理
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            // 保存订单
            boolean result = this.save(order);
            
            if (result) {
                // 扣减库存
                product.setStock(product.getStock() - 1);
                pointProductService.updateById(product);
                    // 扣减用户积分
                account.setScore(account.getScore() - product.getScore());
                accountDetailsMapper.updateById(account);
                
                // 记录积分变动日志
                pointLogService.recordPointChange(uid, "exchange", -product.getScore(),
                    order.getId(), "兑换商品：" + product.getName());
            }
            
            return result;
        } finally {
            // 释放锁：使用Lua脚本确保只有锁的持有者才能释放锁
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            stringRedisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
        }
    }
    
    @Override
    public boolean updateOrderStatus(String id, Integer status) {
        PointOrderDO order = this.getById(id);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return this.updateById(order);
    }
    
    @Override
    public Page<PointOrderRespDTO> getOrderList(int pageNum, int pageSize, Integer status, String username) {
        Page<PointOrderDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointOrderDO> queryWrapper = new QueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like("username", username);
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<PointOrderDO> orderPage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointOrderRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(orderPage, respPage, "records");
        
        List<PointOrderRespDTO> respList = orderPage.getRecords().stream().map(order -> {
            PointOrderRespDTO respDTO = new PointOrderRespDTO();
            BeanUtils.copyProperties(order, respDTO);
            
            // 设置状态描述
            switch (order.getStatus()) {
                case 0:
                    respDTO.setStatusDesc("待处理");
                    break;
                case 1:
                    respDTO.setStatusDesc("已完成");
                    break;
                case 2:
                    respDTO.setStatusDesc("已取消");
                    break;
                default:
                    respDTO.setStatusDesc("未知状态");
            }
            
            // 如果没有用户名，尝试从用户表获取
            if (!StringUtils.hasText(respDTO.getUsername())) {
                AccountDO account = accountService.getById(order.getUid().longValue());
                if (account != null) {
                    respDTO.setUsername(account.getUsername());
                }
            }
            
            // 获取商品名称
            PointProductDO product = pointProductService.getById(order.getProductId());
            if (product != null) {
                respDTO.setProductName(product.getName());
                respDTO.setImage(product.getImage());
            }
            
            return respDTO;
        }).collect(Collectors.toList());
        
        respPage.setRecords(respList);
        return respPage;
    }
    
    @Override
    public Page<PointOrderRespDTO> getUserOrderList(int pageNum, int pageSize, Integer status, Date startTime, Date endTime, Integer uid) {
        Page<PointOrderDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointOrderDO> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.eq("uid", uid);
        
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        if (startTime != null) {
            queryWrapper.ge("create_time", LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault()));
        }

        if (endTime != null) {
            queryWrapper.le("create_time", LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault()));
        }

        queryWrapper.orderByDesc("create_time");
        Page<PointOrderDO> orderPage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointOrderRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(orderPage, respPage, "records");
        
        List<PointOrderRespDTO> respList = orderPage.getRecords().stream().map(order -> {
            PointOrderRespDTO respDTO = new PointOrderRespDTO();
            BeanUtils.copyProperties(order, respDTO);
            
            // 设置状态描述
            switch (order.getStatus()) {
                case 0:
                    respDTO.setStatusDesc("待处理");
                    break;
                case 1:
                    respDTO.setStatusDesc("已完成");
                    break;
                case 2:
                    respDTO.setStatusDesc("已取消");
                    break;
                default:
                    respDTO.setStatusDesc("未知状态");
            }
            
            // 获取用户名
            AccountDO account = accountService.getById(order.getUid().longValue());
            if (account != null) {
                respDTO.setUsername(account.getUsername());
            }
            
            // 获取商品名称
            PointProductDO product = pointProductService.getById(order.getProductId());
            if (product != null) {
                respDTO.setProductName(product.getName());
            }
            
            return respDTO;
        }).collect(Collectors.toList());
        
        respPage.setRecords(respList);
        return respPage;
    }
    
    @Override
    public PointOrderRespDTO getOrderDetail(String id) {
        PointOrderDO order = this.getById(id);
        if (order == null) {
            return null;
        }
        
        PointOrderRespDTO respDTO = new PointOrderRespDTO();
        BeanUtils.copyProperties(order, respDTO);
        
        // 设置状态描述
        switch (order.getStatus()) {
            case 0:
                respDTO.setStatusDesc("待处理");
                break;
            case 1:
                respDTO.setStatusDesc("已完成");
                break;
            case 2:
                respDTO.setStatusDesc("已取消");
                break;
            default:
                respDTO.setStatusDesc("未知状态");
        }
        
        // 如果没有用户名，尝试从用户表获取
        if (!StringUtils.hasText(respDTO.getUsername())) {
            AccountDO account = accountService.getById(order.getUid().longValue());
            if (account != null) {
                respDTO.setUsername(account.getUsername());
            }
        }
        
        // 获取商品名称
        PointProductDO product = pointProductService.getById(order.getProductId());
        if (product != null) {
            respDTO.setProductName(product.getName());
        }
        
        return respDTO;
    }
}