package com.sky.allinone.java.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

public class JavaTest {
	@Test
	public void formatString() throws IOException, ParseException {
		String str = "curl -X POST -H 'Content-Type: application/json' -d '{\"beginDate\": \"%s\",\"endDate\": \"%s\",\"zipFileName\": \"pingan/%s/RECPDF_%s_%s_M.zip\"}' http://10.169.120.33:8008/back/recepit/pingan/saveInDB";
		String merchId = "15199032155530";
		String startDateStr = "20190103";
		String endDateStr = "20190131";
		List<String> filterDateStrs = Arrays.asList("20190104","20190109","20190110","20190111","20190115",
				"20190118","20190121","20190122","20190123","20190124","20190125","20190127","20190130");
		List<String> dateStrs = Arrays.asList("20190115", "20190116", "20190117", "20190121", "20190122", "20190123", "20190125", "20190128", "20190129", "20190131", "20190218", "20190219");

//		List<Date> rangeDates = getRangeDates(startDateStr, endDateStr, filterDateStrs);
		List<Date> rangeDates = getSpecDates(dateStrs);
		rangeDates.forEach(date -> {
			Date nextDate = DateUtils.addDays(date, 1);
			String dateF1 = DateFormatUtils.format(date, "yyyy-MM-dd");
			String dateF2 = DateFormatUtils.format(date, "yyyyMMdd");
			String dateF3 = DateFormatUtils.format(nextDate, "yyyy-MM-dd");

			System.out.println(String.format(str, dateF1, dateF3, dateF2, merchId, dateF2));
		});

	}

	/**
	 * 将开始时间-结束时间之间的date取出来，排除掉需要过滤的时间
	 * @param startDateStr yyyyMMdd
	 * @param endDateStr yyyyMMdd
	 * @param filterDateStrs
	 * @return
	 */
	private List<Date> getRangeDates(String startDateStr, String endDateStr, List<String> filterDateStrs) throws ParseException {
		List<Date> rs = new ArrayList<Date>();
		String yyyyMMdd = "yyyyMMdd";
		String yyyy_MM_dd = "yyyy-MM-dd";

		Date startDate = DateUtils.parseDate(startDateStr, yyyyMMdd);
		Date endDate = DateUtils.parseDate(endDateStr, yyyyMMdd);
		Date midDate = DateUtils.addDays(startDate, 0);
		for (int i = 1; midDate.before(endDate); i++) {
			String dateF = DateFormatUtils.format(midDate, yyyyMMdd);
			midDate = DateUtils.addDays(startDate, i);

			if (filterDateStrs.contains(dateF)){
				continue;
			}
			rs.add(midDate);
		}

		return rs;
	}

	private List<Date> getSpecDates(List<String> dateStrs) throws ParseException {
		List<Date> rs = new ArrayList<Date>();
		String yyyyMMdd = "yyyyMMdd";

		dateStrs.forEach(s -> {
			Date date = null;
			try {
				date = DateUtils.parseDate(s, yyyyMMdd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			rs.add(date);
		});

		return rs;
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