package com.example.finance.mapper;

import com.example.finance.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("SELECT id, username, password, real_name, role, status FROM t_user WHERE username = #{username}")
    User findByUsername(String username);
}