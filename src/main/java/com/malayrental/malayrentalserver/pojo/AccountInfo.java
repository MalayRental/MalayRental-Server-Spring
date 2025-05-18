package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("account_info")
public class AccountInfo {
    @TableId
    private String userId;

    @TableField("full_name")
    private String fullName;

    private String gender;
    private Integer age;
    private String email;
    private String school;
    private String bio;
} 