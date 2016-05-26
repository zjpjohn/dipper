package com.cmbc.devops.util;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * date：2015年8月12日 下午4:59:34 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：TextUtil.java description：
 */
public final class TextUtil {
	
	private TextUtil() {
	}
	private static final Logger LOGGER = Logger.getLogger(TextUtil.class);

	/**
	 * 获取摘要
	 * 
	 * @author langzi
	 * @param input
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String getSummary(String input) {
		if (input.length() > 10) {
			return input.substring(0, 10) + "...";
		} else {
			return input;
		}
	}

	/**
	 * 创建日志对象
	 * 
	 * @author langzi
	 * @param key
	 * @param value
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static JSONObject createLogInfo(String key, String value) {
		JSONObject logInfo = new JSONObject();
		try {
			logInfo.put("key", URLEncoder.encode(key, "utf-8"));
			logInfo.put("value", URLEncoder.encode(value, "utf-8").replace("+", "%20"));
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return logInfo;
	}

	/**
	 * 字符转换
	 * 
	 * @author langzi
	 * @param name
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String encodeText(String name) {
		if (StringUtils.hasText(name)) {
			return "";
		}
		String result = "";
		try {
			result = URLEncoder.encode(name, "utf-8").replace("+", "%20");
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return result;
	}

	/**
	 * 特殊字符校验
	 * 
	 * @author langzi
	 * @param str
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static boolean hasForbiddenChar(String str) {
		Pattern pattern = Pattern.compile("[`~!$%^&#*()+|\\\\\\]\\[\\]\\{\\}:;'\\,<>?]+");
		Matcher m = pattern.matcher(str);
		return m.find();
	}

	/**
	 * 返回正确信息
	 * 
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String stickyToSuccess(String msg) {
		return "<div class='alert alert-success'>" + msg + "</div>";
	}

	/**
	 * 返回一般信息
	 * 
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String stickyToInfo(String msg) {
		return "<div class='alert alert-info'>" + msg + "</div>";
	}

	/**
	 * 返回错误信息
	 * 
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String stickyToError(String msg) {
		return "<div class='alert alert-danger'>" + msg + "</div>";
	}

	/**
	 * 返回警告信息
	 * 
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0 2015年8月12日
	 */
	public static String stickyToWarning(String msg) {
		return "<div class='alert alert-warning'>" + msg + "</div>";
	}

	public static String convertToHexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex;
		}
		return ret;
	}

	private static byte uniteBytes(byte src0, byte src1) {
		byte b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (b0 ^ b1);
		return ret;
	}

	public static byte[] convertToBytes(String src) {
		byte[] ret = new byte[src.getBytes().length / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/* 去除字符串中的空格、回车、换行符、制表符 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	public static boolean isIn(String str, String[] strs){
		if(strs == null || strs.length==0){
			return false;
		}
		for(int i=0; i < strs.length; i++){
			String aStr = strs[i];
			if (aStr.equals(str)) {
				return true;
			}
		}
		return false;
	}
}
