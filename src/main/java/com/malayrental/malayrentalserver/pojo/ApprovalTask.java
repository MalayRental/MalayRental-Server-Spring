package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_task")
public class ApprovalTask {
    @TableId
    private String approvalId;

    private String title;

    @TableField(value = "`desc`")
    private String desc;

    private String status;

    private String command;

    @TableField("create_user")
    private String createUser;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("finish_user")
    private String finishUser;

    @TableField("finish_time")
    private LocalDateTime finishTime;

    @TableField("reject_reason")
    private String rejectReason;
} 