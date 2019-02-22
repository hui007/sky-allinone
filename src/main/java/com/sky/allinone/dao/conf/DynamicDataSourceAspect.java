package com.sky.allinone.dao.conf;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Order(1)
public class DynamicDataSourceAspect {
	
	//切点  
    @Pointcut("execution(* com.sky.mybatis..*.*(..)))")  
    public void aspect() { }  

	@Before("@annotation(DynamicDataSourceName)")
//	@Before("aspect()")
	public void beforeSwitchDS(JoinPoint point) {

		// 获得当前访问的class
		Class<?> className = point.getTarget().getClass();

		// 获得访问的方法名
		String methodName = point.getSignature().getName();
		// 得到方法的参数的类型
		Class[] argClass = ((MethodSignature) point.getSignature()).getParameterTypes();
		String dataSource = DynamicDataSourceContextHolder.DEFAULT_DS;
		try {
			// 得到访问的方法对象
			Method method = className.getMethod(methodName, argClass);

			// 判断是否存在@DS注解
			if (method.isAnnotationPresent(DynamicDataSourceName.class)) {
				DynamicDataSourceName annotation = method.getAnnotation(DynamicDataSourceName.class);
				// 取出注解中的数据源名
				dataSource = annotation.value();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 切换数据源
		DynamicDataSourceContextHolder.setDB(dataSource);

	}

	@After("@annotation(DynamicDataSourceName)")
//	@Before("aspect()")
	public void afterSwitchDS(JoinPoint point) {

		DynamicDataSourceContextHolder.clearDB();

	}
}
