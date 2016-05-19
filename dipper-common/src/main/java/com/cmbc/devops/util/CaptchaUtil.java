package com.cmbc.devops.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.cmbc.devops.component.CaptchaNumber;

public final class CaptchaUtil {

	private CaptchaUtil() {
	}
	private static Font myFont = new Font("Arial Black", Font.PLAIN, 16);
	
	/**
	 * Get random Color
	 * @author langzi
	 * @param fc
	 * @param bc
	 * @return
	 */
	private static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255){
			fc = 255;
		}
		if (bc > 255){
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * delete cache
	 * @author langzi
	 * @return
	 */
	public static HttpHeaders getCaptchaHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Pragma", "No-cache");
		headers.set("Cache-Control", "no-cache");
		headers.set("Expires", "0");
		headers.setContentType(MediaType.IMAGE_JPEG);
		return headers;
	}

	/**
	 * Get a CaptchaNumber object with random number
	 * @author langzi
	 * @return
	 */
	public static CaptchaNumber getCaptchaNumber() {
		Random random = new Random();
		Integer num1 = random.nextInt(10);
		Integer num2 = random.nextInt(10);
		Integer totalNum = num1 + num2;
		return new CaptchaNumber(num1, num2, totalNum);
	}

	/**
	 * Get images of identifing code
	 * @author langzi
	 * @param num1
	 * @param num2
	 * @return
	 * @throws IOException
	 */
	public static byte[] getImage(Integer num1, Integer num2) throws IOException {
		int width = 100, height = 20;
		Random random = new Random();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(getRandColor(200, 250));
		g.fillRect(1, 1, width - 1, height - 1);
		g.setColor(new Color(102, 102, 102));
		g.drawRect(0, 0, width - 1, height - 1);
		g.setFont(myFont);
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width - 1);// 起点的x坐标
			int y = random.nextInt(height - 1);// 起点的y坐标
			int x1 = random.nextInt(6) + 1;// x轴偏移量
			int y1 = random.nextInt(12) + 1;// y轴偏移量
			g.drawLine(x, y, x + x1, y + y1);
		}
		for (int i = 0; i < 70; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int x1 = random.nextInt(12) + 1;
			int y1 = random.nextInt(6) + 1;
			g.drawLine(x, y, x - x1, y - y1);
		}
		g.setColor(new Color(20 + random.nextInt(110),
				20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString(num1.toString(), 15 * 0 + 10, 15);
		g.setColor(new Color(20 + random.nextInt(110),
				20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString("+", 15 * 1 + 10, 15);
		g.setColor(new Color(20 + random.nextInt(110),
				20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString(num2.toString(), 15 * 2 + 10, 15);
		g.setColor(new Color(20 + random.nextInt(110),
				20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString("=", 15 * 3 + 10, 15);
		g.setColor(new Color(20 + random.nextInt(110),
				20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString("?", 15 * 4 + 10, 15);
		g.dispose();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", stream);
		byte[] data = stream.toByteArray();
		stream.close();
		return data;
	}
}
