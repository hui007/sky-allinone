package com.sky.movie.mybatis.data;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.sky.movie.mybatis.domain.User;

//@Mapper
public interface UserMapper1 {
	// 定义新的不同于mapper xml文件里的sql语句
	@Select("SELECT * FROM user WHERE id = #{userId}")
//	@ResultType(value = User.class)
	User getUser(@Param("userId") int i);
	
	/**
	 * 分页查询
	 * @return
	 */
	@Select("SELECT * FROM user")
	List<User> getUserByPage();
	
	// 这个是直接匹配mapper xml文件里的sql语句的id
	User selectUser(@Param("userId") int i);
}
