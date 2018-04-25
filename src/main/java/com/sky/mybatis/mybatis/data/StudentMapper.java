package com.sky.mybatis.mybatis.data;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sky.mybatis.mybatis.domain.User;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.DS;

@Mapper
public interface StudentMapper {
//	@DS("clusterDataSource")
	@Select("SELECT * FROM student WHERE student_id = #{student_id}")
	Map<String, String> getStudent(@Param("student_id") int i);
}
