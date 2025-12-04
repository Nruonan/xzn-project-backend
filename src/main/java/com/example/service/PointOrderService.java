package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dao.PointOrderDO;
import com.example.entity.dto.req.PointOrderCreateReqDTO;
import com.example.entity.dto.resp.PointOrderRespDTO;
import java.util.Date;

/**
 * 积分订单Service接口
 * @author Nruonan
 */
public interface PointOrderService extends IService<PointOrderDO> {
    
    /**
     * 创建积分订单
     * @param reqDTO 创建订单请求DTO
     * @param uid 用户ID
     * @return 是否成功
     */
    boolean createOrder(PointOrderCreateReqDTO reqDTO, Integer uid);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateOrderStatus(String id, Integer status);
    
    /**
     * 获取积分订单列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 订单状态（可选）
     * @param username 用户名（可选）
     * @return 分页结果
     */
    Page<PointOrderRespDTO> getOrderList(int pageNum, int pageSize, Integer status, String username);
    
    /**
     * 获取指定用户的积分订单列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 订单状态（可选）
     * @param uid 用户ID
     * @return 分页结果
     */
    Page<PointOrderRespDTO> getUserOrderList(int pageNum, int pageSize, Integer status, Date startTime, Date endTime, Integer uid);
    
    /**
     * 获取积分订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    PointOrderRespDTO getOrderDetail(String id);
}