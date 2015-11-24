package com.test;

import java.util.HashMap;
import java.util.Map;

public class Test {

	private static final String sendTo = "583424001@qq.com";

	private static final String mailName = "Welcome to Telkomsel Mobile Connect! Activate your account now";

	private static final String templateFileName = "mailTemplate.ftl";

	private static final String USERNAME = "fullName";
	
	private static final String URLLINK = "urlLink";

	public static void main(String[] args) {

		String userNameValue = "夜夜夜猫";
		String urlLinkValue = "http://www.baidu.com";
		
		Map<String, String> contentMap = new HashMap<>();
		contentMap.put(USERNAME, userNameValue);
        contentMap.put(URLLINK, urlLinkValue);

		Mail.sendMailByTemplate(sendTo, mailName, contentMap, templateFileName);

	}

}
