package com.sky.spring.mvc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.mybatis.mybatisSpringBootCommonMapper.domain.GradeEvent;
import com.sky.mybatis.mybatisSpringBootCommonMapper.service.CommonMapperService;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping({"/", "/homepage"})
public class HomeController {
	@Autowired
	private CommonMapperService commonMapperService;
	
	/**
	 * 拦截根目录，这样就没办法通过浏览器访问static下的静态资源了
	 * @return
	 */
	@RequestMapping(method=GET) 
	public String home() {
		return "home";
	}
	
	/**
	 * @param model 本质上也是一个map，可以直接使用Map model形参替换
	 * @return
	 */
	@RequestMapping(value="getGradeEvents", method=GET) 
	public String getGradeEvent(Model model) {
		// 也可以不指定key，key将会被自动推断出来。List<GradeEvent>将会被推断为gradeEventList
		model.addAttribute("gradeEvents", commonMapperService.doSomeBusinessStuff());
		return "getGradeEvents";
	}
	
	/**
	 * 视图名字也会根据请求路径自动推断出来为getGradeEventsList
	 * @return 这个list会自动放入model，并自动推断出名字为gradeEventList
	 */
	@RequestMapping(value="getGradeEventsList", method=GET) 
	public List<GradeEvent> getGradeEvent() {
		return commonMapperService.doSomeBusinessStuff();
	}
	
	@RequestMapping(value="findGradeEventsList", method=GET) 
	public String findGradeEvent(Model model, @RequestParam(value="name", defaultValue="sky") String name, 
			@RequestParam("count") int count) {
		model.addAttribute("gradeEvents", commonMapperService.doSomeBusinessStuff());
		model.addAttribute("name", name);
		model.addAttribute("count", count);
		return "findGradeEvent";
	}
	
	/**
	 * 
	 * @param id 因为url里的id占位符和方法的形成名字一样，可以去掉@PathVariable里的"id"字段
	 * @return
	 */
	@RequestMapping(value="findByid/{id}", method=GET) 
	public String findGradeEventById(@PathVariable("id") int id) {
		return "findGradeEventById";
	}
	
	/**
	 * 处理日期字段的几种方式：
	 * @InitBinder注解，为日期类型添加处理类；
	 * 在类的日期字段上绑定@DateTimeFormat注解；
	 * 使用扩展接口HandlerMethodArgumentResolver，并添加到WebMvcConfigurerAdapter的addArgumentResolvers方法；
	 * 如果是json字符串，则需要在类的日期字段上使用Jackson的注解@JsonDeserialize
	 * @param gradeEvent
	 * @return
	 */
	@RequestMapping(value="saveGradeEvent", method=POST) 
	public String saveGradeEvent(GradeEvent gradeEvent) {
		return "redirect:/showGradeEventInfo/" + gradeEvent.getEventId();
	}
	
	/**
	 * 校验入参：使用javax的校验api，然后通过springboot引入这个api的实现hibernate validate
	 * 可以通过配置文件配置校验不通过的message信息，也可以做分组校验（group）
	 * @param gradeEvent
	 * @param errors
	 * @return
	 */
	@RequestMapping(value="testValidate", method=POST) 
	public String testValidate(@Valid GradeEvent gradeEvent, Errors errors) {
		if (errors.hasErrors()) {
			return "saveGradeEvent";
		}
		
		return "redirect:/showGradeEventInfo/" + gradeEvent.getEventId();
	}
}
