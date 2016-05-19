package com.cmbc.devops.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;


/**
 * @author langzi
 *
 */
public final class IOUtils {
	
	private IOUtils() {
	}

	private static final Logger LOGGER = Logger.getLogger(IOUtils.class);
	
	
	/**
	 * @author langzi
	 * @param is
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static String toString(InputStream is) {
		BufferedReader br = toBufferedReader(is);
		return toStringBuilder(br).toString();
	}
	
	
	/**
	 * @author langzi
	 * @param br
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static StringBuilder toStringBuilder(BufferedReader br) {
		StringBuilder sb = new StringBuilder();
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		
		return sb;
	}
	
	
	/**
	 * @author langzi
	 * @param is
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static BufferedReader toBufferedReader(InputStream is) {
		try {
			return new BufferedReader(new InputStreamReader(is));
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * @author langzi
	 * @param reader
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static void close(Reader reader) {
		if(reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}
	}
	
	/**
	 * @author langzi
	 * @param is
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static void close(InputStream is) {
		if(is != null) {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}
	}
}
