package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@TableName("house_list")
public class HouseList {
    @TableId
    private String houseId;

    @TableField("house_name")
    private String houseName;

    private String area;

    private String orientation;

    private BigDecimal proportion;

    @TableField("cover_image")
    private String coverImage;

    @TableField("create_user")
    private String createUser;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    private String status;
} 