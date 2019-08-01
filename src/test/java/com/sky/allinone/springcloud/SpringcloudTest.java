package com.sky.allinone.springcloud;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author joshui
 *
 */
public class SpringcloudTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RestTemplate restTemplate = new RestTemplate();
	
	@Test
	public void testTODO() {
		/*
		 * eureka:
		 * 适用范围：服务器与客户端均采用java编写。如果是其他语言，需自己编写客户端。
		 * 服务治理：目前是服务注册和服务发现
		 * 高可用：允许在分片故障期间继续提供服务；eureka服务器本身可以作为服务客户端和服务提供方，这样集群内的对象能互相发现彼此。
		 * 自我保护：如果15分钟心跳失败比率低于85%，注册中心会将当前实例注册信息保护起来，不让其过期。
		 * 客户端：很多时候，客户端既是服务提供者，也是服务消费者。
		 */
		
		/*
		 * ribbon：
		 * 使用方式：引入jar、加注解到resttemplate上，自动实现了负载均衡
		 */
		
		/*
		 * slueth：
		 * 	log变更：引入slueth后，log里会加上应用名称、spanId等信息。是通过调用MDC.put放进去的，在dispatchServlet之前就已经放进去了。
		 * 	APM工具对比：应用程序性能管理。zipkin、skywalking、CAT、Pinpoint
		 * 	zipkin：server服务器、UI控制器、
		 * 	上报方式：
		 * 		http方式（会有延迟，生产环境一般不这么用）、
		 * 		spring cloud stream方式（消息机制）：zipkin服务器可重启，不会丢消息，因为消息持久化到kafka了
		 */
	}
}
