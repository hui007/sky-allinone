package com.sky.allinone.mvc.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
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
	
	/**
	 * 校验入参：使用javax的校验api，然后通过springboot引入这个api的实现hibernate validate
	 * 可以通过配置文件配置校验不通过的message信息，也可以做分组校验（group）
	 * @param gradeEvent
	 * @param errors
	 * @return
	 */
	@RequestMapping(value="testValidate", method=POST) 
	public String validate(@Validated GradeEvent gradeEvent, Errors errors) {
//		errors.getAllErrors().stream().forEach(new Consumer<ObjectError>() {
//
//			@Override
//			public void accept(ObjectError t) {
//				logger.warn("getObjectName:{}, getDefaultMessage:{}", t.getObjectName(), t.getDefaultMessage());
//			}
//		});
		errors.getAllErrors().stream().forEach(t -> logger.warn("getObjectName:{}, getDefaultMessage:{}", t.getObjectName(), t.getDefaultMessage()));
		
		if (errors.hasErrors()) {
			return "saveGradeEvent";
		}
		
		return "success";
	}
}
