package com.sky.allinone.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.advancedAutoWire.MagicBean;
import com.sky.allinone.advancedAutoWire.MagicQualifier;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("dev")
public class SpringAdvancedAutoWireTest {
	Logger logger = LoggerFactory.getLogger(SpringAdvancedAutoWireTest.class);
	@Autowired
	Environment env;
	@Autowired(required = false)
	@Qualifier("embeddedDS")
	DataSource testDs;
	@Autowired(required = false)
	@Qualifier("productDS")
	DataSource proDs;
	@Autowired(required = false)
	MagicBean magicBean1;
	@Autowired(required = false)
	MagicBean magicBean2;
	@Autowired(required = false)
	@MagicQualifier
	MagicBean magicBeanQualifier1;
	@Autowired(required = false)
	@MagicQualifier
	MagicBean magicBeanQualifier2;
	
	@Value("${magic.int}")
	private int magicInt;
	@Value("#{systemProperties['magic']}") // spel表达式，系统属性
	private int magicSystemProperty; 
	@Value("#{magicBeanQualifier.name}") // spel表达式
	private String magicBeanQualifierName; 
	@Value("#{magicBeanQualifier.name?.toUpperCase()}") // spel表达式
	private String magicBeanQualifierNameUpper; 
	@Value("#{magicBeanQualifier.getCount()}") // spel表达式
	private AtomicInteger magicBeanQualifierCount; 
	@Value("#{T(java.lang.Math).PI}") // spel表达式
	private double pi; 
	@Value("#{T(java.lang.Math).random()}") // spel表达式
	private double random; 
	// spel还能对集合类做很多操作，这里不一一测试了
	
	@BeforeClass
	public static void init() {
		System.setProperty("magic", "1");
	}
	
	@Test
	public void autoWire() {
		String[] activeProfiles = env.getActiveProfiles();
		logger.warn("激活的profile: {}", activeProfiles);
		
		assertNull(proDs);
		assertNotNull(testDs);
		assertNotNull(magicBean1);
		assertThat(magicBeanQualifier1.getName()).isEqualTo("magicBeanQualifier");
		assertThat(magicBeanQualifier1).isEqualTo(magicBeanQualifier2);
		assertThat(magicBean1).isNotEqualTo(magicBean2);
		assertThat(magicBean1.getCount().get()).isNotEqualTo(1);
	}
	
	@Test
	public void testProperties() {
		Integer intP = env.getProperty("magic.int", Integer.class);
		assertThat(intP.intValue()).isEqualTo(100);
		assertThat(env.getProperty("magic.String")).isEqualTo("中国人");
		assertThat(magicInt).isEqualTo(100);
		
		assertThat(magicSystemProperty).isEqualTo(1);
		assertThat(magicBeanQualifierName).isEqualTo("magicBeanQualifier");
		assertThat(magicBeanQualifierNameUpper).isEqualTo("magicBeanQualifier".toUpperCase());
		assertThat(magicBeanQualifierCount.get()).isNotEqualTo(1);
	}
}
