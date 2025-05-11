package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_list")
public class ChatList {
    @TableId
    private String chatId;

    @TableField("user_id")
    private String userId;

    @TableField("staff_id")
    private String staffId;

    @TableField("create_time")
    private LocalDateTime createTime;
} 