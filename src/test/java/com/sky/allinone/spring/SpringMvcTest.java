package com.sky.allinone.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.sky.allinone.dao.conf.DynamicDataSourceConfig;
import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.mvc.conf.WebConfig;
import com.sky.allinone.mvc.controller.FileController;
import com.sky.allinone.mvc.controller.HomeController;
import com.sky.allinone.service.CommonMapperService;
import com.sky.allinone.service.MethodSecurityService;

@RunWith(SpringRunner.class)
// 跑这个用例前，先去掉SkyAllinoneApplication里的@EnableElasticsearchRepositories，或者给所有的es elastic repository接口加上@NoRepositoryBean注解， 否则会出现elasticTemplate实例化不了的错误
@WebMvcTest({HomeController.class, FileController.class})
@Import({WebConfig.class, DynamicDataSourceConfig.class})
public class SpringMvcTest {
	Logger logger = LoggerFactory.getLogger(SpringMvcTest.class);
	@Autowired
    private MockMvc mvc;
	@MockBean
	private CommonMapperService commonMapperService;
	@MockBean
	private MethodSecurityService methodSecurityService;
	@Autowired
	private Environment env;
	@Autowired
	MultipartProperties multipartProperties;
	RestTemplate restTemplate = new RestTemplate();
	
	@BeforeClass
	public static void init() {
		// 去掉spring Security
		System.setProperty("security.basic.authorize-mode", "none");
	}
	
	/**
	 * 这个测试方法，可以完全不使用SpringMvcTest类上的注解
	 * @throws Exception
	 */
	@Test
	public void testMvc() throws Exception {
		HomeController homeController = new HomeController();
		MockMvc mockMvc = standaloneSetup(homeController).build();
		
		mockMvc.perform(get("/")).andExpect(view().name("home"));
		mockMvc.perform(get("/homepage")).andExpect(view().name("home"));
	}
	
	@Test
	public void testService() throws Exception {
		given(this.commonMapperService.selectGradeEvents())
        	.willReturn(Arrays.asList(new GradeEvent(), new GradeEvent()));
		
		mvc.perform(get("/getGradeEvents"))
			.andExpect(view().name("getGradeEvents"))
			.andExpect(model().attributeExists("gradeEvents"))
			.andExpect(model().attribute("gradeEvents", hasItem(commonMapperService.selectGradeEvents().get(0))))
			.andExpect(model().attribute("gradeEvents", hasItems(commonMapperService.selectGradeEvents().toArray())));
		
		mvc.perform(get("/getGradeEventsList"))
			.andExpect(view().name("getGradeEventsList"))
			.andExpect(model().attributeExists("gradeEventList"))
			.andExpect(model().attribute("gradeEventList", hasItem(commonMapperService.selectGradeEvents().get(0))))
			.andExpect(model().attribute("gradeEventList", hasItems(commonMapperService.selectGradeEvents().toArray())));
		
		mvc.perform(get("/findGradeEventsList?count=10"))
			.andExpect(view().name("findGradeEvent"))
			.andExpect(model().attributeExists("name", "count"))
			.andExpect(model().attribute("name", equalTo("sky")))
			.andExpect(model().attribute("count", equalTo(10)));
		
		mvc.perform(get("/findByid/2"))
			.andExpect(request().attribute("id", equalTo(2)));
		
		mvc.perform(post("/saveGradeEvent")
					.param("eventId", "1")
					.param("date", "20180428")
				)
//			.andExpect(model().attributeExists("gradeEvent"))
			.andExpect(status().is3xxRedirection())
			.andExpect(status().is(302))
			.andExpect(redirectedUrl("/showGradeEventInfo/1"));
	}
	
	/**
	 * with(csrf())是为了通过spring security
	 * @throws Exception
	 */
	@Test
	public void testValidate() throws Exception {
		logger.warn("javax.validation.constraints.Size.message", env.getProperty("javax.validation.constraints.Size.message"));
		mvc.perform(post("/testValidate").with(csrf()).param("eventId", "1").param("date", "20180428"))
//			.andExpect(forwardedUrl("/saveGradeEvent.html"));
			.andExpect(forwardedUrl("saveGradeEvent.html"));
			
	}
	
