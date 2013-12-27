/**
 *	File Name	: 	ImageProcessor.java
 *  Created		:	Dec 22, 2013 6:45:17 AM
 *  Author		:   Sanjay Khattar
 *  Purpose		:
 */
package com.tilofy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author sanjayk
 *
 */
public class ImageProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
		
	private static String[] imageWriterFormatNamesArray;
	
	private static final String FILE_PATH_ILLEGAL_CHARS = "/:?";

	private static final String IMAGES_ROOT_FOLDER = System.getProperty("user.home");

	private static final String RAW_IMAGES_FOLDER_NAME = "rawimages";

	private static final String RESIZED_IMAGES_FOLDER_NAME = "resizedimages";

	private static final String RESIZED_IMAGE_FILE_PREFIX = "Resized.";

	static {
		imageWriterFormatNamesArray = ImageIO.getWriterFormatNames();
		logger.debug("Supported imageWriterFormatNames: "
				+ Joiner.on("\n").join(imageWriterFormatNamesArray));		
	}
	
	/**
	 * 
	 */
	public ImageProcessor() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String resizeImage(String jobId, String strImgUrl, String strNewSize) {
		String resizedImageFilePath = null;
		
		try {
			Pattern sizeSplitterPattern = Pattern.compile("x",
					Pattern.CASE_INSENSITIVE);
			Iterable<String> sizeComponents = Splitter.on(sizeSplitterPattern)
					.split(strNewSize);
			int count = 0;
			int newWidth = -1;
			int newHeight = -1;
			for (String sizeComponent : sizeComponents) {
				logger.trace("Got sizeComponent = " + sizeComponent);
				if (count == 0) {
					newWidth = Integer.parseInt(sizeComponent);
				} else if (count == 1) {
					newHeight = Integer.parseInt(sizeComponent);
				} else {
					logger.warn("Only two size components - one for width and one for height - are handled currently.");
				}
				count++;
			}
			if ((newWidth < 0) || (newHeight < 0)) {
				String excpMsg = "Cannot resize to a negative width or height..."
						+ "Got strNewSize='" + strNewSize + "'.";
				logger.warn(excpMsg);
				throw new RuntimeException(excpMsg);
			}
			BufferedImage img = ImageLoader.loadImage(strImgUrl);
			String rawImagesFolderName = RAW_IMAGES_FOLDER_NAME;
			String rawImageFilePath = getImageFilePath(jobId, rawImagesFolderName,
					strImgUrl);
			File rawImageFile = new File(rawImageFilePath);
			rawImageFile.mkdirs();
			String formatName = "png";
			ImageIO.write(img, formatName, rawImageFile);
			logger.info("Saved a copy of raw image in: " + rawImageFilePath);
			Scalr.Mode resizeMode = Scalr.Mode.AUTOMATIC;
			int targetWidth = newWidth;
			int targetHeight = newHeight;
			BufferedImage resizedImg = Scalr.resize(img, resizeMode,
					targetWidth, targetHeight);
			String resizedImagesFolderName = RESIZED_IMAGES_FOLDER_NAME;
			String strResizedImgUrl = RESIZED_IMAGE_FILE_PREFIX + targetWidth
					+ "x" + targetHeight + "." + strImgUrl;
			resizedImageFilePath = getImageFilePath(jobId, resizedImagesFolderName,
					strResizedImgUrl);
			File resizedImageFile = new File(resizedImageFilePath);
			resizedImageFile.mkdirs();
			ImageIO.write(resizedImg, formatName, resizedImageFile);
			logger.info("Saved resized image in: " + resizedImageFilePath);
		}
		catch (ImageProcessorException ipe) {
			throw ipe;
		}
		catch (Exception e) {
			String excpMsg = "Exception resizing image. strImgUrl: "
					+ strImgUrl + " strNewSize: " + strNewSize + " Exception: "
					+ e;
			throw new ImageProcessorException(excpMsg, e);
		}
		
		return resizedImageFilePath;
	}

	private static String getImageFilePath(String jobId, String imageFolderName, String strImgUrl) {
		String imgFileName = urlToFileName(strImgUrl);
		
		String destFilePath = IMAGES_ROOT_FOLDER + File.separator + "work"
				+ File.separator + "tilofy" + File.separator + "homework"
				+ File.separator + imageFolderName + File.separator + jobId + File.separator
				+ imgFileName;
		
		return destFilePath;
	}
	
	private static String urlToFileName(String url) {
		String strFilePath = CharMatcher.anyOf(FILE_PATH_ILLEGAL_CHARS).replaceFrom(url, "_");
		
		return strFilePath;
	}

}
