package com.sky.allinone.spring;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)  
@Suite.SuiteClasses({SpringAdvancedAutoWireTest.class,SpringAspectTest.class,
	SpringMvcTest.class,SpringSecurityTest.class})  
public class SpringSuiteTest {

}
