package com.sky.allinone.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.elasticsearch.service.ElasticsearchService;

/**
 * git：
 * https://github.com/spring-projects/spring-data-elasticsearch/tree/3.2.0.M1
 * https://github.com/SpringDataElasticsearchDevs/spring-data-elasticsearch-sample-application
 * 
 * 版本兼容：
 * Spring Boot 1.x版本 -> Elastisearch版本为2.x
 * Spring Boot 2.x版本 -> 默认的ES版本为5.6.9
 * 
 * Java技术栈目前有三种可以选择 Node Client, Transport Client, Rest API, 需要注明的是，官方已经标明NodeClient 已经过期，Transport Client 将在7.x版本开始不再支持， 最终会在7.x 统一到Rest API。目前Transport Client使用范围比较广；Rest API方式兼容性较好；除非在In-memory模式下运行单元测试，否则不推荐NodeClient
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTest {
	@Autowired(required = false)
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private ElasticsearchService service;
	
	@Test
	public void testExists() {
		assertThat(elasticsearchTemplate).isNull();
		assertThat(service).isNotNull();
	}
	
	/**
	 * 测试spring根据repository接口自动生成的repository代理
	 * 会自动生成很多常用的查询方法
	 * 
	 * 需要放开：
	 * SkyAllinoneApplication里的@EnableElasticsearchRepositories、exclude
	 * CustomerRepository里的@NoRepositoryBean
	 * pom里的spring-boot-starter-data-elasticsearch
	 */
	@Test
	public void testRepository() {
		
	}
	
	/**
	 * 不使用repository，使用template，手动操作
	 */
	@Test
	public void testTemplate() {
		
	}
	
}
