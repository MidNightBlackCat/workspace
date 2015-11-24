package com.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtil {
	/**
	 * 获取模板
	 * 
	 * @param name 模板名称
	 * @return 模板
	 */
	public static Template getTemplate(String name) {
		try {
			// 通过Freemaker的Configuration读取相应的ftl
			Configuration cfg = new Configuration();
			// 设定去哪里读取相应的ftl模板文件
			cfg.setClassForTemplateLoading(FreemarkerUtil.class, "/mail");
			// 在模板文件目录中找到名称为name的文件
			return cfg.getTemplate(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 输出到控制台
	 * 
	 * @param name 模板文件名
	 * @param root 模板参数
	 */
	public void print(String name, Map<String, Object> root) {
		try {
			// 通过Template可以将模板文件输出到相应的流
			Template temp = FreemarkerUtil.getTemplate(name);
			if (temp != null) {
				temp.process(root, new PrintWriter(System.out));
			}
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出到文件
	 * 
	 * @param name 模板名
	 * @param root 模板参数
	 * @param outFile 模板文件
	 */
	public void fprint(String name, Map<String, Object> root, String outFile) {
		FileWriter out = null;
		try {
			// 通过一个文件输出流，就可以写到相应的文件中
			out = new FileWriter(new File("E:\\freemarker\\ftl\\" + outFile));
			Template temp = FreemarkerUtil.getTemplate(name);
			if (temp != null) {
				temp.process(root, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
