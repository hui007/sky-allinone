package com.sky.movie;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.movie.mybatisGen.data.mapper.BookMapper;
import com.sky.movie.mybatisGen.domain.Book;
import com.sky.movie.mybatisMultiDataSource.config.ds.SampleDynamicDataSourceConfig;

/**
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class MybatisGenTest {
	@Autowired(required = false)
	BookMapper bookMapper;
	
	@Configuration
	// 难怪切面不生效
	@EnableAspectJAutoProxy
	@Import({SampleDynamicDataSourceConfig.class})
	@tk.mybatis.spring.annotation.MapperScan(
			basePackages = {"com.sky.movie.mybatisGen.data.mapper"})
	static class AppConfig {
	}
	
	@Ignore
	@Test
	public void gen() {
		List<String> warnings = new ArrayList<String>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		org.mybatis.generator.config.Configuration config = null;
		try {
			config = cp.parseConfiguration(new ClassPathResource("generatorConfig.xml").getFile());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLParserException e) {
			e.printStackTrace();
		}
		
		DefaultShellCallback callback = new DefaultShellCallback(false);
		MyBatisGenerator myBatisGenerator = null;
		try {
			myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		try {
			myBatisGenerator.generate(null);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
//	@Ignore
	@Test
	public void testMybatisGen() {
		Book book = bookMapper.selectByPrimaryKey(1);
		assertThat(book.getIntroduction()).isEqualTo("人");
	}
}
