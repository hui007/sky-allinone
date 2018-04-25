package com.sky.movie.mybatisGen.data;

import tk.mybatis.mapper.common.MySqlMapper;

public interface CustomBaseMapper<T> extends tk.mybatis.mapper.common.BaseMapper<T>,MySqlMapper<T> {

}
