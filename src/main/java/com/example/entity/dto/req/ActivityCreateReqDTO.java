package com.example.entity.dto.req;

import lombok.Data;
import java.util.Date;

@Data
public class ActivityCreateReqDTO {
    private String title;
    private String url;
    private String location;
    private String picture;
    private Date startTime;
    private Date endTime;
}