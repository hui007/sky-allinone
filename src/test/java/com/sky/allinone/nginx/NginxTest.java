package com.sky.allinone.nginx;

public class NginxTest {
	/*
	 * TODO:
	 * 静态or动态服务器：nginx是静态服务器；不支持jsp、servlet等动态内容
	 * 
	 * 进程模型：
	 * 启动后有一个mater和n个worker进程。master接收客户端请求，分发给worker处理。
	 * 
	 * nginx [-c 配置文件]：启动。如果不指定配置文件，则使用NGINX_HOME下的默认配置文件
	 * 	-s stop[quit|reload]：停止、退出、重启
	 * 	-t：检查配置文件语法
	 * kill -QUIT master进程号：安全停止
	 * 
	 * 高可用：
	 * 	通过keepalived虚拟出一个对外ip，vip对应内网的一个master nginx和N个backup nginx，或者配置双主模式
	 * 	vrrp：虚拟路由器冗余协议
	 * 	keepalived配置：类似nginx，配置vrrp、virtual server（对应nginx实例）等
	 * 
	 * 注意事项：
	 * 	nginx里没有对日志文件做切割，需要运维自己去维护这个。mv access.log access.log.20190629，kill -USR1 主进程号（让nginx重新生成log文件）
	 * 
	 * 核心配置项：
	 * 三大块：main、event、http
	 * main：
	 * 	work_processes 4：工作进程数。跟cpu核数一致，或者是它的两倍
	 * 	work_cpu_affinity 0001 0010 0100 1000：nginx默认没有利用多核，这个例子假设有4核
	 * 	work_rlimit_nofile 10240：工作进程能打开的最大文件数
	 * 	error_log /var/log/nginx/err.log路径及文件名 warn日志标识：日志标识（关联不同的日志格式）
	 * 	pid：pid存储文件路径
	 * 	event
	 * 		use epoll; IO模型
	 * 		worker_connectors 1024; ：每个work进程最大可连接数
	 * 		accept_mutex on|off：惊群（扔一粒米到鸡群，所有的鸡都惊动了）。打开后，没有惊群效应，关闭后，多个进程可以抢着处理多个请求
	 * 	http
	 * 		include /etc/config/*.conf：引入其他的配置片段
	 * 		charset utf-8：编码方式
	 * 		access_log off：关闭记录访问日志
	 * 		sendfile：开启高效传输模式。类似零拷贝？
	 * 		gzip on：打开压缩。一般都是打开的。
	 * 		proxy_temp_path 路径：临时路径
	 * 		proxy_cache_path 路径 level=1:2 keys_zone=mic:200m max_size=1g：缓存
	 * 		server：虚拟主机。类似tomcat里的虚拟主机。基于域名的、基于端口的、基于ip的（很少使用）
	 * 			listen 80：监听端口
	 * 			server_name www.sky.com：服务名称（域名解析）
	 * 			ssl on：打开https
	 * 			ssl_certificate crt文件的路径：
	 * 			ssl_certificate_key key文件的路径：
	 * 			gzip on：是否压缩数据流。
	 * 			gzip_vary on：有些浏览器不支持压缩，可以动态关闭压缩效果？
	 * 			gzip_buffers 4 16k：以16k内存单位，每次按照原始大小的4倍申请
	 * 			gzip_comp_level 4：压缩级别1-9，压缩等级越高，越耗性能。会有失真。
	 * 			gzip_min_length 500|5k：大于500字节以上的才做压缩
	 * 			gzip_types text/css text/xml：只针对这些文件压缩。文件类型参考mime.types文件。对文本类进行压缩性价比才高。
	 * 			log_format 名称 日志格式 ：日志格式里可以引用很多内置变量
	 * 			access_log [off|路径及文件名 日志标识（log_format里日志格式参数的名称）]：访问日志，可以指定不记录日志。
	 * 			location [ |=|^~|~|~*] /uri：精准匹配>一般匹配（最长匹配优先）>正则匹配。/和=/，这个根匹配比较特殊（会匹配两次），访问/时，会先到=/，然后从=/里的页面跳转到/，寻找/里root里的=/里匹配到的文件。从头匹配（低于正则匹配）、精确匹配、头部非正则匹配（中断后续匹配）、正则匹配（低于后面的正则匹配）、正则匹配（不区分大小写）。(location =) > (location 完整路径) > (location ^~ 路径) > (location ~,~* 正则顺序) > (location 部分起始路径) > (/)
	 * 				access_log
	 * 				rewrite ^/正则表达式 http://www.baidu.com替换到的字符串 [break(终止后续匹配)|last(终止当前匹配，新的url重头开始匹配)]：url重写。访问根目录的话，直接跳转到百度首页。
	 * 				[rewrite '^/images/([a-z]{3})/(.*)\.(png)$' /joshui?file=$2.$3]: 使用$0..1..N指代正则匹配到的字符串片段。会继续匹配/joshui打头的Location
	 * 				set $image_name $2：配合rewrite使用。然后可使用try_files
	 * 				try_files /$arg_file /image404.html：尝试找url里file参数指定的文件，如果找不到，继续往后找，知道找到第一个为止
	 * 				return 404 "未找到"：返回404给客户端，并且定义404页面里的内容为”未找到“
	 * 				expires 5s|m|h|d：浏览器缓存时间
	 * 				proxy_pass http://www.baidu.com[http://192.168.1.40:8080|http://upstream的名称]：跳转到百度。反向代理百度
	 * 				proxy_set_header X_Real_IP(任意名字) $remote_addr[$host]：设置头部信息（可配置多条），在被代理的服务器上可以通过header获取。如果带了下划线的名字获取不到，可通过设置http段里的underscores_in_header on。
	 * 				proxy_send_timeout 60：负载连接到实际服务器的超时时间，60秒。
	 * 				root /data/program/tomcat/wabapps/sky/static：根目录。可放置静态文件。
	 * 		upstream tomcatserver（任意名称）：负载均衡。然后设置proxy_pass tomcatserver就好了
	 * 			ip_hash[fair 根据服务器访问时间负载均衡]：负载策略。默认是轮询策略。
	 * 			server 192.168.1.92:8080
	 * 			server 192.168.1.93:8080 [weight 4 如果是轮询策略，可以设置权重]
	 * 
	 * VIP：虚拟IP漂移。两台nginx互为主备，同一时刻只有一台机器响应请求。
	 */
}
