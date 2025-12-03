package com.example.entity.dto.resp;

import lombok.Data;
import java.util.Date;

@Data
public class ActivityRespDTO {
    private Integer id;
    private String title;
    private String url;
    private String location;
    private String picture;
    private Date startTime;
    private Date endTime;
    private Date time;
    private Integer uid;
    private String username;
}