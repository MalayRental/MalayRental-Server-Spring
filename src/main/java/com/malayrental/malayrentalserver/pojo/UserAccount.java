package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldStrategy;

@Data
@TableName("user_account")
public class UserAccount {
    @TableId
    private String userId;

    @TableField("user_name")
    private String userName;

    @TableField("phone_number")
    private String phoneNumber;

    private String password;
    private String avatar;

    @TableField("user_role")
    private String userRole;

    private String status;

    @TableField(value = "ban_reason", updateStrategy = FieldStrategy.ALWAYS)
    private String banReason;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("online_status")
    private String onlineStatus;
}