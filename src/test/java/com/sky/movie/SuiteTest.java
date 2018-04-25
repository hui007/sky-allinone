package com.sky.movie;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)  
@Suite.SuiteClasses({MybatisAloneTest.class,MybatisSpringEnableTest.class,MybatisSpringJavaConfigTest.class
	, MybatisSpringTest.class, MybatisSpringBootTest.class, MybatisSpringBootMultiDSTest.class,
	MybatisSpringBootCommonMapperTest.class, MybatisGenTest.class})  
public class SuiteTest {

}
