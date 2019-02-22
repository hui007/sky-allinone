package com.sky.allinone.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sky.allinone.dao.CustomBaseMapper;
import com.sky.allinone.dao.entity.User;

public interface UserMapper extends CustomBaseMapper<User> {
		/**
		 * 定义新的不同于mapper xml文件里的sql语句
		 * @param i
		 * @return
		 */
		@Select("SELECT * FROM user WHERE id = #{userId}")
		//@ResultType(value = User.class)
		User getUser(@Param("userId") int i);
		
		/**
		 * 分页查询：见测试用例
		 * @return
		 */
		@Select("SELECT * FROM user")
		List<User> getUserByPage();
		@Select("SELECT * FROM user order by id desc")
		List<User> getUserByPageParam(@Param("pageNumKey") int pageNum, @Param("pageSizeKey") int pageSize);
		
		/**
		 * 这个是直接匹配mapper xml文件里的sql语句的id
		 * @param i
		 * @return
		 */
		User selectUser(@Param("id") int i);
}