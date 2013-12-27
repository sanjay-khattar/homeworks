/**
 * 
 */
package com.tilofy;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author esakhat
 *
 */
public class ImageLoader {

	private static final Logger logger = LoggerFactory.getLogger(ImageLoader.class);
	
	/**
	 * 
	 */
	public ImageLoader() {
		// TODO Auto-generated constructor stub
	}

	public static BufferedImage loadImage(String strImgUrl) {
		logger.info("Loading image from strImgUrl: " + strImgUrl);
		BufferedImage img = null;
		URL imgUrl = null;
		if (strImgUrl != null) {
			try {
				imgUrl = new URL(strImgUrl);
			    img = ImageIO.read(imgUrl);
			} 
			catch (Exception e) {
				String excpMsg = "Exception loading image from URL: " + strImgUrl + " :" + e;
				throw new ImageProcessorException(excpMsg, e);
			}
		}
		else {
			String excpMsg = "Cannot load 'null' image.";
			throw new ImageProcessorException(excpMsg);
		}
		
		return img;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
