package com.sky.allinone.java.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class JavaTest {
	@Test
	public void testFile1() throws IOException {
		File srcDir = new File("/Users/jianghui/Downloads/汇付回单凭证0626");
		String str_180621788779 = new String("180621001787717,180621001787718,180621001787719,180621001787721,180621001787722");
		String str_180621789551 = new String("180621001787678,180621001787677,180621001787679,180621001787680,180621001787681,180621001787682,180621001787683,180621001787684,180621001787685,180621001787686,180621001787687,180621001787688,180621001787689,180621001787690,180621001787691,180621001787692,180621001787693,180621001787694,180621001787695,180621001787696,180621001787697,180621001787698,180621001787699,180621001787700,180621001787701,180621001787702,180621001787703,180621001787704,180621001787705,180621001787706,180621001787707,180621001787708,180621001787709,180621001787710,180621001787711,180621001787712,180621001787713,180621001787714");
		String str_180621790381 = new String("180621001787727,180621001787728,180621001787729,180621001787730,180621001787731,180621001787732,180621001787733,180621001787734,180621001787735,180621001787736,180621001787737,180621001787738,180621001787739,180621001787740,180621001787741,180621001787742,180621001787743,180621001787744,180621001787745,180621001787746,180621001787747,180621001787748,180621001787749,180621001787750,180621001787751");
		String str_180622793196 = new String("180622001788316,180622001788317,180622001788318,180622001788319,180622001788320");
		
		File destDir = new File("/Users/jianghui/Downloads/汇付回单凭证-订单号（180621788779）-temp");
		copyDirectoryFilter(srcDir, destDir, str_180621788779);
		copyFileFilter(destDir);
		
		destDir = new File("/Users/jianghui/Downloads/汇付回单凭证-订单号（180621789551）-temp");
		copyDirectoryFilter(srcDir, destDir, str_180621789551);
		copyFileFilter(destDir);
		
		destDir = new File("/Users/jianghui/Downloads/汇付回单凭证-订单号（180621790381）-temp");
		copyDirectoryFilter(srcDir, destDir, str_180621790381);
		copyFileFilter(destDir);
		
		destDir = new File("/Users/jianghui/Downloads/汇付回单凭证-订单号（180622793196）-temp");
		copyDirectoryFilter(srcDir, destDir, str_180622793196);
		copyFileFilter(destDir);
		
	}
	
	@Test
	public void testFile2() throws IOException {
		String basePath = "/Users/jianghui/Downloads/temp-del/hf";
		String[] srcPath = {"hf0910"};
		String destPath = "/Users/jianghui/Downloads/temp-del/hf/total";
		
		for (int i = 0; i < srcPath.length; i++) {
			FileUtils.copyDirectory(new File(basePath + "/" + srcPath[i]), new File(destPath + "/" + srcPath[i]), file -> {
				boolean contains = StringUtils.contains(file.getName(), "-");
				return contains;
			});
		}
	}

	/**
	 * 将文件复制到另外一个文件夹
	 * @param destDir
	 * @throws IOException
	 */
	private void copyFileFilter(File destDir) throws IOException {
		Iterator<File> iterateFiles = FileUtils.iterateFiles(destDir, new String[]{"pdf"}, false);
		while (iterateFiles.hasNext()) {
			File fileSrc = (File) iterateFiles.next();
			String destDirFinal = destDir.getPath().replace("-temp", "");
			
			if (StringUtils.contains(fileSrc.getName(), "胡桂风")) {
				FileUtils.copyFile(fileSrc, new File(destDirFinal + File.separator + fileSrc.getName()));
			} else {
				FileUtils.copyFile(fileSrc, new File(destDirFinal + File.separator + fileSrc.getName().split("-")[0] + ".pdf"));
			}
		}
	}

	/**
	 * 将一个文件夹下的文件拷贝到另一个文件夹，按照过滤条件，有些文件不拷贝过去
	 * @param srcDir
	 * @param destDir
	 * @param str_180621788779
	 * @throws IOException
	 */
	private void copyDirectoryFilter(File srcDir, File destDir, String str_180621788779) throws IOException {
		FileUtils.copyDirectory(srcDir, destDir, file -> {
			Stream<String> stream = Stream.of(str_180621788779.split(","));
			boolean anyMatch = stream.anyMatch(t -> {
				if (StringUtils.contains(file.getName(), t)) {
					return true;
				}
				
				return false;
			});
			
			return anyMatch;
		});
	}
}
