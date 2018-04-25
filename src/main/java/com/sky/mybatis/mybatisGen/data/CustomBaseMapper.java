package com.sky.mybatis.mybatisGen.data;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import tk.mybatis.mapper.common.MySqlMapper;

// 有相同CustomBaseMapper，要起个不同的名字，否则tk包会报错
@Component("customBaseMapperGen")
public interface CustomBaseMapper<T> extends tk.mybatis.mapper.common.BaseMapper<T>,MySqlMapper<T> {

}
