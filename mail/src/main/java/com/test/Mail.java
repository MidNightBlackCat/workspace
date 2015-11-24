package com.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Mail {

	/**
	 * MIME邮件对象
     */
	private MimeMessage mimeMsg;

    /**
	 * 系统属性
	 */
	private Properties props;

	/**
	 * smtp是否需要认证
	 */
	private boolean needAuth = false;

	/**
	 * smtp认证用户名和密码
	 */
	private String username = "";

	private String password = "";

	/**
	 * Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成
	 */
	private Multipart mp;

	protected static Logger log = Logger.getLogger(Mail.class);

    /**
     * 编码形式UTF-8
     */
	private static final String ENCODING = "UTF-8";

    /**
     * 邮件模板
     */
	private static Template template = null;

    /**
     * 构造方法
     * @param smtp SMTP服务端地址
     */
	public Mail(String smtp) {

		setSmtpHost(smtp);

		createMimeMessage();

	}

    /**
     * 设置SMTP服务端
     * @param hostName 服务端地址
     */
	public void setSmtpHost(String hostName) {

		log.debug("set system properties：mail.smtp.host= " + hostName);

		if (props == null)

			props = System.getProperties(); // 获得系统属性对象

		props.put("mail.smtp.host", hostName); // 设置SMTP主机

	}

    /**
     * 创建mime消息
     * @return TRUE:创建成功<br>FALSE:创建失败
     */
	public boolean createMimeMessage() {

        //邮件会话对象
        Session session;
        log.debug(" prepare to get email object");
        session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
        mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
        mp = new MimeMultipart(); // mp 一个multipart对象
        return true;
	}

    /**
     * 设置是否需要验证
     * @param need 是否需要验证
     */
	public void setNeedAuth(boolean need) {
		log.debug("set smtp ：mail.smtp.auth= " + need);
		if (props == null)

			props = System.getProperties();

		if (need) {

			props.put("mail.smtp.auth", "true");

		} else {

			props.put("mail.smtp.auth", "false");

		}

	}

    /**
     * 设置用户名和密码
     * @param name 用户名
     * @param pass 密码
     */
	public void setNamePass(String name, String pass) {

		log.debug("get email user and password");
		username = name;

		password = pass;

	}

    /**
     * 设置邮件主题
     * @param mailSubject 邮件主题
     * @return TRUE:成功<br>FALSE:失败
     */
	public boolean setSubject(String mailSubject) {
		try {
			mimeMsg.setSubject(MimeUtility
					.encodeText(mailSubject, "utf-8", "B"));
			return true;

		} catch (Exception e) {
			log.error("set email subTitle error:" + e.toString(), e);
			return false;

		}

	}

    /**
     * 设置mime消息体
     * @param mailBody 消息体内容
     * @return TRUE:成功<br>FALSE:失败
     */
	public boolean setBody(String mailBody) {

		try {

			BodyPart bp = new MimeBodyPart();

			// 转换成中文格式

			bp.setContent(

			""

			+ mailBody, "text/html;charset=utf-8");

            mp.addBodyPart(bp);

			return true;

		} catch (Exception e) {
			log.error("set email text error:" + e.toString(), e);
			return false;

		}

	}

    /**
     * 添加邮件附件
     * @param filename 附件名称
     * @return TRUE:成功<br> FALSE:失败
     */
	public boolean addFileAffix(String filename) {

		log.debug("add email file");
		try {

			BodyPart bp = new MimeBodyPart();

			FileDataSource fileds = new FileDataSource(filename);

			bp.setDataHandler(new DataHandler(fileds));

			bp.setFileName(fileds.getName());

			mp.addBodyPart(bp);

			return true;

		} catch (Exception e) {
			log.error("add email file error:" + e.toString(), e);
			return false;

		}

	}

    /**
     * 设置邮件发送人
     * @param from 邮件发送人地址
     * @return TRUE:成功<br>FALSE:失败
     */
	public boolean setFrom(String from) {
		log.debug("set email sender");
		AtomicReference<String> nick = new AtomicReference<>("");
        AtomicReference<String> name = new AtomicReference<>("");
		try {
			Resource resource = new ClassPathResource("mail/mail.properties");
			Properties nickprops = PropertiesLoaderUtils
					.loadProperties(resource);
			name.set(nickprops.getProperty("mail.nickname"));
            nick.set(MimeUtility.encodeText(name.get(), "utf-8", "B"));
            mimeMsg.setFrom(new InternetAddress(nick.get() + " <" + from + ">")); // 设置发信人

			return true;

		} catch (Exception e) {
			log.error("set email sender error:" + e.toString(), e);
			return false;

		}

	}

    /**
     * 设置收件人
     * @param to 收件人地址
     * @return TRUE:成功<br>FALSE:失败
     */
	public boolean setTo(String to) {

		log.debug("set email receiver");
		if (to == null)

			return false;

		try {

			mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress

                    .parse(to));

			return true;

		} catch (Exception e) {
			log.error("set email sender receiver:" + e.toString(), e);
			return false;

		}

	}

    /**
     * 设置抄送
     * @param copyto 抄送的email地址
     * @return TRUE:设置成功<br>FALSE:设置失败
     */
	public boolean setCopyTo(String copyto) {

		if (copyto == null)

			return false;

		try {

			mimeMsg.setRecipients(Message.RecipientType.CC,

                    InternetAddress.parse(copyto));

			return true;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;

		}

	}

    /**
     * 发送邮件
     * @return TRUE:成功<br>FALSE:失败
     */
	public boolean sendout() {

		try {

			mimeMsg.setContent(mp);

			mimeMsg.saveChanges();
			log.debug("email is sending ...");
			Session mailSession = Session.getInstance(props, null);

			Transport transport = mailSession.getTransport("smtp");

			transport.connect((String) props.get("mail.smtp.host"), username,

			password);

			transport.sendMessage(mimeMsg, mimeMsg

			.getRecipients(Message.RecipientType.TO));

			// transport.send(mimeMsg);

			log.debug("email send success!");
            transport.close();

			return true;

		} catch (Exception e) {

			log.error("email send error:" + e.toString(), e);

			return false;

		}

	}
	
	/**
	 * 根据邮件内容模版发送邮件
	 * 
	 * @param receiver
	 *            收件人邮箱
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容map
	 * @param templateName
	 *            邮件内容模版名称
	 * @return boolean true表示发送成功，false表示发送失败
	 */
	public static boolean sendMailByTemplate(String receiver, String subject,
			Map<String, String> map, String templateName) {
		String content = null;
		Resource resource = new ClassPathResource("mail/mail.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String server = props.getProperty("mail.server");
			String from = props.getProperty("mail.from");
			String username = props.getProperty("mail.username");
			String password = props.getProperty("mail.password");
			Mail mail = new Mail(server);
			mail.setNeedAuth(true);
			template = FreemarkerUtil.getTemplate(templateName);
            if (null == template)
            {
                return  false;
            }
			template.setEncoding(ENCODING);
			try {
				content = FreeMarkerTemplateUtils.processTemplateIntoString(
						template, map);
			} catch (TemplateException e) {
				log.error(
						"################### set template error #################",
						e);
			}
			// 标题
			if (!mail.setSubject(subject)) {
				log.error("################### set mail title error #################");
				return false;
			}
			// 邮件内容 支持html 如欢迎你,java</font>
			if (!mail.setBody(content)) {
				log.error("################### set mail content error#################");
				return false;
			}
			// 收件人邮箱
			if (!mail.setTo(receiver)) {
				log.error("################### set mail receiver error#################");
				return false;
			}
			// 发件人邮箱
			if (!mail.setFrom(from)) {
				log.error("################### set mail from error#################");
				return false;
			}
			// 设置附件
			// if(themail.addFileAffix("mail.txt") == false)
			// return; //附件在本地机子上的绝对路径
			mail.setNamePass(username, password); // 用户名与密码,即您选择一个自己的电邮
			if (!mail.sendout()) {
				log.error("################### send mail error#################");
				return false;
			}
			log.debug("====================== Send Email Success!!!!=======================");
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		return true;
	}

	/**
	 * 普通文本方式发送邮件
	 * 
	 * @param receiver
	 *            收件人邮箱
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @return boolean true表示发送成功，false表示发送失败
	 */
	public static boolean sendMail(String receiver, String subject,
			String content) {
		Resource resource = new ClassPathResource("mail/mail.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String server = props.getProperty("mail.server");
			String from = props.getProperty("mail.from");
			String username = props.getProperty("mail.username");
			String password = props.getProperty("mail.password");
			String temp = props.getProperty("mail.template");
			Mail mail = new Mail(server);
			mail.setNeedAuth(true);
			HashMap<String, String> root = new HashMap<>();
			root.put("content", content);
			template = FreemarkerUtil.getTemplate(temp);
            if (null == template)
            {
                return false;
            }
			template.setEncoding(ENCODING);
			try {
				content = FreeMarkerTemplateUtils.processTemplateIntoString(
						template, root);
			} catch (TemplateException e) {
				log.error(
						"################### set template error #################",
						e);
			}
			// 标题
			if (!mail.setSubject(subject)) {
				log.error("################### set mail title error #################");
				return false;
			}
			// 邮件内容 支持html 如欢迎你,java</font>
			if (!mail.setBody(content)) {
				log.error("################### set mail content error#################");
				return false;
			}
			// 收件人邮箱
			if (!mail.setTo(receiver)) {
				log.error("################### set mail receiver error#################");
				return false;
			}
			// 发件人邮箱
			if (!mail.setFrom(from)) {
				log.error("################### set mail from error#################");
				return false;
			}
			// 设置附件
			// if(themail.addFileAffix("mail.txt") == false)
			// return; //附件在本地机子上的绝对路径
			mail.setNamePass(username, password); // 用户名与密码,即您选择一个自己的电邮
			if (!mail.sendout()) {
				log.error("################### send mail error#################");
				return false;
			}
			log.debug("====================== Send Email Success!!!!=======================");
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		return true;
	}

}
