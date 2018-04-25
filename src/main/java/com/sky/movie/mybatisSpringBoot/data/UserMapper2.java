package com.sky.movie.mybatisSpringBoot.data;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sky.movie.mybatis.domain.User;

@Mapper
public interface UserMapper2 {
	// 定义新的不同于mapper xml文件里的sql语句
	@Select("SELECT * FROM user WHERE id = #{userId}")
//	@ResultType(value = User.class)
	User getUser(@Param("userId") int i);
}
