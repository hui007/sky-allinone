package com.sky.allinone.remote;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.remote.service.RemoteServiceInf;

/**
 * 这里模拟客户端，必须先启动springboot工程，然后使用WebEnvironment.RANDOM_PORT开启另一个端口，模拟客户端请求服务端
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RemoteTest {
	@Autowired
	@Qualifier("hessianServiceClient")
	RemoteServiceInf remoteServiceInf;
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testHessian() {
		expectedEx.expect(RuntimeException.class);
		remoteServiceInf.sayHi();
		
		String wahaha = remoteServiceInf.wahaha();
		assertThat(wahaha).isEqualTo("娃哈哈，我要喝");
	}
	
	/**
	 * hessian可以使用密码调用：
	 * 可以看到传递到服务端是通过HTTP协议的header传递的。并且传递的Basic认证头。这个头后面跟的字符串就是用户名:密码的Base64字符串，通过base64解密我们可以得到
	 * 实际使用中，传递的密码或者用户请通过其他非对称加密方式加密后传输，服务端进行解密更为安全。
	 * 
	 * 其他的RPC方案：
	 * spring httpInvoker：http协议、java序列化、客户端/服务端都需要使用spring框架
	 * JAX-WS：java api for xml web service。可以通过spring来发布和使用soap web服务。
	 * 
	 */
	@Test
	public void testTodo() {
		
	}
	
}
