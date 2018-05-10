package com.sky.allinone.mybatis.plugin;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({@Signature(
		  type= Executor.class,
		  method = "query",
		  args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})})
public class ExamplePlugin implements Interceptor {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		logger.info("测试调用intercept");
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		logger.info("测试调用plugin");
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		logger.info("测试调用setProperties");
	}

}
