package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message_list")
public class MessageList {
    @TableId
    private String messageId;

    @TableField("chat_id")
    private String chatId;

    @TableField("sender_id")
    private String senderId;

    @TableField("message_type")
    private String messageType;

    private String content;

    private String status;

    @TableField("create_time")
    private LocalDateTime createTime;
} 