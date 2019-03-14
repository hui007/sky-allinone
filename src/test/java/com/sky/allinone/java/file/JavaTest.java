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