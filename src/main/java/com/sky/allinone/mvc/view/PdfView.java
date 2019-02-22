package com.sky.allinone.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfView extends AbstractIText5PdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			document.open();
			
	        Image img = Image.getInstance(new ClassPathResource("static/forest.jpeg").getURL());
	        //图片缩小到40%
	        img.scalePercent(40);
	        img.setAlignment(Element.ALIGN_CENTER);
	        document.add(img);
			
			// 关闭
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
