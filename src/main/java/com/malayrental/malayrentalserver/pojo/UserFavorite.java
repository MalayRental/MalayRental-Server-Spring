package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {
    @TableId
    private String favoriteId;

    @TableField("user_id")
    private String userId;

    @TableField("house_id")
    private String houseId;

    @TableField("create_time")
    private LocalDateTime createTime;
} 