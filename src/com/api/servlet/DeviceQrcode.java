package com.api.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.demo.trade.utils.ZxingUtils;
import com.api.annotation.WebServlet;
import com.api.init.StartInit;

public class DeviceQrcode {

	static final Logger logger = LoggerFactory.getLogger(PayServlet.class);
	
	@WebServlet(name = "DeviceQrcodeServlet", urlPatterns = {"/Qrcode.png"}, method = {"get"})
	public void WXTradeRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String data=request.getParameter("data");
		
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);


		//String context="http://118.190.44.144/api/Pay?mid="+mid;
		String fileName=data.hashCode()+"";
		String filePath = StartInit.APPLICATIONPATH+"\\WEB-INF\\tmp\\"+fileName;
		ZxingUtils.getQRCodeImge(data, 256, filePath);

		File file=new File(filePath);
		
		response.getOutputStream();
		
		
		
		ServletOutputStream out = response.getOutputStream();
		BufferedOutputStream bufferOut = new BufferedOutputStream(out);
		InputStream in = new FileInputStream(file);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = in.read(buffer, 0, buffer.length)) != -1) {
			bufferOut.write(buffer, 0, length);
		}
		bufferOut.flush();
		bufferOut.close();
		out.close();
		in.close();
		file.delete();
		
	}
}
