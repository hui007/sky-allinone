package com.sky.allinone.mvc.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.InternalResourceView;

import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.mvc.exception.CommonException;
import com.sky.allinone.mvc.exception.MethodException;
import com.sky.allinone.mvc.exception.NotAccepableException;
import com.sky.allinone.service.CommonMapperService;

@Controller
@RequestMapping({"/", "/homepage"})
public class HomeController {
	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private CommonMapperService commonMapperService;
	
	/**
	 * 拦截根目录，这样就没办法通过浏览器访问static下的静态资源了;
	 * 也导致访问根目录时，不能自动跳转到index.html页面了
	 * 
	 * 也可以通过spring Security来做安全过滤
	 * @return
	 */
	@RequestMapping( method=GET) 
	public String home() {
		return "home";
	}
	
	/**
	 * 配置spring Security跳转到登录页面
	 * 为了防止CSRF攻击，登录页面登录时必须带上csrf token，当然也可以配置spring Security去掉csrf功能
	 * 参考DefaultLoginPageGeneratingFilter，看怎么生成默认登录页面的，见loginDefault.html
	 * @param model
	 * @param request
	 * @return
	 */
	@GetMapping(path = "/showLoginPage")
	public String showLoginPage(@RequestParam(value="myError", required = false) String error, 
				Model model, HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

		if (token != null) {
			model.addAttribute("csrfKey", token.getParameterName());
			model.addAttribute("csrfValue", token.getToken());
		}
		
		String viewName = "login";
//		String viewName = "loginDefault";
		return viewName;
	}
	
	@PostMapping(path = "/showLoginPagePost")
	public String showLoginPagePost(@RequestParam(value="myError", required = false) String error, 
			Model model, HttpServletRequest request) {
		
		String viewName = "login";
		return viewName;
	}
	
//	@PostMapping(path = "/doLogin")
	@RequestMapping(value = "/doLogin", method = {RequestMethod.GET, RequestMethod.POST})
	public  /*ModelAndView*/ String login(@RequestParam(value="username") String userName, @RequestParam(value="password") String pw,
			@RequestParam(value="error", required = false) String error) {
		String viewName = "welcomePage";
		if (error != null) {
			viewName = "loginFailure";
        }
		
//		ModelAndView modelAndView = new ModelAndView(new InternalResourceView("/login.html"));
		return "redirect:/" + viewName; // 这里一定要使用redirect，如果不用的话，默认是post方式，使用post方式访问html静态页面时，会在ResourceHttpRequestHandler里报错“405 request method post not supported”
	}
	
	/**
	 * path不要和返回值一样，否则会报循环引用的问题。
	 * javax.servlet.ServletException: Circular view path [welcome.html]: would dispatch back to the current handler URL [/welcome.html] again. Check your ViewResolver setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)
	 * @return
	 */
	@GetMapping(path = "/welcomePage")
	public String welcome() {
		String viewName = "welcome";
		
		return viewName;
	}
	
	/**
	 * @param model 本质上也是一个map，可以直接使用Map model形参替换
	 * @return
	 */
	@RequestMapping(value="getGradeEvents", method=GET) 
	public String getGradeEvent(Model model) {
		// 也可以不指定key，key将会被自动推断出来。List<GradeEvent>将会被推断为gradeEventList
		model.addAttribute("gradeEvents", commonMapperService.selectGradeEvents());
		return "getGradeEvents";
	}
	
	/**
	 * 视图名字也会根据请求路径自动推断出来为getGradeEventsList
	 * @return 这个list会自动放入model，并自动推断出名字为gradeEventList
	 */
	@RequestMapping(value="getGradeEventsList", method=GET) 
	public List<GradeEvent> getGradeEvent() {
		return commonMapperService.selectGradeEvents();
	}
	
	@RequestMapping(value="findGradeEventsList", method=GET) 
	public String findGradeEvent(Model model, @RequestParam(value="name", defaultValue="sky") String name, 
			@RequestParam("count") int count) {
		model.addAttribute("gradeEvents", commonMapperService.selectGradeEvents());
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
		
		return "redirect:/showGradeEventInfo/" + gradeEvent.getEventId();
	}
	
	/**
	 * 也可以使用@requestPart注解拿到byte[]，不过这种方式比较原始；
	 * 在处理使用代码段传递过来的文件时，这个注解比较有用，如@RequestPart("meta-data") MetaData metadata, @RequestPart("file-data") MultipartFile file
	 * @param file
	 * @param copyToPath
	 * @param request
	 * @return
	 */
	@PostMapping(path="fileUpload")
	public String fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("copyToPath") String copyToPath, HttpServletRequest request) {
		try {
			file.transferTo(new File(copyToPath + File.separator + file.getOriginalFilename()));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return "uploadSuccess";
	}
	
	/**
	 * 处理异常的几种方式：
	 * 特定的spring异常会映射会指定的http状态码；
	 * 异常上添加@ResponseStatus，映射为http状态码；
	 * 在方法上添加@ExceptionHandler，用来处理异常
	 * @return
	 */
	@GetMapping(path = "handlerException", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String handlerException(@RequestParam Map<String, String> params) {
		if (params.containsKey("throwNotAccepableException")) {
			throw new NotAccepableException();
		}
		if (params.containsKey("throwMethodException")) {
			throw new MethodException();
		}
		if (params.containsKey("throwCommonException")) {
			throw new CommonException();
		}
		
		return "handlerSuccess";
	}
	
	@ExceptionHandler(MethodException.class)
	public String handlerMethodException() {
		
		return "dealedMethodException";
	}
	
	/**
	 * 本来想用Model传递过去的，结果报错，只能使用RedirectAttributes来传递了
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping(path = "redirectBefore")
	public String redirectBefore(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("from", "sky");
		model.addAttribute("to", "you");
		
		redirectAttributes.addAttribute("from", "sky");
		redirectAttributes.addAttribute("to", "you");
		redirectAttributes.addFlashAttribute(new GradeEvent()); //flash属性可以存放复杂对象，这个复杂对象会自动放入session中
		
		// to参数会作为查询参数传递过去 ?to=you
		return "redirect:/redirectAfter/{from}";
	}
	
	@GetMapping(path = "redirectAfter/{from}")
//	@RequestMapping(value="redirectAfter/{from}", method=GET) 
	public String redirectAfter(@PathVariable String from, @RequestParam String to) {
		logger.info("from:{}, to:{}", from, to);
		
		return "redirectAfter";
	}
}
