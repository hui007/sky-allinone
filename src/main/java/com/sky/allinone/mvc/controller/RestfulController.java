package com.sky.allinone.mvc.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sky.allinone.dao.entity.GradeEvent;

/**
 * 控制器直接返回客户端需要的数据表述，不经过视图名-视图渲染这个过程。
 * 使用@RestController注解后，类里所有的方法返回类型前不用加@ResponseBody注解了。
 * @author joshui
 *
 */
@RestController
@RequestMapping({ "/rest" })
public class RestfulController {
	Logger logger = LoggerFactory.getLogger(RestfulController.class);

	/**
	 * produces表明仅接受客户端accept为json的请求，也就是会返回json格式的数据。
	 * jackson默认会使用反射机制，可以使用jackson的映射注解，改变json字符串的字段名。
	 * @return
	 */
	@RequestMapping(value = "getGradeEvents", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<GradeEvent> getGradeEvents() {
		List<GradeEvent> rs = new ArrayList<>();
		
		GradeEvent event1 = new GradeEvent();
		event1.setCategory("c1");
		event1.setDate(new Date());
		event1.setEventId("1");
		GradeEvent event2 = new GradeEvent();
		event2.setCategory("c2");
		event2.setDate(new Date());
		event2.setEventId("2");
		
		rs.add(event1);
		rs.add(event2);
		
		return rs;
	}
	
	/**
	 * consumes表明仅接受content-type是json的请求，自动将json数据转换为GradeEvent对象。
	 * @param e
	 * @return
	 */
	@RequestMapping(value = "saveGradeEvent", method = RequestMethod.POST, consumes = "application/json")
	public GradeEvent saveGradeEvent(@RequestBody GradeEvent e) {
		e.setCategory("changed");
		return e;
	}
	
	/**
	 * 指定返回状态码：通过@ResponseStatus
	 * @return
	 */
	@RequestMapping(value = "gradeEventByIdStatus", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public GradeEvent gradeEventByIdStatus() {
		return null;
	}
	
	/**
	 * 指定返回状态码：通过ResponseEntity
	 * @return
	 */
	@RequestMapping(value = "gradeEventByIdEntity", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<GradeEvent> gradeEventByIdEntity() {
		GradeEvent e = null;
		ResponseEntity<GradeEvent> responseEntity = new ResponseEntity<GradeEvent>(e, HttpStatus.NOT_FOUND);
		return responseEntity;
	}
	
	/**
	 * 指定返回状态码、头部信息：通过ResponseEntity
	 * 使用UriComponentsBuilder构建返回路径
	 * @param e
	 * @return
	 */
	@RequestMapping(value = "saveGradeEventWithHeaer", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<GradeEvent> saveGradeEventWithHeaer(@RequestBody GradeEvent e,
			UriComponentsBuilder ucb) {
		e.setCategory("changed");
		
		HttpHeaders header = new HttpHeaders();
		URI uri = ucb.path("/rest/getGradeEvent/").path(e.getEventId()).build().toUri();
		header.setLocation(uri);
		
		ResponseEntity<GradeEvent> responseEntity = new ResponseEntity<GradeEvent>(e, header, HttpStatus.CREATED);
		return responseEntity;
	}
	
	@RequestMapping(value = "aplipay/app/async/pay", method = RequestMethod.POST)
	public String alipayAppCallback(HttpServletRequest request, HttpServletResponse response) {
		Map requestParams = request.getParameterMap();
		logger.info("接收到支付宝app支付异步通知。requestParams：{}", requestParams);
		
		Map<String,String> transParams = new HashMap<String,String>();
		// 继续校验
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		    String name = (String) iter.next();
		    String[] values = (String[]) requestParams.get(name);
		    String valueStr = "";
		    for (int i = 0; i < values.length; i++) {
		        valueStr = (i == values.length - 1) ? valueStr + values[i]
		                    : valueStr + values[i] + ",";
		  	}
		    // 乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			transParams.put(name, valueStr);
		}
		logger.info("接收到支付宝app支付异步通知。transParams：{}", transParams);
		
		return "success";
	}
	
	@RequestMapping(value = "wx/app/async/pay", method = RequestMethod.POST, consumes = "text/xml")
	public String wxAppCallback(@RequestBody String xml) {
		logger.info("接收到微信app支付异步通知。xmlStr：{}", xml);
		
		return "success";
	}
	
	@RequestMapping(value = "wx/app/async/pay2", method = RequestMethod.POST)
	public String wxAppCallback2(HttpServletRequest request, HttpServletResponse response) {
		logger.info("接收到微信app支付异步通知2。xmlStr：{}");
		
		return "success";
	}
	
	@RequestMapping(value = "wx/app/async/pay3", method = RequestMethod.POST)
	public String wxAppCallback3(@RequestBody String xml, HttpServletRequest request, HttpServletResponse response) {
		logger.info("接收到微信app支付异步通知2。xmlStr：{}");
		
		return "success";
	}
}
