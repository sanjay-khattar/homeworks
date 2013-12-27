package com.tilofy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary Servlet Class for photo-processing operations. 
 * 
 * @author sanjayk
 * 
 */
public class PhotoQueueController extends HttpServlet {
	
	public static final String PARAM_NAME_IMG_URL = "url";
	public static final String PARAM_NAME_NEW_SIZE = "size";

	public static final String ERR_MSG_NO_IMG_URL = "No '" + PARAM_NAME_IMG_URL + "' parameter provided...";
	public static final String ERR_MSG_NO_NEW_SIZE = "No '" + PARAM_NAME_NEW_SIZE + "' parameter provided...";
	
	public static final String EMPTY_STRING = "";
	public static final String RESP_MSG_EMPTY = EMPTY_STRING;
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 492247028855735312L;

	private static final Logger logger = LoggerFactory.getLogger(PhotoQueueController.class);
	
	private static final String DEFAULT_MESSAGE_PREFIX = "PhotoQueueController: ";
	
	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd h:mm:ss.SSS a Z";
	
	private ExecutorService executorService;
	
	private Map<String, FutureTask<ImageProcessingResult>> imageProcessingTasksMap;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		
		imageProcessingTasksMap = new HashMap<String, FutureTask<ImageProcessingResult>>();
		executorService = Executors.newCachedThreadPool();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		executorService.shutdown();
		
