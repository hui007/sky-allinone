package com.sky.allinone;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.allinone.mybatis.MybatisSuiteTest;
import com.sky.allinone.spring.SpringSuiteTest;

@RunWith(Suite.class)  
@Suite.SuiteClasses({MybatisSuiteTest.class,SpringSuiteTest.class})  
public class SuiteTest {

}