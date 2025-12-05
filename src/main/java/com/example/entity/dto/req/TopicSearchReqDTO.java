package com.example.entity.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Nruonan
 * @description 帖子搜索请求DTO
 */
@Data
public class TopicSearchReqDTO {
    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 50, message = "搜索关键词长度必须在1-50个字符之间")
    private String keyword;
    
    private Integer page = 1;
    
    private Integer size = 10;
}