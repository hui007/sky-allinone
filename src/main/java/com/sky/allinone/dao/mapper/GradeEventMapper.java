package com.sky.allinone.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sky.allinone.conf.datasource.DynamicDataSourceName;
import com.sky.allinone.dao.CustomBaseMapper;
import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.dao.entity.User;

public interface GradeEventMapper  extends CustomBaseMapper<GradeEvent>{
	@DynamicDataSourceName("clusterDataSource")
	@Select("select * from grade_event order by event_id desc")
	List<GradeEvent> getGradeEventByPageParam(@Param("pageNumKey") int pageNum, @Param("pageSizeKey") int pageSize);
}
