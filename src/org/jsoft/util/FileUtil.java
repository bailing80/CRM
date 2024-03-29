package org.jsoft.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

public class FileUtil {

	public static void downLoad(String filePath, HttpServletResponse response,
			boolean isOnLine) throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			response.sendError(404, "File not found!");
			return;
		}
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
		byte[] buf = new byte[1024];
		int len = 0;

		response.reset(); // 非常重要
		if (isOnLine) { // 在线打开方式
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
			response.setHeader("Content-Disposition",
					"inline; filename=" + f.getName());
			// 文件名应该编码成UTF-8
		} else { // 纯下载方式
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ f.getName());
		}
		OutputStream out = response.getOutputStream();
		while ((len = br.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		br.close();
		out.close();
	}

	@SuppressWarnings("rawtypes")
	public static String upload(String path, HttpServletRequest request)
			throws IOException {
		String result = "";
		// 获得根目录的物理路径
		String saveDirectory = path;
		// 每个文件最大5m,最多3个文件,所以...
		int maxPostSize = 3 * 5 * 1024 * 1024;
		// response的编码为"utf-8",同时采用缺省的文件名冲突解决策略,实现上传
		MultipartRequest multi = new MultipartRequest(request, saveDirectory,
				maxPostSize, "utf-8");
		// 用于接收文本字段
		String text = multi.getParameter("text");
		System.out.println("text = " + text);
		// 把获得的文件名放在容器中
		Enumeration files = multi.getFileNames();
		while (files.hasMoreElements()) {
			String name = (String) files.nextElement();
			File f = multi.getFile(name);
			if (f != null) {
				String fileName = multi.getFilesystemName(name);
				result = fileName;
				// 在这里进行相应的操作，如存入数据库等
				System.out.println("fileName = " + fileName);
			}
		}
		return result;
	}

}
