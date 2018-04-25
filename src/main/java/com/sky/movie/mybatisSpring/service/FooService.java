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
package com.sky.movie.mybatisSpring.service;

import org.springframework.transaction.annotation.Transactional;

import com.sky.movie.mybatis.data.UserMapper1;
import com.sky.movie.mybatis.domain.User;

/**
 * FooService simply receives a userId and uses a mapper to get a record from the database.
 */
@Transactional
public class FooService {

  private final UserMapper1 userMapper;

  public FooService(UserMapper1 userMapper) {
    this.userMapper = userMapper;
  }

  public User doSomeBusinessStuff(int userId) {
    return this.userMapper.getUser(userId);
  }

}
