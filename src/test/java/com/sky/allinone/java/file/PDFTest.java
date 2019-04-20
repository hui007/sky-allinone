package com.sky.allinone.java.file;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.*;

public class PDFTest {
	/** The original PDF file. */
	public static final String SRC = "/Users/jianghui/Downloads/20171009_0901_010_15000082040406.pdf";

	/** The resulting PDF file. */
	public static final String DEST = "/Users/jianghui/Downloads/20171009_0901_010_15000082040406-new.pdf";

	@Test
	public void testPDF() {

	}

	/**
	 * Manipulates a PDF file src with the file dest as result
	 *
	 * @param src
	 *            the original PDF
	 * @param dest
	 *            the resulting PDF
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void manipulatePdf(String src, String dest) throws IOException, DocumentException {
		// Creating a reader
		PdfReader reader = new PdfReader(src);
		// step 1
		Rectangle pageSize = reader.getPageSize(1);
		Rectangle toMove = new Rectangle(100, 500, 200, 600);
		Document document = new Document(pageSize);
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		// step 3
		document.open();
		// step 4
		PdfImportedPage page = writer.getImportedPage(reader, 1);
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate template1 = cb.createTemplate(pageSize.getWidth(), pageSize.getHeight());
		template1.rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight());
		template1.rectangle(toMove.getLeft(), toMove.getBottom(), toMove.getWidth(), toMove.getHeight());
		template1.eoClip();
		template1.newPath();
		template1.addTemplate(page, 0, 0);
		PdfTemplate template2 = cb.createTemplate(pageSize.getWidth(), pageSize.getHeight());
		template2.rectangle(toMove.getLeft(), toMove.getBottom(), toMove.getWidth(), toMove.getHeight());
		template2.clip();
		template2.newPath();
		template2.addTemplate(page, 0, 0);
		cb.addTemplate(template1, 0, 0);
		cb.addTemplate(template2, -20, -2);
		// step 4
		document.close();
		reader.close();
	}

	public void manipulatePdf2(String src, String dest) throws IOException, DocumentException {
		// Creating a reader
		PdfReader reader = new PdfReader(src);
		Rectangle pagesize = reader.getPageSizeWithRotation(1);
		float width = pagesize.getWidth();
		float height = pagesize.getHeight();
		// step 1
		Rectangle mediabox = new Rectangle(0, 3 * height, width, 4 * height);
		Document document = new Document(mediabox);
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		// step 3
		document.open();
		// step 4
		PdfContentByte content = writer.getDirectContent();
		PdfImportedPage page = writer.getImportedPage(reader, 1);
		// adding the same page 16 times with a different offset
		for (int i = 0; i < 16;) {
			content.addTemplate(page, 4, 0, 0, 4, 0, 0);
			i++;
			mediabox = new Rectangle((i % 4) * width, (4 - (i / 4)) * height, ((i % 4) + 1) * width,
					(3 - (i / 4)) * height);
			document.setPageSize(mediabox);
			document.newPage();
		}
		// step 4
		document.close();
		reader.close();
	}

	public void manipulatePdf3(String src, String dest) throws IOException, DocumentException {
		// Creating a reader
		PdfReader reader = new PdfReader(src);
		int n = reader.getNumberOfPages();
		// step 1
		Rectangle mediabox = new Rectangle(getHalfPageSize(reader.getPageSizeWithRotation(1)));
		Document document = new Document(mediabox);
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		// step 3
		document.open();
		// step 4
		PdfContentByte content = writer.getDirectContent();
		PdfImportedPage page;
		int i = 1;
		while (true) {
			page = writer.getImportedPage(reader, i);
			content.addTemplate(page, 0, -mediabox.getHeight());
			document.newPage();
			content.addTemplate(page, 0, 0);
			if (++i > 1)
				break;
			mediabox = new Rectangle(getHalfPageSize(reader.getPageSizeWithRotation(i)));
			document.setPageSize(mediabox);
			document.newPage();
		}
		// step 5
		document.close();
		reader.close();
	}

	public Rectangle getHalfPageSize(Rectangle pagesize) {
		float width = pagesize.getWidth();
		float height = pagesize.getHeight();
		return new Rectangle(width, height / 2);
	}

	public void manipulatePdf4(String src, String dest) throws IOException, DocumentException {
		// Creating a reader
		PdfReader reader = new PdfReader(src);
		int n = reader.getNumberOfPages();
		// step 1
		Rectangle mediabox = new Rectangle(getHalfPageSize4(reader.getPageSizeWithRotation(1)));
		Document document = new Document(mediabox);
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		// step 3
		document.open();
		// step 4
		PdfContentByte content = writer.getDirectContent();
		PdfImportedPage page;
		int i = 2;
		while (true) {
			page = writer.getImportedPage(reader, i);
			content.addTemplate(page, 0, -mediabox.getHeight());
			// content.addTemplate(page, 0, -mediabox.getHeight() * 0);
			// document.newPage();
			// content.addTemplate(page, 0, 0);
			if (++i > 1)
				break;
			mediabox = new Rectangle(getHalfPageSize4(reader.getPageSizeWithRotation(i)));
			document.setPageSize(mediabox);
			document.newPage();
		}
		// step 5
		document.close();
		reader.close();
	}

	/**
	 * 可用
	 * 按照高度拆分pdf每页内容
	 * @param excel
	 * @param src
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void pdfSplitedBy3(File srcFile, Map<String, List<Map<Integer, String>>> excel) throws IOException, DocumentException {
		String destDirRoot = "/Users/jianghui/Downloads/平安爱洋回单凭证-0411/aiyang-pingan-2019/";
		String srcPath = srcFile.getAbsolutePath();
		PdfReader reader = new PdfReader(srcPath );
		int n = reader.getNumberOfPages();

		// String contentStr1 = PdfTextExtractor.getTextFromPage(reader, 1);
		// System.out.println(contentStr1.replace("\t", " "));
		// if (1 == 1) {
		// return;
		// }

		int destNameIndex = 1;
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		TextExtractionStrategy ts = null;
		String[] substringsBetween = null;
		Rectangle mediabox = new Rectangle(getHalfPageSize4(reader.getPageSizeWithRotation(1)));
		for (int i = 1; i <= n; i++) {
			ts = parser.processContent(i, new SimpleTextExtractionStrategy());
			substringsBetween = StringUtils.substringsBetween(ts.getResultantText(), "15000092918271\n", "\n");
			// ArrayUtils.reverse(substringsBetween);

			for (int j = substringsBetween.length - 1; j >= 0; j--) {
				// for (int j = 2; j >= 0; j--) {
				// Rectangle rect = new Rectangle(0, mediabox.getHeight() * j,
				// mediabox.getWidth(), mediabox.getHeight());
				// RenderFilter regionFilter = new RegionTextRenderFilter(rect);
				// TextExtractionStrategy strategys = new
				// FilteredTextRenderListener(new
				// LocationTextExtractionStrategy(), regionFilter);
				// String contentStr = PdfTextExtractor.getTextFromPage(reader,
				// i, strategys);
				// String contentStr = PdfTextExtractor.getTextFromPage(reader,
				// i);
				// System.out.println("destNameIndex:" + destNameIndex +
				// contentStr);

				Document document = new Document(mediabox);
				// String destFileName = src.replace(".pdf", "-" +
				// (destNameIndex++) + ".pdf");
//				String destFileName = srcPath.replace(".pdf",
//						"-" + substringsBetween[substringsBetween.length - 1 - j] + ".pdf");
				String name = substringsBetween[substringsBetween.length - 1 - j];

				// 丢到相应的客户公司里
				List<Map<Integer, String>> list = excel.get(name);
				if (list == null) {
//					System.out.println("未找到：" + srcFile.getAbsolutePath() + "-" + name);
					continue;
				}
				String company = list.get(0).get(0);
				String destDir = destDirRoot + company;
				if (!new File(destDir).exists()) {
					new File(destDir).mkdirs();
				}
				destDir = destDir + File.separator;
				String destFileName = destDir + name + ".pdf";

//				String destFileName = srcFile.getParent() + File.separator + name + ".pdf";
				if (new File(destFileName).exists()) {
					int index = 1;
//					destFileName = srcFile.getParent() + File.separator + name + "-" + index + ".pdf";
					destFileName = destDir + name + "-" + index + ".pdf";
					while(new File(destFileName).exists()){
						index = index + 1;
//						destFileName = srcFile.getParent() + File.separator + name + "-" + index + ".pdf";
						destFileName = destDir + name + "-" + index + ".pdf";
					}
//					destFileName = srcFile.getParent() + File.separator + substringsBetween[substringsBetween.length - 1 - j] + ".pdf";
				}
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destFileName));
				document.open();

				PdfContentByte content = writer.getDirectContent();
				PdfImportedPage page = writer.getImportedPage(reader, i);
				content.addTemplate(page, 0, -mediabox.getHeight() * j);
				// document.newPage();
				// content.addTemplate(page, 0, 0);

				document.close();

				// 复制到其他目录去
				for (int k = 1; k < list.size(); k++) {
					String company1 = list.get(k).get(0);
					String destDir1 = destDirRoot + File.separator + company1;
					if (!new File(destDir1).exists()) {
						new File(destDir1).mkdirs();
					}
					destDir1 = destDir1 + File.separator;
					String destFileName1 = destDir1 + name + ".pdf";

					if (new File(destFileName1).exists()) {
						int index = 1;
						destFileName1 = destDir1 + name + "-" + index + ".pdf";
						while(new File(destFileName1).exists()){
							index = index + 1;
							destFileName1 = destDir1 + name + "-" + index + ".pdf";
						}
					}

					FileCopyUtils.copy(new File(destFileName), new File(destFileName1));
				}

				// reader.close();
				// break;

				// PdfReader reader1 = new PdfReader(src.replace(".pdf", "-" +
				// (destNameIndex - 1) + ".pdf"));
				// String contentStr1 =
				// PdfTextExtractor.getTextFromPage(reader1, 1);
				// System.out.println(contentStr1.replace("\t", " "));
				// reader1.close();
			}
		}

		reader.close();
	}

	public Rectangle getHalfPageSize4(Rectangle pagesize) {
		float width = pagesize.getWidth();
		float height = pagesize.getHeight();
		return new Rectangle(width, height / 3);
	}

	public static void extractContent(String str) throws FileNotFoundException, IOException, DocumentException {

		// String pdf =
		// "/Users/jianghui/Downloads/20171009_0901_010_15000082040406-new.pdf";
		PdfReader reader = new PdfReader(new FileInputStream(str));
		// FileOutputStream outfs = new FileOutputStream(pdf + ".txt");

		String content1 = PdfTextExtractor.getTextFromPage(reader, 1);
		System.out.println(content1.replace("\t", " "));
		if (1 == 1) {
			return;
		}

		OutputStream bos = null;
		PdfStamper ps = new PdfStamper(reader, bos);

		Rectangle page = reader.getPageSizeWithRotation(1);
		Rectangle rect = new Rectangle(0, page.getHeight() / 3 * 2, page.getWidth(), page.getHeight());
		RenderFilter regionFilter = new RegionTextRenderFilter(rect);
		TextExtractionStrategy strategys = new FilteredTextRenderListener(new LocationTextExtractionStrategy(),
				regionFilter);

		System.out.println("number of pages:" + reader.getNumberOfPages());
		int pageNumber = reader.getNumberOfPages();
		for (int i = 1; i <= pageNumber; i++) {
			// System.out.println(PdfTextExtractor.getTextFromPage(pdfReader,
			// i).replace("\t", " "));
			System.out.println("Page:" + i);
			String content = PdfTextExtractor.getTextFromPage(reader, i, strategys);
			// String content = PdfTextExtractor.getTextFromPage(reader, i);
			// System.out.println(content.indexOf("验证码:"));
			// System.out.println(content.indexOf("\n", 44));
			// System.out.println(content.substring(44 + "验证码:".length(), 55));
			// System.out.println("content:" + content.replace("\t", " "));
			System.out.println(content.replace("\t", " "));

			byte[] pageContent = content.replace("\t", " ").getBytes();
			// outfs.write(pageContent);
			// outfs.close();
		}
		reader.close();
	}

	/**
	 * 可用
	 * 提取pdf文字内容
	 * @param path
	 * @throws Throwable
	 */
	public static void extractContent2(String path) throws Throwable {
		PdfReader reader = new PdfReader(path);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		File f = new File("d:\\20171009_0901_010_15000082040406.pdf");
		StringWriter pw = new StringWriter();
		// PdfContentReaderTool.listContentStream(f, pw);
		TextExtractionStrategy ts = null;
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			ts = parser.processContent(i, new SimpleTextExtractionStrategy());
			pw.append(ts.getResultantText());
			// pw.println(ts.getResultantText());
			System.out.println(ts.getResultantText());

			String[] substringsBetween = StringUtils.substringsBetween(ts.getResultantText(), "15000082040406\n", "\n");
			System.out.println(substringsBetween.length);
			System.out.println(substringsBetween[0]);
			System.out.println(substringsBetween[1]);
			System.out.println(substringsBetween[2]);
			break;
		}
		System.out.println(pw.toString().indexOf("15000082040406"));
	}

	public void parse() throws IOException {
		PdfReader reader = new PdfReader(DEST);
		PdfObject obj;
		PRAcroForm acroForm = reader.getAcroForm();
		for (int i = 1; i <= reader.getXrefSize(); i++) {
			obj = reader.getPdfObject(i);
			if (obj != null && obj.isStream()) {
				PRStream stream = (PRStream) obj;
				byte[] b;
				try {
					b = PdfReader.getStreamBytes(stream);
				} catch (UnsupportedPdfException e) {
					b = PdfReader.getStreamBytesRaw(stream);
				}
				// FileOutputStream fos = new
				// FileOutputStream(String.format(dest, i));
				// fos.write(b);
				// fos.flush();
				// fos.close();
			}
		}
	}

	@SuppressWarnings("unused")
	public static void extractImage() {

		PdfReader reader = null;
		try {
			// 读取pdf文件
			reader = new PdfReader(DEST);
			// 获得pdf文件的页数
			int sumPage = reader.getNumberOfPages();
			// 读取pdf文件中的每一页
			for (int i = 1; i <= sumPage; i++) {
				// 得到pdf每一页的字典对象
				PdfDictionary dictionary = reader.getPageN(i);
				// 通过RESOURCES得到对应的字典对象
				PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));
				// 得到XOBJECT图片对象
				PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));
				if (xobj != null) {
					for (Iterator it = xobj.getKeys().iterator(); it.hasNext();) {
						PdfObject obj = xobj.get((PdfName) it.next());
						if (obj.isIndirect()) {
							PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);
							PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
							if (PdfName.IMAGE.equals(type)) {
								PdfObject object = reader.getPdfObject(obj);
								if (object.isStream()) {
									PRStream prstream = (PRStream) object;
									byte[] b;
									try {
										b = reader.getStreamBytes(prstream);
									} catch (UnsupportedPdfException e) {
										b = reader.getStreamBytesRaw(prstream);
									}
									FileOutputStream output = new FileOutputStream(
											String.format("d:/pdf/output%d.jpg", i));
									output.write(b);
									output.flush();
									output.close();
								}
							}
							if (PdfName.FORM.equals(type)) {
								PdfObject object = reader.getPdfObject(obj);
								if (object.isDictionary()) {
									PRStream prstream = (PRStream) object;
								}
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testExcel() throws IOException {
		// result1和result2必须为同一份文件
		int keyIndex1 = 0;
		int keyIndex2 = 0;
		int keyIndex3 = 1;
		Map<String, List<Map<Integer, String>>> result1 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex1); // 公司id为key
		Map<String, List<Map<Integer, String>>> result2 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex2); // 业务编码为key
//        Map<String, List<Map<Integer, String>>> result3 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex3); // 业务编码为key
		Map<String, List<Map<Integer, String>>> result3 = null; // 业务编码为key

		printExcelParseResult(result1);
		printExcelParseResult(result2);
//        groupByTwoExcel(result1, result2, 0, 2, 3, "10248");
		groupByOneExcel(result1, keyIndex1, 2, 3, "10424", "-v2", "10424");

		if (1 == 1) {
			return;
		}

		System.out.println(result1.keySet().toString());
		printExcelParseResult(result1);
		printExcelParseResult(result2);
		printExcelParseResult(result3);

		// 连接两个excel，类似于数据库的join操作。
//        Map<String, List<String>> resultJoin = new HashMap<String, List<String>>();
		Map<String, List<Map<Integer, String>>> resultJoin = new HashMap<String, List<Map<Integer, String>>>();
		result2.forEach((s, maps) -> {
//            List<String> oneCellValues = new ArrayList<String>();
//            result3.getOrDefault(s, new ArrayList<Map<Integer, String>>()).forEach(integerStringMap -> {
//                oneCellValues.add(integerStringMap.get(1));
//            });

			resultJoin.put(s, result3.getOrDefault(s, new ArrayList<Map<Integer, String>>()));
		});
		printExcelParseResult(resultJoin);

		// 将已经join的结果，再按另外一个主键group
		String curl = "curl -H \"Content-Type:application/json\" -X POST -d '{\"receiptType\": \"01\", \"busiSystemId\": \"deliver\", \"thirdpaySystemId\": \"hf\", \"payeruserId\":\"11400\", \"downloadId\":\"%s\", \"qryType\":\"1\", \"qryNos\":\"%s\"}' http://10.169.120.33:8008/front/recepit/req";
		result1.forEach((s1, l1) -> {

			List<String> resultGroup = new ArrayList<String>();
			l1.forEach(m1 -> {
				resultJoin.get(m1.get(keyIndex2)).forEach(mJoin -> {
					resultGroup.add(mJoin.get(keyIndex3));
				});
			});
			int groupSize = resultGroup.size();
			System.out.println(String.format("主键：%s，包含的记录数：%d，join后的记录数：%d", s1, l1.size(), groupSize));
			String groupStr = "";
//            System.out.println(String.format("主键：%s，所有记录group后的结果：%s", s1, groupStr));
			int maxNum = 2000;
			if (groupSize > maxNum) {
				int count = groupSize / maxNum;
				for (int i = 0; i <= count + 1 && i * maxNum <= groupSize; i++){
					groupStr = resultGroup.subList(i * maxNum > groupSize ? groupSize : i * maxNum, (i + 1) * maxNum > groupSize ? groupSize : (i + 1) * maxNum).toString();
					System.out.println(String.format("" + curl, s1.trim() + "-" + i, groupStr.replaceAll("\\[|\\]|\\s", "")));
				}
			} else {
				groupStr = resultGroup.toString();
				System.out.println(String.format("" + curl, s1.trim(), groupStr.replaceAll("\\[|\\]|\\s", "")));
			}
		});
	}

	private void groupByOneExcel(Map<String, List<Map<Integer, String>>> result1, int keyIndex, int valueIndex,
								 int cellIgnoreIndex1, String cellIgnoreValue1, String downloadId_postfix, String payuserId){
		// 将已经join的结果，再按另外一个主键group
		String curl = "curl -H \"Content-Type:application/json\" -X POST -d '{\"receiptType\": \"01\", \"busiSystemId\": \"deliver\", \"thirdpaySystemId\": \"hf\", \"payeruserId\":\"%s\", \"downloadId\":\"%s\", \"qryType\":\"1\", \"qryNos\":\"%s\"}' http://10.169.120.33:8008/front/recepit/req";
		result1.forEach((s1, l1) -> {

			List<String> resultGroup = new ArrayList<String>();
			l1.forEach(m1 -> {
				if (StringUtils.equals(cellIgnoreValue1, m1.get(cellIgnoreIndex1))) {
					return;
				}

				resultGroup.add(m1.get(valueIndex));
			});
			int groupSize = resultGroup.size();
			System.out.println(String.format("主键：%s，包含的记录数：%d，join后的记录数：%d", s1, l1.size(), groupSize));
			String groupStr = "";
//            System.out.println(String.format("主键：%s，所有记录group后的结果：%s", s1, groupStr));
			int maxNum = 2000;
			if (groupSize > maxNum) {
				int count = groupSize / maxNum;
				for (int i = 0; i <= count + 1 && i * maxNum <= groupSize; i++){
					groupStr = resultGroup.subList(i * maxNum > groupSize ? groupSize : i * maxNum, (i + 1) * maxNum > groupSize ? groupSize : (i + 1) * maxNum).toString();
					System.out.println(String.format("" + curl, payuserId, s1.trim() + "-" + i + StringUtils.defaultString(downloadId_postfix, ""), groupStr.replaceAll("\\[|\\]|\\s", "")));
				}
			} else {
				groupStr = resultGroup.toString();
				System.out.println(String.format("" + curl, payuserId, s1.trim() + StringUtils.defaultString(downloadId_postfix, ""), groupStr.replaceAll("\\[|\\]|\\s", "")));
			}
		});
	}

	private void printExcelParseResult(Map<String, List<Map<Integer, String>>> result) {
		Map<String, Integer> resultTotalSize = new HashMap<String, Integer>();
		resultTotalSize.put("size", 0);
		System.out.println("主键数量：" + result.size());
		result.forEach((s, maps) -> {
			resultTotalSize.put("size", resultTotalSize.get("size") + maps.size());
			System.out.println(String.format("主键：%s，包含的记录数：%s", s, maps.size() + ""));
		});
		System.out.println("总记录数：" + resultTotalSize.get("size"));
	}

	/*
	 * 遍历该sheet的行，将excel转换为Map<String, List<Map<Sring, String>>>
	 */
	private Map<String, List<Map<Integer, String>>> parseExcelToMap(String file, int keyIndex) throws IOException {
		Map<String, List<Map<Integer, String>>> result = new HashMap<String, List<Map<Integer, String>>>();
		try (InputStream stream = new FileInputStream(file)){
			//读取一个excel表的内容
			POIFSFileSystem fs = new POIFSFileSystem(stream);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			//获取excel表的第一个sheet
			HSSFSheet sheet = wb.getSheetAt(0);
			if (sheet == null) {
				return result;
			}

			//遍历该sheet的行，将excel转换为Map<String, List<Map<Sring, String>>>
			for (int totalRowNum = 1; totalRowNum <= sheet.getLastRowNum(); totalRowNum++) {
				HSSFRow row = sheet.getRow(totalRowNum);
				HSSFCell keyCell = row.getCell(keyIndex);
				CellType keyCellTypeEnum = keyCell.getCellTypeEnum();
				String keyValue = "";
				if (CellType.STRING.equals(keyCellTypeEnum)) {
					keyValue = keyCell.getStringCellValue();
				} else if(CellType.NUMERIC.equals(keyCellTypeEnum)){
					keyValue = new Double(keyCell.getNumericCellValue()).intValue() + "";
				}

				if (!result.containsKey(keyValue)) {
					result.put(keyValue, new ArrayList<Map<Integer, String>>());
				}

				Map<Integer, String> rowData = new HashMap<>();
				row.forEach(cell -> {
					CellType cellTypeEnum = cell.getCellTypeEnum();
					if (CellType.STRING.equals(cellTypeEnum)) {
						rowData.put(cell.getColumnIndex(), cell.getStringCellValue());
					} else if(CellType.NUMERIC.equals(cellTypeEnum)){
						rowData.put(cell.getColumnIndex(), cell.getNumericCellValue() + "");
					}

				});
				result.get(keyValue).add(rowData);
			}

//            printExcelParseResult(result);
		}

		return result;
	}

	/**
	 * Main method.
	 *
	 * @param args
	 *            no arguments needed
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		long start = System.currentTimeMillis();
		PDFTest pdfTest = new PDFTest();

		Map<String, List<Map<Integer, String>>> result1 = pdfTest.parseExcelToMap("/Users/jianghui/Downloads/pingan-company2name-2018.xls", 1); // 公司id为key
		pdfTest.printExcelParseResult(result1);
		if (1 == 1) {
//            return;
		}

//		File file = new File(DEST);
//		file.getParentFile().mkdirs();
		// manipulatePdf(SRC,
		// "/Users/jianghui/Downloads/20171009_0901_010_15000082040406-11.pdf");
		// new
		// PDFTest().manipulatePdf4("/Users/jianghui/Downloads/20171009_0901_010_15000082040406(1).pdf",
		// "/Users/jianghui/Downloads/20171009_0901_010_15000082040406-3.pdf");
		// new
		// PDFTest().extractContent("/Users/jianghui/Downloads/20171009_0901_010_15000082040406-22.pdf");
		// new
		// PDFTest().extractContent("/Users/jianghui/Downloads/20171009_0901_010_15000082040406-new.pdf");
		// new PDFTest().parse();

//		IOFileFilter fileFilter = new IOFileFilter() {
//
//			@Override
//			public boolean accept(File dir, String name) {
//				if (name.startsWith("15000092918271")) {
//					return true;
//				} else {
//					return false;
//				}
//			}
//
//			@Override
//			public boolean accept(File file) {
//				if (file.isDirectory() && file.getName().startsWith("15000092918271")) {
//					return true;
//				} else {
//					return false;
//				}
//			}
//		};
		//
		Collection<File> listFiles = FileUtils.listFilesAndDirs(new File("/Users/jianghui/Downloads/平安爱洋回单凭证-0411"), FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
//		Collection<File> listFiles = FileUtils.listFiles(new File("/Users/jianghui/Downloads/平安爱洋回单凭证-0411"), fileFilter, null );
		System.out.println(listFiles);
		for (Iterator iterator = listFiles.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			if (file.isDirectory() && file.getName().startsWith("15000092918271")) {
				System.out.println("\n" + file.getAbsolutePath());
				IOFileFilter fileFilter = new IOFileFilter() {

					@Override
					public boolean accept(File dir, String name) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean accept(File file) {
						if (file.getName().indexOf("_010_") != -1) {
							return true;
						} else {
							return false;
						}
					}
				};
				Collection<File> listFiles2 = FileUtils.listFiles(file, fileFilter, null);
				System.out.println(listFiles2);

				long start1 = System.currentTimeMillis();
				pdfTest.pdfSplitedBy3(listFiles2.iterator().next(), result1);
				System.out.println("单条耗时：" + (System.currentTimeMillis() - start1) / 1000);
			}
		}
		System.out.println("\n总共耗时：" + (System.currentTimeMillis() - start) / 1000);
		if (1 == 1) {
			return;
		}

		File srcFile = new File("/Users/jianghui/Downloads/平安爱洋回单凭证-0411/15000092918271_20180801-20180831/20190411_0901_010_15000092918271.pdf");
		pdfTest.pdfSplitedBy3(srcFile, result1);
		// extractImage();
		// extractContent2("/Users/jianghui/Downloads/20171009_0901_010_15000082040406-1.pdf");

	}
}
