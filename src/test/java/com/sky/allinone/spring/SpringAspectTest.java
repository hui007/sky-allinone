package com.sky.allinone.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class SpringAspectTest {
	Logger logger = LoggerFactory.getLogger(SpringAspectTest.class);
	@Autowired
	Environment env;
	
	@Test
	public void aspect() {
		logger.info("不具体测试aspect了，参见代码：{}", "com.sky.allinone.aspect");
	}
}
