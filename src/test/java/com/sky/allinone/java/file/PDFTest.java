package com.sky.allinone.java.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.IOP.Encoding;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class PDFTest {
	/** The original PDF file. */
	public static final String SRC = "/Users/jianghui/Downloads/20171009_0901_010_15000082040406.pdf";

	/** The resulting PDF file. */
	public static final String DEST = "/Users/jianghui/Downloads/20171009_0901_010_15000082040406-new.pdf";

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
	 * @param src
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void pdfSplitedBy3(String src) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(src);
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
			substringsBetween = StringUtils.substringsBetween(ts.getResultantText(), "15000082040406\n", "\n");
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
				String destFileName = src.replace(".pdf",
						"-" + substringsBetween[substringsBetween.length - 1 - j] + ".pdf");
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destFileName));
				document.open();

				PdfContentByte content = writer.getDirectContent();
				PdfImportedPage page = writer.getImportedPage(reader, i);
				content.addTemplate(page, 0, -mediabox.getHeight() * j);
				// document.newPage();
				// content.addTemplate(page, 0, 0);

				document.close();
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

	/**
	 * Main method.
	 * 
	 * @param args
	 *            no arguments needed
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
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
		new PDFTest().pdfSplitedBy3(SRC);
		// extractImage();
		// extractContent2("/Users/jianghui/Downloads/20171009_0901_010_15000082040406-1.pdf");
	}
}
