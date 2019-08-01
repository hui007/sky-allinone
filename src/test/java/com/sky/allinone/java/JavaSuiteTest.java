package com.sky.allinone.java;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.allinone.java.file.PDFTest;

@RunWith(Suite.class)  
@Suite.SuiteClasses({JavaTest.class,PDFTest.class})  
public class JavaSuiteTest {

}
