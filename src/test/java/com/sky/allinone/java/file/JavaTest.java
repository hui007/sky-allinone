package com.sky.allinone.java.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

public class JavaTest {
	@Test
	public void testLambda() {
		String s = "'180428001714669','180428001714668','180428001714662','180508001725282','180508001725281','180508001725280','180508001725279','180508001725278','180508001725277','180508001725273','180508001725269','180508001725268','180810001910619','180810001910566','180810001910564','180813001912936','180827001940884','180827001940883','180827001940882','180827001940881','180919001995078','180919001995077','180919001995076','180919001995075','180919001995078','180919001995077','180919001995076','180919001995075','181024002092294','181024002092112','181024002092086','181024002092079','181024002092076','181024002092073','181024002092070','181024002092062','181024002092049','181024002092046','181024002092023','181024002092020','181024002092011','181024002092000','181024002091994'";
		String[] arr = s.split(",");
		
		Set<String> set = Sets.newSet(arr);
		System.out.println(set.size());
		
		List<String> list = Arrays.asList(arr);
		Map<String, String> map = list.stream().collect(Collectors.toMap(String::toString, Function.identity(), (v1, v2) -> v2));
		System.out.println(map);
		
		Map<String, Integer> map1 = list.stream().collect(Collectors.toMap(String::toString, a -> 1, (v1, v2) -> v1 + v2));
		System.out.println(map1);
	}
	
	/**
	 * 解析已有文件，根据文件名里的日期，按天生成汇总文件，汇总文件里包含当天所有的文件名
	 * @throws IOException
	 */
	@Test
	public void testFile3() throws IOException {
		File srcDir1 = new File("/Users/jianghui/Downloads/pingan-12");
//		File srcDir2 = new File("/Users/jianghui/Downloads/khkf01_15199032155530");
		File destDir1 = new File("/Users/jianghui/Downloads/done");
		if (!destDir1.exists()) {
			destDir1.mkdirs();
		}
		
		Collection<File> listFiles = FileUtils.listFiles(srcDir1, null, false);
//		listFiles.addAll(FileUtils.listFiles(srcDir2, null, false));
		Iterator<File> iterateFiles = listFiles.iterator();
		
		while (iterateFiles.hasNext()) {
			File fileSrc = iterateFiles.next();
			String[] split = fileSrc.getName().substring(0, fileSrc.getName().indexOf(".")).split("_");
			if (StringUtils.equals("aiyunchang", split[0]) || StringUtils.equals("C005", split[1])) {
				continue;
			}
			
			File destFile = new File(destDir1.getAbsolutePath() + "/" + split[3] + ".DONE");
			if (destFile.exists()) {
				FileUtils.writeStringToFile(destFile, "|::|" + fileSrc.getName(), true);
			} else {
				destFile.createNewFile();
				FileUtils.writeStringToFile(destFile, fileSrc.getName(), false);
			}
		}
	}
	
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
