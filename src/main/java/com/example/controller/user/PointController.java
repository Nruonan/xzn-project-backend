package com.example.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.config.exception.ServiceException;
import com.example.entity.RestBean;
import com.example.entity.dto.req.PointOrderCreateReqDTO;
import com.example.entity.dto.resp.PointLogRespDTO;
import com.example.entity.dto.resp.PointOrderRespDTO;
import com.example.entity.dto.resp.PointProductRespDTO;
import com.example.entity.dto.resp.UserPointRuleStatusRespDTO;
import com.example.service.PointLogService;
import com.example.service.PointOrderService;
import com.example.service.PointProductService;
import com.example.service.PointRuleService;
import com.example.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端积分控制器
 * @author Nruonan
 */
@RestController
@RequestMapping("/api/point")
public class PointController {

    @Autowired
    private PointLogService pointLogService;
    
    @Autowired
    private PointOrderService pointOrderService;
    
    @Autowired
    private PointProductService pointProductService;
    @Resource
    private PointRuleService pointRuleService;
    // ==================== 积分明细 ====================
    
    /**
     * 获取当前用户的积分明细（分页）
     */
    @GetMapping("/log/list")
    @Operation(summary = "获取当前用户的积分明细")
    public RestBean<Page<PointLogRespDTO>> getMyLogList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="type", required = false) Integer type,
            @RequestAttribute(Const.ATTR_USER_ID) Integer uid) {
        return RestBean.success(pointLogService.getUserLogList(pageNum, pageSize, type, uid));
    }

    @GetMapping("/log/my-point")
    @Operation(summary = "获取当前用户的积分")
    public RestBean<Integer> getMyPoint(@RequestAttribute(Const.ATTR_USER_ID) Integer uid) {
        return RestBean.success(pointLogService.getUserPoint(uid));
    }
    
    @GetMapping("/rule/have-point")
    @Operation(summary = "获取当前用户的每天可获得积分")
    public RestBean<UserPointRuleStatusRespDTO> getUserPointRuleStatus(@RequestAttribute(Const.ATTR_USER_ID) Integer uid) {
        return RestBean.success(pointRuleService.getUserPointRuleStatus(uid));
    }
    // ==================== 积分订单 ====================
    
    /**
     * 获取当前用户的积分订单列表（分页）
     */
    @GetMapping("/order/list")
    @Operation(summary = "获取当前用户的积分订单列表")
    public RestBean<Page<PointOrderRespDTO>> getMyOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestAttribute(Const.ATTR_USER_ID) Integer uid) {
        return RestBean.success(pointOrderService.getUserOrderList(pageNum, pageSize, status, startTime, endTime, uid));
    }
    
    /**
     * 获取积分订单详情
     */
    @GetMapping("/order/detail")
    @Operation(summary = "获取积分订单详情")
    public RestBean<PointOrderRespDTO> getOrderDetail(@RequestParam("id") String id) {
        return RestBean.success(pointOrderService.getOrderDetail(id));
    }
    
    // ==================== 积分商品 ====================
    
    /**
     * 获取积分商品列表（分页）
     */
    @GetMapping("/product/list")
    @Operation(summary = "获取积分商品列表")
    public RestBean<Page<PointProductRespDTO>> getProductList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="name", required = false) String name) {
        return RestBean.success(pointProductService.getProductList(pageNum, pageSize, name));
    }
    
    /**
     * 获取积分商品详情
     */
    @GetMapping("/product/detail")
    @Operation(summary = "获取积分商品详情")
    public RestBean<PointProductRespDTO> getProductDetail(@RequestParam("id") Integer id) {
        return RestBean.success(pointProductService.getProductDetail(id));
    }
    
    // ==================== 兑换产品 ====================
    
    /**
     * 兑换产品
     */
    @PostMapping("/order/create")
    @Operation(summary = "兑换产品", description = "用户使用积分兑换产品")
    public RestBean<Boolean> createOrder(@RequestBody PointOrderCreateReqDTO reqDTO,
                                         @RequestAttribute(Const.ATTR_USER_ID) Integer userId) {
        try {
            boolean result = pointOrderService.createOrder(reqDTO, userId);
            return result ? RestBean.success(true) : RestBean.failure(400, "兑换失败");
        } catch (ServiceException e) {
            return RestBean.failure(400, e.getMessage());
          }
    }
}