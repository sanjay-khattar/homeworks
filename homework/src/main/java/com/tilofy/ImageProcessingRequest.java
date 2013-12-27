/**
 *	File Name	: 	ImageProcessingRequest.java
 *  Created		:	Dec 22, 2013 8:06:13 AM
 *  Author		:   Sanjay Khattar
 *  Purpose		:
 */
package com.tilofy;

/**
 * @author sanjayk
 *
 */
public final class ImageProcessingRequest {

	private final String jobId;
	private final String strImgUrl;
	private final String strNewSize;
	
	/**
	 * 
	 */
	public ImageProcessingRequest(String jobId, String strImgUrl, String strNewSize) {
		this.jobId = jobId;
		this.strImgUrl = strImgUrl;
		this.strNewSize = strNewSize;
	}

	/**
	 * @return the strImgUrl
	 */
	public String getStrImgUrl() {
		return strImgUrl;
	}

	/**
	 * @return the strNewSize
	 */
	public String getStrNewSize() {
		return strNewSize;
	}

	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImageProcessingRequest [jobId=" + jobId + ", strImgUrl="
				+ strImgUrl + ", strNewSize=" + strNewSize + "]";
	}

}
