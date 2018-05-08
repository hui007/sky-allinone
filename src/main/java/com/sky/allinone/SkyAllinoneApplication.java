package com.sky.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/*
 *  需要设置事务(本质也是AOP)执行的顺序，否则事务的执行顺序高于后续的AOP，会导致动态切换数据源失效。
 *  同时需要设置动态切换数据源AOP的order(需要在事务之前，否则事务只发生在默认库中)  
 */
//@EnableTransactionManagement(order = 2) 
//@ImportResource(locations={"classpath:application-bean.xml"})
/*如果有通过mapper工厂生成的mapper，spring boot不会默认扫描@mapper注解，需要再用注解指定下*/
//@MapperScan("com.sky.mybatis")

// exclude = {DataSourceAutoConfiguration.class}用于禁用掉默认的数据源获取方式，默认会读取配置文件的据源（spring.datasource.*）
@SpringBootApplication(exclude = {  
        DataSourceAutoConfiguration.class  
})  
// 切面
@EnableAspectJAutoProxy
public class SkyAllinoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyAllinoneApplication.class, args);
	}
}
