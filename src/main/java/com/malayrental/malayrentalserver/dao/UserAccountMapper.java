package com.malayrental.malayrentalserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}