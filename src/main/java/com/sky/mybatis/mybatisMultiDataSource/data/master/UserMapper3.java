package com.sky.mybatis.mybatisMultiDataSource.data.master;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.sky.mybatis.mybatis.domain.User;

@Mapper
public interface UserMapper3 {
	// 定义新的不同于mapper xml文件里的sql语句
	@Select("SELECT * FROM user WHERE id = #{userId}")
//	@ResultType(value = User.class)
	User getUser(@Param("userId") int i);
	
	// 这个是直接匹配mapper xml文件里的sql语句的id
	User selectUser(@Param("userId") int i);
}
