package com.cmbc.devops.util;

import java.security.MessageDigest;

import org.apache.log4j.Logger;

/**  
 * date：2015年8月17日 上午9:52:27  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：HashUtil.java  
 * description：  
 */
public class HashUtil {
	
	private HashUtil() {
	}
	private static final Logger logger = Logger.getLogger(HashUtil.class);
	
	public static String sha1Hash(String source) {
		try {
			String ret = new String(source);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			ret = TextUtil.convertToHexString(
					md.digest(ret.getBytes()));
			return ret;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public static String md5Hash(String source) {
		try {
			String ret = new String(source);
			MessageDigest md = MessageDigest.getInstance("MD5");
			ret = TextUtil.convertToHexString(
					md.digest(ret.getBytes()));
			return ret.toUpperCase();
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
	
}