		super.destroy();
	}

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.trace("Processing HttpServletRequest: " + req);
		
		@SuppressWarnings("rawtypes")
		Map reqParamsMap = req.getParameterMap();
		logger.info("req ParameterMap: " + reqParamsMap);
		
		String strPathInfo = req.getPathInfo();
		logger.info("strPathInfo: " + strPathInfo);
			
		String strJobId = null;
		int respStatus = HttpServletResponse.SC_NOT_FOUND;
		
		String msgPrefix = DEFAULT_MESSAGE_PREFIX + currentDateTimeString() + ": For jobId: ";		
		String respMsg = msgPrefix;
		
		if ((strPathInfo == null) || (strPathInfo.trim().isEmpty()) || (strPathInfo.trim().length() < 1)) {
			respMsg += strJobId + ", Job not found...";
			logger.warn(respMsg);
		}
		else {
			strJobId = strPathInfo.trim().substring(1);
			respMsg += strJobId;		
			FutureTask<ImageProcessingResult> imageProcessingTask = imageProcessingTasksMap.get(strJobId);

			if (imageProcessingTask != null) {
				respMsg += ", found imageProcessingTask...";
				logger.debug(respMsg);
				
				respStatus = HttpServletResponse.SC_OK;
				ImageProcessingResult imageProcessingResult = null;
				TimeUnit waitTimeUnit = TimeUnit.MILLISECONDS;
				int waitTime = 50;
				try {
					imageProcessingResult = imageProcessingTask.get(50,
							waitTimeUnit);
					logger.info("Got imageProcessingResult: " + imageProcessingResult);
					String resultMsg = imageProcessingResult.getResultMsg();
					respMsg += ", Got resultMsg: " + resultMsg;
					logger.info(respMsg);
				} catch (TimeoutException toe) {
					respMsg += ", timed out after waiting "
							+ waitTime + " " + waitTimeUnit.toString()
							+ " to get job status...Try again...";
					logger.info(respMsg);
				} catch (Exception e) {
					respStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
					respMsg += ", Exception occurred getting imageProcessingResult: " + e;
					logger.error(respMsg, e);
				}
			}
			else {
				respMsg += ", no Image Processing Task found...";
				logger.info(respMsg);
			}			
		}
		
		resp.setStatus(respStatus);
		resp.getWriter().println(respMsg);
		
		logger.trace("Returning HttpServletResponse: " + resp);
    }


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.trace("Processing HttpServletRequest: " + req);
		
		@SuppressWarnings("rawtypes")
		Map reqParamsMap = req.getParameterMap();
		logger.info("req ParameterMap: " + reqParamsMap);
		
		String strImgUrl = req.getParameter(PARAM_NAME_IMG_URL);
		String strNewSize = req.getParameter(PARAM_NAME_NEW_SIZE);
		
		
		String msgPrefix = getMsgPrefix();
		String errMsg = msgPrefix;
		int errRespStatus = HttpServletResponse.SC_NOT_FOUND;
		
		if ((strImgUrl == null) || (strImgUrl.trim().isEmpty())) {
			errMsg += ERR_MSG_NO_IMG_URL;
			resp.sendError(errRespStatus, errMsg);
			return;
		}
		
		if ((strNewSize == null) || (strNewSize.trim().isEmpty())) {
			errMsg += ERR_MSG_NO_NEW_SIZE;
			resp.sendError(errRespStatus, errMsg);
			return;
		}

		UUID uuid = UUID.randomUUID();
		String jobId = uuid.toString();
		
		int respStatus = HttpServletResponse.SC_ACCEPTED;
		JobStatus jobStatus = JobStatus.PROCESSING;

		final ImageProcessingRequest imageProcessingRequest = new ImageProcessingRequest(jobId, strImgUrl, strNewSize);
		final ImageProcessingResult imageProcessingResult = new ImageProcessingResult(imageProcessingRequest, jobStatus);
		
		String resultMsg = getMsgPrefix();
		resultMsg += "Accepted imageProcessingRequest: " + imageProcessingRequest;
		imageProcessingResult.setResultMsg(resultMsg);
				
		FutureTask<ImageProcessingResult> imageProcessingTask = new FutureTask<ImageProcessingResult>(
				new Runnable() {

					public void run() {
						logger.debug("Running imageProcessingTask: " + imageProcessingRequest);
						
						JobStatus jobStatus = imageProcessingResult.getJobStatus();
						String resultMsg = getMsgPrefix();
						try {
							String jobId = imageProcessingRequest.getJobId();
							String strImgUrl = imageProcessingRequest.getStrImgUrl();
							String strNewSize = imageProcessingRequest.getStrNewSize();

							resultMsg += "Running imageProcessingRequest: "
									+ imageProcessingRequest + ", jobStatus: " + jobStatus;
							imageProcessingResult.setResultMsg(resultMsg);
							
							ImageProcessor imageProcessor = new ImageProcessor();
							String resizedImageFilePath = imageProcessor
									.resizeImage(jobId, strImgUrl, strNewSize);
							imageProcessingResult
									.setResizedImageFilePath(resizedImageFilePath);
							jobStatus = JobStatus.SUCCESS;
							
							resultMsg = getMsgPrefix();
							resultMsg += "Finished imageProcessingRequest: "
									+ imageProcessingRequest + ", jobStatus: " + jobStatus
									+ ", resizedImageFilePath: " + resizedImageFilePath;
							logger.info(resultMsg);
						} catch (Exception e) {
							String excpMsg = getMsgPrefix();
							excpMsg += "Exception while processing image task. ImageProcessingRequest: "
									+ imageProcessingRequest
									+ " Exception: "
									+ e;
							jobStatus = JobStatus.FAILED;
							resultMsg = excpMsg + ", jobStatus: " + jobStatus;
							logger.error(excpMsg, e);
						}
						
						imageProcessingResult.setJobStatus(jobStatus);
						imageProcessingResult.setResultMsg(resultMsg);
					}
					
				}, imageProcessingResult);
		
		imageProcessingTasksMap.put(jobId, imageProcessingTask);
		logger.info("Put imageProcessingTask: " + imageProcessingTask + " for jobId: " + jobId);
		
		executorService.execute(imageProcessingTask);
		
		resp.setStatus(respStatus);

		String respMsg = imageProcessingResult.getResultMsg();

		resp.getWriter().println(respMsg);
		
		logger.trace("Returning HttpServletResponse: " + resp);

	}
	
	private static String getMsgPrefix() {
		String msgPrefix = DEFAULT_MESSAGE_PREFIX + currentDateTimeString() + ": ";	
		
		return msgPrefix;
	}
	private static String currentDateTimeString() {
		String currentDateTimeString = dateTimeString(new Date());
		
		return currentDateTimeString;
	}
	
	private static String dateTimeString(Date date) {		
		String dateTimeString = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		dateTimeString = sdf.format(date);
		
		return dateTimeString;		
	}
	
	
	
}
