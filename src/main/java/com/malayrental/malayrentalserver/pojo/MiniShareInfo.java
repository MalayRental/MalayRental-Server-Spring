package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("mini_shareinfo")
public class MiniShareInfo {
    @TableId(type = IdType.INPUT)
    private String id;
    private String page;
    private String title;
    @TableField("image_url")
    private String imageUrl;
    private String status;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
} 