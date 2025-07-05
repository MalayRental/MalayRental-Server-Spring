package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("mini_searchkey")
public class MiniSearchKey {
    @TableId(type = IdType.INPUT)
    private String id;
    private String type; // guess/hot
    private String keyword;
    private Integer sort;
    private String status;
    @TableField("create_time")
    private LocalDateTime createTime;
} 