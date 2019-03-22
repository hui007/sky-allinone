package com.sky.allinone.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.common.collect.MapBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.elasticsearch.entity.Customer;
import com.sky.allinone.elasticsearch.repositories.CustomerRepository;
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
 * Java技术栈目前有三种可以选择 Node Client（嵌入式节点客户端）, Transport Client（通过连接的客户端）, Rest API, 需要注明的是，官方已经标明NodeClient 已经过期，Transport Client 将在7.x版本开始不再支持， 最终会在7.x 统一到Rest API。目前Transport Client使用范围比较广；Rest API方式兼容性较好；除非在In-memory模式下运行单元测试，否则不推荐NodeClient
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTest {
	@Autowired(required = false)
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
    private CustomerRepository repository;
	@Autowired
	private ElasticsearchService service;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void testExists() {
		assertThat(elasticsearchTemplate).isNotNull();
		assertThat(repository).isNotNull();
		assertThat(service).isNotNull();
	}
	
	/**
	 * 测试spring根据repository接口自动生成的repository代理
	 * 会自动生成很多常用的查询方法
	 * 
	 * 需要放开：
	 * SkyAllinoneApplication里的@EnableElasticsearchRepositories、exclude
	 * CustomerRepository里的@NoRepositoryBean
	 * pom里的spring-boot-starter-data-elasticsearch放开，去掉pom里的jest
	 * application.yml里的elasticsearch服务器配置
	 */
	@Test
	public void testRepository() {
		// 新增。同时也会自动创建索引，即使改变索引的分片和副本数，再执行本test方法，在es里也不会再改变索引的分片和副本数了
		Customer insert = repository.save(new Customer("Alice", "Smith"));
		logger.info("Customer-create:{}", insert.getId());
		assertThat(insert.getId()).isNotNull(); // 此时Customer对象的id字段不为空，但是es里的source id字段是空的
		
		// 查询
		Customer select = repository.findOne(insert.getId());
		logger.info("Customer-select:{}", select.getId());
		assertThat(select.getId()).isNotNull();
		assertEquals("新增后返回的文档对象和查询出的文档对象ID不一致", insert.getId(), select.getId());
		
		List<Customer> findByFirstName = repository.findByFirstName("Alice");
		assertThat(findByFirstName).isNotNull();
		
		// 修改
		insert.setFirstName("Alice-update");
		Customer update = repository.save(insert);
		assertThat(update.getId()).isNotNull();
		assertThat(update.getId()).isEqualTo(insert.getId());
		assertThat(update.getId()).isEqualTo(select.getId());
		
		// 删除文档
//		repository.delete(update);
//		repository.delete(findByFirstName); // 批量删除
		
	}
	
	/**
	 * 不使用repository，使用template，手动操作
	 */
	@Test
	public void testTemplate() {
		String indexName = "testtemplate_index";
		
		// 创建索引
		boolean indexExists = elasticsearchTemplate.indexExists(indexName);
		Map<String, String> map = new MapBuilder<String, String>()
			.put("index.number_of_shards", "2")
			.put("index.number_of_replicas", "2")
			.put("index.refresh_interval", "-1")
//			.put("index.store.type", "testTemplate")
			.map();
		if (!indexExists) {
			elasticsearchTemplate.createIndex(indexName, map); // 索引名不能大写
		}
		
		// 获取索引信息
		Map setting = elasticsearchTemplate.getSetting(indexName);
		assertThat(setting.get("index.number_of_shards")).isEqualTo("2");
		assertThat(setting.get("index.number_of_replicas")).isEqualTo("2");
		assertThat(setting.get("index.refresh_interval")).isEqualTo("-1");
		
		// 更新索引。比如更新索引的副本数量，暂时没找到更新索引的api，不过可以通过rest接口更新
		// curl -H "Content-Type:application/json" -X PUT -d '{"number_of_replicas": "3"}' http://localhost:9200/testtemplate_index/_settings
		
		// 删除索引。这个索引里还有文档，也能删除这个索引，是不是某些配置没打开？
		boolean deleteIndex = elasticsearchTemplate.deleteIndex(indexName); 
		assertThat(deleteIndex).isEqualTo(true);
		
	}
	
	/**
	 * 使用jest方式操作es
	 * 
	 * 需要放开：
	 * SkyAllinoneApplication里的JestAutoConfiguration
	 * pom里的jest
	 */
	@Test
	public void testJest() {
		
	}
	
}
