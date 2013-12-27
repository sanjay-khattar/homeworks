/**
 *	File Name	: 	ImageProcessingResult.java
 *  Created		:	Dec 22, 2013 8:22:43 AM
 *  Author		:   Sanjay Khattar
 *  Purpose		:
 */
package com.tilofy;

/**
 * @author sanjayk
 *
 */
public final class ImageProcessingResult {

	private final ImageProcessingRequest imageProcessingRequest;
	private String resizedImageFilePath;
	private JobStatus jobStatus = JobStatus.UNKNOWN;
	private String resultMsg;
	
	/**
	 * 
	 */
	public ImageProcessingResult(ImageProcessingRequest imageProcessingRequest, JobStatus jobStatus) {
		this.imageProcessingRequest = imageProcessingRequest;
		this.jobStatus = jobStatus;
	}

	/**
	 * @return the imageProcessingRequest
	 */
	public ImageProcessingRequest getImageProcessingRequest() {
		return imageProcessingRequest;
	}

	/**
	 * @return the resizedImageFilePath
	 */
	public String getResizedImageFilePath() {
		return resizedImageFilePath;
	}

	/**
	 * @param resizedImageFilePath the resizedImageFilePath to set
	 */
	public void setResizedImageFilePath(String resizedImageFilePath) {
		this.resizedImageFilePath = resizedImageFilePath;
	}

	/**
	 * @return the jobsStatus
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * @return the resultMsg
	 */
	public String getResultMsg() {
		return resultMsg;
	}

	/**
	 * @param resultMsg the resultMsg to set
	 */
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImageProcessingResult [imageProcessingRequest="
				+ imageProcessingRequest + ", resizedImageFilePath="
				+ resizedImageFilePath + ", jobStatus=" + jobStatus
				+ ", resultMsg=" + resultMsg + "]";
	}
	
}
