package com.sky.mybatis.mybatisMultiDataSource.data.cluster;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import com.sky.mybatis.mybatis.domain.User;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.DS;

public interface StudentMapper3 {
	/*
	 * 因为会在 Service 层开启事务，所以当注解在 DAO 层时不会生效
	 * 这个ds注解不会生效
	 */
	@Transactional
	@DS("clusterDataSource")
	@Select("SELECT * FROM student WHERE student_id = #{student_id}")
	Map<String, String> getStudent(@Param("student_id") int i);
}
