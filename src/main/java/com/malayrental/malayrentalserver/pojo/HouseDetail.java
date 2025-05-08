package com.malayrental.malayrentalserver.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@TableName("house_detail")
public class HouseDetail {
    @TableId
    private String houseId;

    private String address;

    @TableField("lat_lng")
    private String latLng;

    @TableField(value = "`desc`")
    private String desc;

    private String tags;

    @TableField("detail_images")
    private String detailImages;

    private String floor;

    @TableField("available_date")
    private Date availableDate;

    @TableField("payment_methods")
    private String paymentMethods;

    @TableField("agency_fees")
    private BigDecimal agencyFees;

    private BigDecimal deposit;

    private String facility;

    private String community;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
} 