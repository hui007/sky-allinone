/**
 *    Copyright 2010-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sky.allinone.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.allinone.conf.datasource.DynamicDataSourceName;
import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.dao.entity.User;
import com.sky.allinone.dao.mapper.GradeEventMapper;
import com.sky.allinone.dao.mapper.UserMapper;

@Service
public class CommonMapperService {

	@Autowired
	private GradeEventMapper gradeEventMapper;
	@Autowired
	private UserMapper userMapper;

	@DynamicDataSourceName("clusterDataSource")
	// @Transactional
	public List<GradeEvent> selectGradeEvents() {
		List<GradeEvent> selectAll = gradeEventMapper.selectAll();
		return selectAll;
	}
	
//	@DS("masterDataSource")
	// @Transactional
	public List<User> selectUsers() {
		List<User> selectAll = userMapper.selectAll();
		return selectAll;
	}
	
	public List<User> findByCustomizeSQl() {
		User u1 = userMapper.getUser(1);
		User u2 = userMapper.selectUser(1);
		
		return Arrays.asList(u1, u2);
	}

}
