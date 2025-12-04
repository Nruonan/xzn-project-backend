package com.example.entity.dto.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

/**
 * 积分订单创建请求DTO
 * @author Nruonan
 */
@Data
public class PointOrderCreateReqDTO {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 电话
     */
    @NotBlank(message = "电话不能为空")
    private String phone;
    
    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String address;

    /**
     * 总积分
     */
    @NotNull(message = "总积分不能为空")
    private Integer payScore;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于等于1")
    private Integer Count;
}