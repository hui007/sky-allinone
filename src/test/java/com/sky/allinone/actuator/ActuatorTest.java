package com.sky.allinone.actuator;

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
 * 需要先启动远程应用
 * @author joshui
 *
 */
public class ActuatorTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private RestTemplate restTemplate = new RestTemplate();
	
	/**
	 * 登录，通过spring Security
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	@SuppressWarnings("unused")
	@Test
	public void testLogin() throws JsonProcessingException, IOException {
		// 先登录远程应用，主要目的是获取cookie
		String url = "http://localhost:8081/login";
		HttpHeaders loginHeaders = new HttpHeaders();
		loginHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> loginMap = new LinkedMultiValueMap<String, String>();
		loginMap.add("username", "user");
		loginMap.add("password", "userpw");

		HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<MultiValueMap<String, String>>(loginMap, loginHeaders);
		ResponseEntity<Object> loginResponse = restTemplate.postForEntity(url, loginRequest, Object.class);
		logger.info("登录返回的ResponseEntity：{}", loginResponse.toString());
		
		HttpHeaders loginHeadersResp = loginResponse.getHeaders();
		Object body = loginResponse.getBody();
		assertThat(body).isNull();
		HttpStatus statusCode = loginResponse.getStatusCode();
		assertThat(statusCode).isEqualTo(HttpStatus.FOUND);
		
		String cookies = loginHeadersResp.getFirst(HttpHeaders.SET_COOKIE);
		assertThat(StringUtils.contains(cookies, "JSESSIONID")).isTrue();
		List<String> cookieList = loginHeadersResp.get(HttpHeaders.SET_COOKIE);
		logger.info("登录返回的cookies String：{}。返回的cookies list: {}", cookies, cookieList);
		
		// 获取actuator端点信息，带上登录成功后，获取到的cookie
		HttpHeaders actuatorHeaders = new HttpHeaders();
		actuatorHeaders.set(HttpHeaders.COOKIE, cookies);
		HttpEntity<String> actuatorHttpEntity = new HttpEntity<String>(actuatorHeaders);
		String actuatorUrl = "http://localhost:8081/manage/{type}";
		String acuatorType = "beans";
		ResponseEntity<String> actuatorResponse = restTemplate.exchange(actuatorUrl, HttpMethod.GET, actuatorHttpEntity, String.class, acuatorType);
		logger.info("获取端点{}的信息为:{}", acuatorType, actuatorResponse.getBody());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actuatorBody = mapper.readTree(actuatorResponse.getBody());
		assertThat(actuatorBody.isArray()).isTrue();
		JsonNode beansJson = actuatorBody.get(0).get("beans");
		assertThat(beansJson.isArray()).isTrue();
		
		boolean founded = false;
		for (int i = 0; i < beansJson.size(); i++) {
			if (beansJson.get(i).get("bean").asText().equals("skyAllinoneApplication")) {
				founded = true;
				break;
			}
		}
		assertThat(founded).isTrue();
	}
}
