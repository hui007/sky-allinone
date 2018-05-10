package com.sky.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

/*
 *  需要设置事务(本质也是AOP)执行的顺序，否则事务的执行顺序高于后续的AOP，会导致动态切换数据源失效。
 *  同时需要设置动态切换数据源AOP的order(需要在事务之前，否则事务只发生在默认库中)  
 */
//@EnableTransactionManagement(order = 2) 

// 禁用掉默认的数据源获取方式，默认会读取配置文件的数据源（spring.datasource.*)。不禁用掉的话，动态数据源初始化的时候会报异常
@SpringBootApplication(exclude = {  
        DataSourceAutoConfiguration.class  
})  
// 切面
@EnableAspectJAutoProxy
// 不使用mybatis的MapperScan。并且不能扫描到CustomBaseMapper，否则会报错
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.sky.allinone.dao.mapper")
// 演示引入xml配置文件
@ImportResource(locations={"classpath:application-bean.xml"})
public class SkyAllinoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyAllinoneApplication.class, args);
	}
}
