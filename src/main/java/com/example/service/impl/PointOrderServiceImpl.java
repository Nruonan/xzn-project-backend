package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(PointOrderCreateReqDTO reqDTO, Long uid) {
        // 查询商品信息
        PointProductDO product = pointProductService.getById(reqDTO.getProductId());
        if (product == null) {
            return false;
        }
        
        // 检查库存
        if (product.getStock() <= 0) {
            return false;
        }
        
        // 查询用户信息
        AccountDetailsDO account = accountDetailsMapper.selectById(uid);
        if (account == null) {
            return false;
        }
        
        // 检查用户积分是否足够
        if (account.getScore() < product.getScore()) {
            return false;
        }
        
        // 创建订单
        PointOrderDO order = new PointOrderDO();
        BeanUtils.copyProperties(reqDTO, order, "productId");
        order.setUid(uid.intValue());
        order.setProductId(reqDTO.getProductId());
        order.setPayScore(product.getScore());
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
            pointLogService.recordPointChange(uid.intValue(), "exchange", -product.getScore(),
                order.getId(), "兑换商品：" + product.getName());
        }
        
        return result;
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