	/**
	 * 怎么测试servlet？
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testServlet() throws Exception {
//		mvc.perform(get("/CarServlet"));
		restTemplate.getForEntity("http://localhost:8080/CarServlet", Object.class);
	}
	
	@Test
	public void testUpload() throws Exception {
		String name = "file"; // 这个一定要写成file，否则会报400 bad request错误。这是因为spring自身的异常，被自动转化为相应的http状态码了
		String originalFilename = "测试文件上传.txt";
		String contentType = ",multipart/form-data";
		byte[] contentStream = "文件内容abcd".getBytes("utf-8");
		String copyToPath = "/Users/jianghui/Downloads/temp-del/cachecloud-web";
		mvc.perform(fileUpload("/fileUpload").file(new MockMultipartFile(name, originalFilename, contentType, contentStream))
				.param("copyToPath", copyToPath))
			.andExpect(status().isOk());
		
		assertThat(new File(copyToPath + File.separator + originalFilename).exists()).isTrue();
		// 上传临时目录里的文件，貌似临时文件会被自动删除的？
		assertThat(new File(multipartProperties.getLocation() + File.separator + originalFilename).exists()).isFalse();
	}
	
	@Test
//	@WithMockUser
	public void testHandlerException() throws Exception {
		// 特定的spring异常会自动映射到指定的http状态码
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is(415));
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		mvc.perform(post("/handlerException").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()));
		
		// 自定义异常
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwNotAccepableException", "1"))
			.andExpect(status().isNotAcceptable());
		
		// 使用方法统一处理异常
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwMethodException", "1"))
			.andExpect(view().name("dealedMethodException"));
		
		// 测试ControllerAdvice
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwCommonException", "1"))
			.andExpect(view().name("dealedCommonException"));
		
	}
	
	@Test
	public void testRedirectPassParam() throws Exception {
		mvc.perform(get("/redirectBefore"))
			.andExpect(status().is3xxRedirection());
	}
	
	/**
	 * 测试生成PDF-普通文本
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	@SuppressWarnings("unused")
	public void testPDFText() throws FileNotFoundException, DocumentException {
		 //第一步，创建一个 iTextSharp.text.Document对象的实例：  
        Document document = new Document();  
        //第二步，为该Document创建一个Writer实例：  
		String destUrl = "/Users/jianghui/Downloads/temp-del/测试生成PDF-text.pdf";
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destUrl));  
        //第三步，打开当前Document  
        document.open();  
        //第四步，为当前Document添加内容；  
        document.add(new Paragraph("Hello World"));    
        //第五步，关闭Document  
        document.close(); 
        
        assertThat(new File(destUrl).exists()).isTrue();
	}
	
	/**
	 * 测试生成PDF-从html文件生成pdf
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	@Test
	public void testPDFHtml() throws DocumentException, IOException {
		//第一步，创建一个 iTextSharp.text.Document对象的实例：  
        Document document = new Document();  
        //第二步，为该Document创建一个Writer实例：  
        String destUrl = "/Users/jianghui/Downloads/temp-del/测试生成PDF-html.pdf";
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destUrl));  
        //第三步，打开当前Document  
        document.open();  
        //第四步，为当前Document添加内容：  
        //document.add(new Paragraph("Hello World"));  
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ClassPathResource("static/pdf.html").getInputStream());  
        //第五步，关闭Document  
        document.close();  
        
        assertThat(new File(destUrl).exists()).isTrue();
	}
	
	/**
	 * 测试生成PDF-从html字符串生成pdf
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	@Test
	public void testPDFHtmlStr() throws DocumentException, IOException {
		Document document = new Document();  
        String destUrl = "/Users/jianghui/Downloads/temp-del/测试生成PDF-htmlStr.pdf";
		PdfWriter mPdfWriter = PdfWriter.getInstance(document, new FileOutputStream(destUrl));  
        document.open();  
        String s = getHtml();  
        ByteArrayInputStream bin = new ByteArrayInputStream(s.getBytes());  
        XMLWorkerHelper.getInstance().parseXHtml(mPdfWriter, document, bin, null, new ChinaFontProvide());  
        document.close();  
        
        assertThat(new File(destUrl).exists()).isTrue();
	}
	
	/**
	 * 自定义错误提示页，有三种方式。
	 * 1、Spring Boot 将所有的错误默认映射到/error， 实现ErrorController，修改发生错误后的映射路径
	 * 2、添加自定义的错误页面
	 * html静态页面：在resources/public/error/ 下定义，如添加404页面： resources/public/error/404.html页面，中文注意页面编码；
	 * 模板引擎页面：在templates/error/下定义，如添加5xx页面： templates/error/5xx.ftl
	 * 注：templates/error/ 这个的优先级比较 resources/public/error/高
	 * 3、使用注解@ControllerAdvice，这个在本项目已经有例子了
	 */
	@Test
	public void errorPage() {
		
	}
	
	@Test
	public void testPDFMvc() throws Exception {
		mvc.perform(get("/file/downloadVoidPdf"));
		mvc.perform(get("/file/downloadResponseEntityPdf"));
		mvc.perform(get("/file/downloadViewPdf"));
		mvc.perform(get("/file/downloadZip"));
	}
	
	/** 
     * 拼写html字符串代码 
     */  
    private String getHtml() {  
        StringBuffer html = new StringBuffer();                                                                                                             
        html.append("<div>美丽的风景</div>");  
        html.append("<div><img src='https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527616378659&di=8620de737fc0dcbec50afda15f0fc12b&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Faec379310a55b319c14072dc4fa98226cefc17d9.jpg'/></div>");  
                                                                                                                
        return html.toString();  
    }  
    
    /** 
     * 解决中文字体 
     */  
    public static final class ChinaFontProvide implements FontProvider {  
  
        @Override  
        public Font getFont(String arg0, String arg1, boolean arg2, float arg3, int arg4, BaseColor arg5) {  
            BaseFont bfChinese = null;  
            try {  
                bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);  
                //也可以使用Windows系统字体(TrueType)  
                //bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);   
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            Font FontChinese = new Font(bfChinese, 20, Font.NORMAL);  
            return FontChinese;  
        }  
  
        @Override  
        public boolean isRegistered(String arg0) {  
            return false;  
        }  
    }  
	
}
