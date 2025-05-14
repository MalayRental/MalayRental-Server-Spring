package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("mini_banner")
public class MiniBanner {
    @TableId
    private String bannerId;

    private String image;

    private String link;

    private String status;

    @TableField("create_time")
    private LocalDateTime createTime;
} 