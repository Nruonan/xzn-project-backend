package com.example.entity.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 积分商品创建请求DTO
 * @author Nruonan
 */
@Data
public class PointProductCreateReqDTO {
    
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    /**
     * 兑换积分
     */
    @NotNull(message = "兑换积分不能为空")
    @Min(value = 0, message = "兑换积分不能小于0")
    private Integer score;
    
    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stock;
    /**
     * 总库存(初始投放量)
     */
    @NotNull(message = "总库存不能为空")
    @Min(value = 0, message = "总库存不能小于0")
    private Integer totalStock;

    /**
     * 商品图片URL
     */
    private String image;
    
    /**
     * 商品详情描述
     */
    private String description;
}