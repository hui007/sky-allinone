package com.sky.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.spring.advancedAutoWire.AutoWireConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AutoWireConfiguration.class})
public class AdvancedAutoWireTest {
	Logger logger = LoggerFactory.getLogger(AdvancedAutoWireTest.class);
	@Autowired
	Environment env;
	
	@Test
	public void autoWire() {
		String[] activeProfiles = env.getActiveProfiles();
		logger.warn("logger: {}", activeProfiles);
	}
}
