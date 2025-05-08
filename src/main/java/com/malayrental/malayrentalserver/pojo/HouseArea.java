package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("house_area")
public class HouseArea {
    @TableId
    private String areaId;

    @TableField("area_name")
    private String areaName;

    private String address;

    @TableField("lat_lng")
    private String latLng;

    @TableField("desc")
    private String desc;

    @TableField("create_user")
    private String createUser;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_time")
    private LocalDateTime createTime;
} 