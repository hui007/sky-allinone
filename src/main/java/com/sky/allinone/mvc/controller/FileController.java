package com.sky.allinone.mvc.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sky.allinone.mvc.view.PdfView;

@Controller
@RequestMapping({ "/file" })
public class FileController {
	Logger logger = LoggerFactory.getLogger(FileController.class);

	/**
	 * 也可以使用@requestPart注解拿到byte[]，不过这种方式比较原始；
	 * 在处理使用代码段传递过来的文件时，这个注解比较有用，如@RequestPart("meta-data") MetaData
	 * metadata, @RequestPart("file-data") MultipartFile file
	 * 
	 * @param file
	 * @param copyToPath
	 * @param request
	 * @return
	 */
	@PostMapping(path = "fileUpload")
	public String fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("copyToPath") String copyToPath,
			HttpServletRequest request) {
		try {
			file.transferTo(new File(copyToPath + File.separator + file.getOriginalFilename()));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		return "uploadSuccess";
	}

	/**
	 * 这里完全是手动创建的pdf，也可以使用pdf模板来创建
	 * 
	 * 使用模板方式：
	 * 需要新建视图，继承AbstractPdfStamperView或AbstractPdfView。不过测试的情况是，spring4的版本可能跟iText5版本不匹配，不能使用AbstractPdfStamperView
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/downloadVoidPdf")
	public void downloadVoidPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 告诉浏览器用什么软件可以打开此文件
		response.setHeader("content-Type", "application/pdf");
		// 下载文件的默认名称
		response.setHeader("Content-Disposition", "attachment;filename=sky1.pdf");

		createPdf(response.getOutputStream());
	}
	
	/**
	 * 下载pdf文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/downloadResponseEntityPdf")
	public ResponseEntity<byte[]> downloadResponseEntityPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpHeaders headers = new HttpHeaders(); 
		headers.setContentDispositionFormData("attachment", "sky2.pdf");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		createPdf(bos);
		
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.OK); 
		return resp;
	}
	
	/**
	 * 下载pdf文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/downloadViewPdf")
	public ModelAndView downloadViewPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PdfView pdf = new PdfView(); 
		
		Map<String, String> dataMap = new HashMap<String, String>();
		ModelAndView mv = new ModelAndView(pdf, dataMap);  
		
		return mv;
	}
	
	/**
	 * 压缩文件下载
	 * 这里没有注意流的正确关闭，实际使用中要注意
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/downloadZip")
	public ResponseEntity<byte[]> downloadZip(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// zip临时文件
		String outFileName = UUID.randomUUID().toString() + ".zip";
		String outFilePath = request.getSession().getServletContext().getRealPath("/");
		
		File outFile = new File(outFilePath + outFileName); 
		HttpHeaders headers = new HttpHeaders(); 
		headers.setContentDispositionFormData("attachment", outFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		
		// zip压缩流
		ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(outFile));
		
		// 生成pdf文件内容到输出流
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		createPdf(bos);
		
		// 将pdf文件输出流压缩输出到zip临时文件
		BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(bos.toByteArray()));
		zipFile(is, zipOutStream);
		
		// 读取zip临时文件内容到字节数组
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(outFile));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        
        fis.close();
        zipOutStream.close();
        bos.close();
        is.close();
        
        outFile.delete();
		
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(buffer, headers, HttpStatus.OK); 
		return resp;
	}
	
	private void zipFile(InputStream is, ZipOutputStream outputstream) throws IOException, ServletException {
		final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
		long totalBytes = 0; // 接受流的容量
		int splitNum = 0; // 流需要分开的数量
		int leaveByte = 0; // 文件剩下的字符数
		byte[] inOutbyte; // byte数组接受文件的数据
		BufferedInputStream bis = null;
		
        try {
			bis = new BufferedInputStream(is);
			ZipEntry entry = new ZipEntry(RandomStringUtils.random(5, "sky01") + ".pdf");
			outputstream.putNextEntry(entry);

			totalBytes = bis.available(); // 通过available方法取得流的最大字节数
			splitNum = (int) Math.floor(totalBytes / MAX_BYTE); // 取得流文件需要分开读取的次数
			leaveByte = (int) totalBytes % MAX_BYTE; // 分开读取之后，最后剩余要读取的字节数

			// 写入流数据
			if (splitNum > 0) {
			    for (int j = 0; j < splitNum; ++j) {
			        inOutbyte = new byte[MAX_BYTE];
			        bis.read(inOutbyte, 0, MAX_BYTE);
			        outputstream.write(inOutbyte, 0, MAX_BYTE); 
			    }
			}
			// 写入剩下的流数据
			inOutbyte = new byte[leaveByte];
			bis.read(inOutbyte, 0, leaveByte);
			outputstream.write(inOutbyte);
			outputstream.flush();
			
        } catch (IOException e) {
            throw e;
        } finally {
        	// 关闭
        	if (outputstream != null) {
				outputstream.closeEntry(); // Closes the current ZIP entry and positions the stream for writing the next entry
			}
        }
    }

	private void createPdf(OutputStream outputStream)
			throws DocumentException, IOException, BadElementException, MalformedURLException {
		//创建pdf文档对象
		Document document = new Document();
		//将文件输出流与pdf对象，进行关联
		PdfWriter.getInstance(document, outputStream);
		//打开文档
		document.open();
		
		//字体类型为宋体
        BaseFont Chinese = BaseFont.createFont("STSong-Light",
                "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        //红色、字号15的加粗字体
        Font fontChinese1 = new Font(Chinese, 15, Font.BOLDITALIC,BaseColor.RED);
        //蓝色、字号15的加粗字体
        Font fontChinese2 = new Font(Chinese, 15, Font.BOLDITALIC, BaseColor.BLUE);
        //黑色、字号12的普通字体
        Font fontChinese3 = new Font(Chinese, 12, Font.NORMAL, BaseColor.BLACK);
        //黑色、字号12的普通字体
        Font fontChinese4 = new Font(Chinese, 12, Font.NORMAL, BaseColor.BLACK);
		
        Paragraph paragraph = new Paragraph("章节",fontChinese1); //创建段落对象
        Chapter chapter = new Chapter(paragraph,1); //创建章节对象
        paragraph = new Paragraph("小节",fontChinese2);
        Section section = chapter.addSection(paragraph);//创建并加入小节对象
        
        //创建表格对象
        PdfPTable table = new PdfPTable(3);
        PdfPCell cell = new PdfPCell(); 
        
        //第一行标题：姓名、年龄、工资
        Paragraph zhi = new Paragraph("姓名",fontChinese3);       
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);            

        zhi = new Paragraph("年龄",fontChinese3);
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        zhi = new Paragraph("工资",fontChinese3);
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        
        //第二行：张三、24、3500
        zhi = new Paragraph("张三",fontChinese3);
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        zhi = new Paragraph("24",fontChinese3);
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        zhi = new Paragraph("3500",fontChinese3);
        cell.setPhrase(zhi);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        
        //将表格添加到小节中
        paragraph = new Paragraph("\n\n工资表:\n\n",fontChinese3);
        section.add(paragraph);
        section.add(table); 
        
        paragraph = new Paragraph("\n\n添加的图片:\n\n",fontChinese4);
        Image img = Image.getInstance(new ClassPathResource("static/forest.jpeg").getURL());
        //图片缩小到40%
        img.scalePercent(40);
        img.setAlignment(Element.ALIGN_CENTER);
        section.add(paragraph);
        section.add(img);

        //先将section添加到chapter,再将chapter添加到文档document中
        document.add(chapter);

        //关闭文档
        document.close();
	}
}
