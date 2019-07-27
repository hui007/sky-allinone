package com.sky.allinone.database;

public class DatabaseTest {
	/*
	 * TODO：
	 * 分库分表：
	 * 	拆分策略：
	 * 		垂直拆分：垂直分库（解决表过多，按领域模型拆分；避免跨库join）、垂直分表（解决列过多）
	 * 		水平拆分：单表拆成多表
	 * 	拆分手段：
	 * 		水平拆分：hash；范围拆分；日期拆分；一致性hash：
	 * 	拆分后的问题：
	 * 		跨库join：采用服务间调用；全局表（每个库都冗余一份？）；字段冗余（定时任务或通知异步更新）；
	 * 		跨分片排序分页：
	 * 		唯一主键：uuid、snowflake、mongodb唯一注解、zookeeper自增id、取id的服务、
	 * 		分布式事务：互联网公司用强一致性分布式事务比较少
	 * 	难点：业务数据已经渗透到各个应用；数据本身的迁移也麻烦
	 * 
	 * mysql主从复制：
	 * 	log-bin=mysql-bin：定义主机mysql的二进制日志文件的前缀。文件放在/var/lib/mysql/下。
	 * 	server-id=140：集群内唯一的id。主和从都需要写这个
	 * 	主从强同步：可通过配置解决？主机等到从机返回同步完成后，才返回客户端？
	 * 	mysqlbinlog --base64-output=decode-rows -v mysql-bin.00001：查看bin-log文件。可以看到文件里的position。
	 * 	show binlog events in 'mysql-bin.00001'：也可以通过这种方式查看binlog文件内容，以表格方式展示。
	 * 	bin-log的格式：show variables like '%log%'可以查看当前使用的日志格式。可以使用set命令修改格式或者在配置文件里修改。
	 * 		statement：记录update语句。但是对于使用了uuid、now()这种函数的语句，会导致主从数据对不上。
	 * 		row：记录update语句更新到的每一条语句。占空间。
	 * 		mix：mysql自己决定采用哪种方式。
	 * 	show master status：会显示二进制日志文件等信息
	 * 	relay-log=slave-relay-bin：从打开中继日志。从机里的io线程读取主机的bin-log文件，写入从机的中继日志文件；然后从机里的sql线程，读取从机的中继日志文件，写入从机的数据库。
	 * 	relay-log-index=slave-relay-bin.index：
	 * 	read-only=1：只读。不过限制不了root权限的用户。
	 * 	change master to master-host='192.168.1.140',master-port=3306,master-user='主里的用户',master-passowrd='密码',master-log-file='show master status里的文件名',master-log-pos='起始复制地点':从机命令行里输入，用来连接主机。
	 * 	start slave：change master命令连上主机后，执行这个命令。
	 * 	show slave status：从机状态
	 * 	问题：
	 * 		主从同步的延迟问题：缓存异步刷磁盘（可配置刷新策略）、磁盘读写、网络io、大sql的执行，导致从机sql排队。
	 * 		延时监控：Nagios做网络监控；mk-heatbeat做心跳监控；应用层解决方案，先写redis，同步完成后，再删除redis。
	 * 
	 * mysql双主架构：
	 * 	结构拓扑图：vip-（masterA，masterB），masterB-（slaveB1，slaveB2）。master互为主备，A负责写入，B可负责读，B负责同步数据到从库。
	 * 
	 * 分库分表中间件：
	 * 	非关系型数据库：key-value，如redis，memcache；面向文档，如mongodb；面向列，如hbase；那es属于哪种类型？
	 * 	常用中间件：mycat（基于cobar改造）、sharding-jdbc、TDDL（淘宝的）、cobar。mycat是基于mysql协议，sharding-jdbc是基于jdbc接口。
	 * 	mycat：实现了mysql协议，可以当做一个mysql来用，类似TiDB。
	 * 		schema.xml：逻辑库、逻辑表、数据节点配置。会映射到实际库、实际表。可配置读写分离、负载均衡、表插入查询策略；
	 * 		rule.xml：分片路由规则。如果是非分片键，会把sql发到所有的数据节点执行，然后汇总数据，然后会跟分片键做一个缓存。
	 * 		server.xml：mycat作为mysql代理服务器的配置。
	 * 		explain：可以看到分片查询的逻辑。好似重写了mysql标准的explain协议？
	 * 		高可用：
	 * 			拓扑图：vip-两台haproxy-多台mycat（因为mycat是无状态的，所以可以在前面加haproxy）
	 */
}
