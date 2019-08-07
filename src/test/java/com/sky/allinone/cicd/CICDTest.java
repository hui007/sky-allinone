package com.sky.allinone.cicd;

import org.junit.Test;

public class CICDTest {
	@Test
	public void testCICD() {
		/*
		 * 持续集成。
		 * 
		 * 打包docker镜像：
		 * clean package dockerfile:build  -DskipTests  -X
		 * 
		 * jenkins集成：
		 * 根目录下的Jenkinsfile文件
		 * 
		 * maven:
		 * 	打包：clean package  -DskipTests
		 * 
		 * 上传jar包到腾讯云主机：
		 * 	安装lrzsz：yum -y install lrzsz。然后rz在腾讯云主机的浏览器界面并没有什么用
		 * 	SecureCRT：使用SSH方式登录SecureCRT，然后使用rz命令
		 */
	}
}
