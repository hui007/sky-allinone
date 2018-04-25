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
package com.sky.movie.mybatisSpringBootCommonMapper.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.movie.mybatis.data.StudentMapper;
import com.sky.movie.mybatisMultiDataSource.config.ds.DS;
import com.sky.movie.mybatisSpringBootCommonMapper.data.GradeEventMapper;
import com.sky.movie.mybatisSpringBootCommonMapper.domain.GradeEvent;

@Service
public class CommonMapperService {

	@Autowired(required = false)
	private GradeEventMapper gradeEventMapper;

	@DS("clusterDataSource")
	// @Transactional
	public List<GradeEvent> doSomeBusinessStuff() {
		List<GradeEvent> selectAll = gradeEventMapper.selectAll();
		return selectAll;
	}

}
