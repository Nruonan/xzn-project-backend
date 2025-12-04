package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dao.PointProductDO;
import com.example.entity.dto.req.PointProductCreateReqDTO;
import com.example.entity.dto.req.PointProductUpdateReqDTO;
import com.example.entity.dto.resp.PointProductRespDTO;
import com.example.mapper.PointProductMapper;
import com.example.service.PointProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 积分商品Service实现类
 * @author Nruonan
 */
@Service
public class PointProductServiceImpl extends ServiceImpl<PointProductMapper, PointProductDO> implements PointProductService {
    
    @Override
    public boolean createProduct(PointProductCreateReqDTO reqDTO, Integer adminId) {
        PointProductDO product = new PointProductDO();
        BeanUtils.copyProperties(reqDTO, product);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return this.save(product);
    }
    
    @Override
    public boolean updateProduct(PointProductUpdateReqDTO reqDTO) {
        if (reqDTO == null) {
            return false;
        }

        PointProductDO bean = BeanUtil.toBean(reqDTO, PointProductDO.class);
        bean.setUpdateTime(LocalDateTime.now());
        return this.updateById(bean);
    }
    
    @Override
    public boolean deleteProduct(Integer id) {
        return this.removeById(id);
    }
    
    @Override
    public Page<PointProductRespDTO> getProductList(int pageNum, int pageSize, String name) {
        Page<PointProductDO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PointProductDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("create_time");
        Page<PointProductDO> productPage = this.page(page, queryWrapper);
        
        // 转换为响应DTO
        Page<PointProductRespDTO> respPage = new Page<>();
        BeanUtils.copyProperties(productPage, respPage, "records");
        
        java.util.List<PointProductRespDTO> respList = productPage.getRecords().stream().map(product -> {
            PointProductRespDTO respDTO = new PointProductRespDTO();
            BeanUtils.copyProperties(product, respDTO);
            // 计算销量
            respDTO.setSales(product.getTotalStock() - product.getStock());
            return respDTO;
        }).collect(java.util.stream.Collectors.toList());
        
        respPage.setRecords(respList);
        return respPage;
    }
    
    @Override
    public PointProductRespDTO getProductDetail(Integer id) {
        PointProductDO product = this.getById(id);
        if (product == null) {
            return null;
        }
        
        PointProductRespDTO respDTO = new PointProductRespDTO();
        BeanUtils.copyProperties(product, respDTO);
        // 计算销量
        respDTO.setSales(product.getTotalStock() - product.getStock());
        return respDTO;
    }
}