package com.sky.allinone.docker;

public class DockerTest {
	/*
	 * docker镜像：有序、无状态、不可更改
	 * 容器：多个容器共享同一个镜像，有不同的可写入层。容器退出后，可写入层随之删除。
	 * joshui/demo:1.0：repository是joshui/demo，1.0是tag。
	 * 容器id是自动生成的，容器名称可以指定。
	 * docker命令 --help：查看命令的帮助信息
	 * 
	 * 容器间通信：
	 * docker run --link 容器名字：官方不推荐这种方式。如果是同一主机，则不需要暴露额外端口。主要是修改/etc/hosts文件，加入link到的容器的映射条目，相应的env环境变量也会加上。
	 * 
	 * docker compose：定义docker-compose.yml。适用于单机部署，不能跨网络，每次启动后会新建一个网络（在yml文件里配置）。可以一键启动yml里所有的容器。
	 * docker compose up -d：启动所有容器
	 * docker compose ps：显示所有容器
	 * logs、stop、rm、build：docker compose的其他命令。更新Dockerfile后，一定要执行build重新创建镜像。
	 * 
	 * docker网络模型：NONE网络模型、bridge网络模型（默认）、HOST网络模型、OVERLAY网络模型（跨主机）。
	 * docker network ls：显示docker启动的网络
	 * 	inspect 网络名（ls命令显示的）：查看网络信息
	 * 	create --driver bridge 网络名称：创建一个bridge网络
	 * 	connect 网络名 容器名：把容器连接到某一个网络，可以同时加入多个网络
	 * docker run --net 网络名称：连接到指定网络（docker network ls可显示网络名）。
	 * bridge网络模型：容器默认有两个接口，loopback和eth0。docker demon进程会创建一个网桥bridge0，所有容器通过etho接口连接网桥。通过ifconfig、brctl（要安装）可以查看。每个容器有自己独立的namespace网络空间，有自己独立的ip地址。
	 * 	同一主机上相同的bridge可以互相通信，不同bridge不能通信；不能跨主机通信。
	 * host网络模型：隔离性（与主机共享网络，可见主机的所有网络接口；容器间网络不具有隔离性）、安全性差、性能好（不需经过bridge和iptable的处理）
	 * overlay网络模型：跨主机通信；swarm模式无需外部键值存储系统，否则需要如外部存储系统consul；
	 * 
	 * COE工具：容器编排引擎
	 * 	如：docker swarm mode（原生的）、k8s、mesos、hashicorp nomad
	 * 
	 * 容器监控：
	 * HEALTHCHECK：在Dockerfile里用这个命令定时检测
	 */
	
	/*
	 * TODO:
	 * 
	 * 常用命令:
	 * docker pull tomcat：下载tomcat的latest标签镜像，尽量避免使用这个标签
	 * 
	 * docker images：查看所有镜像
	 * docker history joshui/demo:1.0：查看镜像创建历史
	 * 
	 * docker run：对同一个镜像，执行多次，会起来多个容器
	 * -ti centos:7 ls[bash|sleep 1000] 绑定终端，交互式的。终端将直接进入docker，并停留在ls命令后的界面
	 * -d ：后台运行，默认是前台运行。
	 * -p 9000:8080：映射到宿主机的9000端口
	 * --name yourname：指定名称
	 * 
	 * docker exec -ti 容器名称 命令（如bash）：进入运行中的容器
	 * 
	 * docker logs containerid[cotainerName]：类似tail，显示容器的日志
	 * 
	 * docker ps：所有运行中的docker进程
	 * docker ps -a：已退出的容器也会显示
	 * 
	 * docker inspect containerId：查看容器信息
	 * 
	 * exit：退出容器终端界面，容器进程还在
	 * docker stop containerid：停止容器进程
	 * docker start containerid：重启已停掉的容器
	 * docker rm -f containerid：强制删掉容器。其他方式停掉容器后，容器其实还在，可以通过docker logs看容器日志
	 * docker rmi -f 镜像id：删掉镜像
	 * 
	 * docket commit containerid joshui/centos:1.01：基于一个已停止的容器创建一个新的镜像
	 * 
	 * Dockerfile：一个命令的集合，每个命令创建一个镜像层。docker有一套自己的命令。每一个命令都创建一个临时的容器，命令结束后，就删掉临时容器
	 * FROM：指定base image。如果本地没有，则去远程下载base image
	 * LABEL maintainer "joshui(joshui@163.com)"：该镜像的一些说明
	 * RUN yum update -y：执行一个命令，执行完之后就commit，生成新的容器供下一步使用
	 * COPY：将上下文里的文件复制到镜像里
	 * ADD：不但可以复制文件，还可以下载文件，自动解压/压缩文件。通常用copy，除非明确指定add。
	 * 命令1 && 命令2：两个命令会在同一层里，层数越少，镜像大小越小
	 * CMD ["echo","hello world"]：exec模式。容器启动后，执行的命令，如果不指定，会使用基础镜像提供的cmd命令。也可以在启动容器时，覆盖这个默认命令。
	 * CMD echo "hello world"：shell模式。这种模式可以识别当前的环境变量，如$HOME。
	 * 
	 * docker build -t joshui/demo:1.0 . ：以当期目录为上下文，会把上下文里的文件打成tar包上传给docer守护进程，然后读取当前目录的Dockerfile创建docker镜像。
	 * 主动缓存：会导致安装的镜像不是最新的。可以在dockerfile里使用&&或者--no-cache，不使用缓存。
	 * --no-cache，不使用缓存
	 * 
	 * docker login：在客户端登录docker hub
	 * docker push：push镜像到docker hub
	 * 
	 */
}
