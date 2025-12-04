package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.PointProductDO;
import com.example.entity.dto.req.PointProductCreateReqDTO;
import com.example.entity.dto.req.PointProductUpdateReqDTO;
import com.example.entity.dto.resp.PointProductRespDTO;

/**
 * 积分商品Service接口
 * @author Nruonan
 */
public interface PointProductService extends IService<PointProductDO> {
    
    /**
     * 创建积分商品
     * @param reqDTO 创建请求DTO
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean createProduct(PointProductCreateReqDTO reqDTO, Integer adminId);
    
    /**
     * 更新积分商品
     * @param reqDTO 更新请求DTO
     * @return 是否成功
     */
    boolean updateProduct(PointProductUpdateReqDTO reqDTO);
    
    /**
     * 删除积分商品
     * @param id 商品ID
     * @return 是否成功
     */
    boolean deleteProduct(Integer id);
    
    /**
     * 获取积分商品列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param name 商品名称（可选）
     * @return 分页结果
     */
    Page<PointProductRespDTO> getProductList(int pageNum, int pageSize, String name);
    
    /**
     * 获取积分商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    PointProductRespDTO getProductDetail(Integer id);
}