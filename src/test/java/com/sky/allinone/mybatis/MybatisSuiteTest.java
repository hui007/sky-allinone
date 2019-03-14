package com.sky.allinone.mybatis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 需要启动本机的mysql，因为本机安装了两个mysql，旧的mysql不兼容操作系统了，启动新mysql时，需要指定数据目录到旧的mysql
 * $MYSQL_HOME/support-files/mysql.server start --datadir=/usr/local/mysql-5.7.11-osx10.9-x86_64/data
 * 
 * 或者将旧的数据库导入新数据库服务器，登录本机mysql后，执行
 * source /src/test/resources/mysql/文件名
 * @author joshui
 *
 */
@RunWith(Suite.class)  
@Suite.SuiteClasses({MybatisGenTest.class,MybatisCommonMapperTest.class})  
public class MybatisSuiteTest {

}
