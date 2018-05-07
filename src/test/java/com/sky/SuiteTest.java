package com.sky;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.mybatis.MybatisSuiteTest;
import com.sky.spring.SpringSuiteTest;

@RunWith(Suite.class)  
@Suite.SuiteClasses({MybatisSuiteTest.class,SpringSuiteTest.class})  
public class SuiteTest {

}
