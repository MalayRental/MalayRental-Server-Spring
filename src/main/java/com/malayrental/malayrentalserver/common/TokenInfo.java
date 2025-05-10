package com.malayrental.malayrentalserver.common;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record TokenInfo(
    String token,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    LocalDateTime expired
) {} 