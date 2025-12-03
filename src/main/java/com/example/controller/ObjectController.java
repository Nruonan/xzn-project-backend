package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.resp.BrowserRespDTO;
import com.example.service.ImageService;
import com.example.utils.CommonUtils;
import com.example.utils.Const;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Nruonan
 * @description
 */
@Slf4j
@RestController
public class ObjectController {
    @Resource
    ImageService service;



    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpg");
        this.fetchImage(request, response);
    }

    @GetMapping("/api/common")
    public RestBean<BrowserRespDTO> getBrowser(HttpServletRequest request){
        String browser = CommonUtils.getBrowser((HttpServletRequest) request);
        String ip = CommonUtils.getActualIp((HttpServletRequest) request);
        return RestBean.success(new BrowserRespDTO(ip,browser));
    }

    /**
     * 上传活动图片
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/api/activity/upload-image")
    public RestBean<String> uploadActivityImage(@RequestParam("file") MultipartFile file,
                                                @RequestAttribute(Const.ATTR_USER_ID) int id) {
        try {
            if(file.getSize() > 1024 * 100 * 5)
                return RestBean.failure(400, "活动图片不能大于5MB");
            log.info("正在进行活动图片上传操作...");
            String url = service.uploadImage(file, id);
            if(url != null) {
                log.info("活动图片上传成功，大小: " + file.getSize());
                return RestBean.success(url);
            } else {
                return RestBean.failure(400, "活动图片上传失败，请联系管理员！");
            }
        } catch (Exception e) {
            log.error("活动图片上传失败: " + e.getMessage(), e);
            return RestBean.failure(400, "活动图片上传失败，请联系管理员！");
        }
    }

    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream stream = response.getOutputStream();
        if(imagePath.length() <= 13) {
            response.setStatus(404);
            stream.println(RestBean.failure(404, "Not found").toString());
        } else {
            try {
                service.fetchImageFromMinio(stream, imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if(e.response().code() == 404) {
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not found").toString());
                } else {
                    log.error("从Minio获取图片出现异常: "+e.getMessage(), e);
                }
            }
        }
    }

}
