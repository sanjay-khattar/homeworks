package com.tilofy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class PhotoQueueControllerTest {
	
	@Test
	public void testMissingJobId() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		String missingJobId = "";
		when(request.getPathInfo()).thenReturn(missingJobId);

		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        when(response.getWriter()).thenReturn(printWriter);
        
        PhotoQueueController photoQueueController = new PhotoQueueController();
		photoQueueController.init();
		photoQueueController.doGet(request, response);
		
		printWriter.flush();
		
		String expectedResponseMsg = "Job not found...";
		
		String responseStr = stringWriter.toString();
		String message = "Response expected to contain: " + expectedResponseMsg + " Actual response: " + responseStr;
		assertTrue(message, responseStr.contains(expectedResponseMsg));
		
	}

	@Test
	public void testUnknownJobId() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		String nonExistentJobId = "1100";
		when(request.getPathInfo()).thenReturn("/" + nonExistentJobId);
		
		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        when(response.getWriter()).thenReturn(printWriter);
        
        PhotoQueueController photoQueueController = new PhotoQueueController();
		photoQueueController.init();
		photoQueueController.doGet(request, response);
		
		printWriter.flush();
		
		String expectedResponseMsg = "no Image Processing Task found...";
		
		String responseStr = stringWriter.toString();
		String message = "Response expected to contain: " + expectedResponseMsg + " Actual response: " + responseStr;
		assertTrue(message, responseStr.contains(expectedResponseMsg));		
		
	}

	@Test
	public void testMissingImgUrl() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		String paramNameImgUrl = PhotoQueueController.PARAM_NAME_IMG_URL;
		String paramNameNewSize = PhotoQueueController.PARAM_NAME_NEW_SIZE;
		String testParamValueImgUrl = "";
		String testParamValueNewSize = "800x600";
		
		when(request.getParameter(paramNameImgUrl)).thenReturn(testParamValueImgUrl);
		when(request.getParameter(paramNameNewSize)).thenReturn(testParamValueNewSize);
		
		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        when(response.getWriter()).thenReturn(printWriter);
        
        PhotoQueueController photoQueueController = new PhotoQueueController();
		photoQueueController.init();
		photoQueueController.doPost(request, response);
		
		verify(request, atLeast(1)).getParameter(paramNameImgUrl); 
		verify(request, atLeast(1)).getParameter(paramNameNewSize); 
		printWriter.flush();
		
		String expectedResponseMsg = PhotoQueueController.RESP_MSG_EMPTY;
		
		String responseStr = stringWriter.toString();
		String message = "Response expected to contain: " + expectedResponseMsg + " Actual response: " + responseStr;
		assertTrue(message, responseStr.contains(expectedResponseMsg));		
		
	}

	@Test
	public void testMissingNewSize() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		String paramNameImgUrl = PhotoQueueController.PARAM_NAME_IMG_URL;
		String paramNameNewSize = PhotoQueueController.PARAM_NAME_NEW_SIZE;
		String testParamValueImgUrl = "test.jpg";
		String testParamValueNewSize = "";
		
		when(request.getParameter(paramNameImgUrl)).thenReturn(testParamValueImgUrl);
		when(request.getParameter(paramNameNewSize)).thenReturn(testParamValueNewSize);
		
		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        when(response.getWriter()).thenReturn(printWriter);
        
        PhotoQueueController photoQueueController = new PhotoQueueController();
		photoQueueController.init();
		photoQueueController.doPost(request, response);
		
		verify(request, atLeast(1)).getParameter(paramNameImgUrl); 
		verify(request, atLeast(1)).getParameter(paramNameNewSize); 
		printWriter.flush();
		
		String expectedResponseMsg = PhotoQueueController.RESP_MSG_EMPTY;
		
		String responseStr = stringWriter.toString();
		String message = "Response expected to contain: " + expectedResponseMsg + " Actual response: " + responseStr;
		assertTrue(message, responseStr.contains(expectedResponseMsg));		
		
	}
	
}
