package com.example.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.req.PointOrderCreateReqDTO;
import com.example.entity.dto.req.PointProductCreateReqDTO;
import com.example.entity.dto.req.PointProductUpdateReqDTO;
import com.example.entity.dto.req.PointRuleCreateReqDTO;
import com.example.entity.dto.req.PointRuleUpdateReqDTO;
import com.example.entity.dto.resp.PointLogRespDTO;
import com.example.entity.dto.resp.PointOrderRespDTO;
import com.example.entity.dto.resp.PointProductRespDTO;
import com.example.entity.dto.resp.PointRuleRespDTO;
import com.example.service.ImageService;
import com.example.service.PointLogService;
import com.example.service.PointOrderService;
import com.example.service.PointProductService;
import com.example.service.PointRuleService;
import com.example.utils.Const;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 积分管理控制器
 * @author Nruonan
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/point")
public class PointAdminController {
    
    @Resource
    PointProductService pointProductService;
    
    @Resource
    PointOrderService pointOrderService;
    
    @Resource
    PointLogService pointLogService;
    
    @Resource
    PointRuleService pointRuleService;
    
    @Resource
    ImageService imageService;
    
    // ==================== 积分商品管理 ====================
    
    /**
     * 创建积分商品
     */
    @PostMapping("/product/create")
    public RestBean<Boolean> createProduct(@RequestBody PointProductCreateReqDTO reqDTO,
        @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(pointProductService.createProduct(reqDTO, id));
    }

    /**
     * 更新积分商品
     */
    @PostMapping("/product/update")
    public RestBean<Boolean> updateProduct(@RequestBody PointProductUpdateReqDTO reqDTO) {
        return RestBean.success(pointProductService.updateProduct(reqDTO));
    }

    /**
     * 删除积分商品
     */
    @GetMapping("/product/delete")
    public RestBean<Boolean> deleteProduct(@RequestParam("id") Integer id) {
        return RestBean.success(pointProductService.deleteProduct(id));
    }

    /**
     * 获取积分商品列表（分页）
     */
    @GetMapping("/product/list")
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
    public RestBean<PointProductRespDTO> getProductDetail(@RequestParam("id") Integer id) {
        return RestBean.success(pointProductService.getProductDetail(id));
    }
    
    // ==================== 积分订单管理 ====================
    
    /**
     * 更新订单状态
     */
    @GetMapping("/order/update-status")
    public RestBean<Boolean> updateOrderStatus(@RequestParam("id") String id, @RequestParam("status")Integer status) {
        return RestBean.success(pointOrderService.updateOrderStatus(id, status));
    }

    /**
     * 获取积分订单列表（分页）
     */
    @GetMapping("/order/list")
    public RestBean<Page<PointOrderRespDTO>> getOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="status", required = false) Integer status,
            @RequestParam(value ="username", required = false) String username) {
        return RestBean.success(pointOrderService.getOrderList(pageNum, pageSize, status, username));
    }

    /**
     * 获取积分订单详情
     */
    @GetMapping("/order/detail")
    public RestBean<PointOrderRespDTO> getOrderDetail(@RequestParam("id") String id) {
        return RestBean.success(pointOrderService.getOrderDetail(id));
    }
    
    // ==================== 积分日志管理 ====================
    
    /**
     * 获取积分日志列表（分页）
     */
    @GetMapping("/log/list")
    public RestBean<Page<PointLogRespDTO>> getLogList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="type", required = false) String type,
            @RequestParam(value ="username", required = false) String username) {
        return RestBean.success(pointLogService.getLogList(pageNum, pageSize, type, username));
    }
    
    // ==================== 积分规则管理 ====================
    
    /**
     * 创建积分规则
     */
    @PostMapping("/rule/create")
    public RestBean<Boolean> createRule(@RequestBody PointRuleCreateReqDTO reqDTO,
        @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(pointRuleService.createRule(reqDTO, id));
    }

    /**
     * 更新积分规则
     */
    @PostMapping("/rule/update")
    public RestBean<Boolean> updateRule(@RequestBody PointRuleUpdateReqDTO reqDTO) {
        return RestBean.success(pointRuleService.updateRule(reqDTO));
    }

    /**
     * 删除积分规则
     */
    @GetMapping("/rule/delete")
    public RestBean<Boolean> deleteRule(@RequestParam("id") Integer id) {
        return RestBean.success(pointRuleService.deleteRule(id));
    }

    /**
     * 获取积分规则列表（分页）
     */
    @GetMapping("/rule/list")
    public RestBean<Page<PointRuleRespDTO>> getRuleList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value ="type", required = false) String type) {
        return RestBean.success(pointRuleService.getRuleList(pageNum, pageSize, type));
    }

    /**
     * 获取积分规则详情
     */
    @GetMapping("/rule/detail")
    public RestBean<PointRuleRespDTO> getRuleDetail(@RequestParam("id") Integer id) {
        return RestBean.success(pointRuleService.getRuleDetail(id));
    }
}