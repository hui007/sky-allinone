package com.sky.allinone.conf.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

	@Override
	protected Object determineCurrentLookupKey() {
		log.debug("数据源为{}", DynamicDataSourceContextHolder.getDB());

		return DynamicDataSourceContextHolder.getDB();
	}

}
