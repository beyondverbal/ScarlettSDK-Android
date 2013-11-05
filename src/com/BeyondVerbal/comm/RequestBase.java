package com.BeyondVerbal.comm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

//import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;


import android.content.ContentValues;
import android.util.Log;

//The class provides built-in methods to create XML files.
public abstract class RequestBase implements ICommLayerListener {

	protected static final String LOGTAG = "NGRequest";
	
	protected boolean requestIsPost;
	protected String baseUrl;
	
	
	protected byte[] body;
	protected String requestName;
	protected ContentValues requestparams;
	
	// is request canceled flag
	protected boolean canceled = false;
	
	public RequestBase(String baseUrl,boolean requestIsPost, byte[] body) {
		this.baseUrl = baseUrl;
		this.requestIsPost = requestIsPost;
		this.body = body;
	}
	
	public String getUrl(String serverUrl) {
		
		//URL = BaseURL + REQUEST NAME + ? + PARAM1 + & + + PARAM2 + & + ...
		String url = serverUrl + requestName;
		
		if (requestparams != null && requestparams.size() > 0) {
			if (!url.contains("?")) {
				url += "?";
			} else {
				url += "&";
			}
		
			// get the requests params
			Set<Entry<String, Object>> valueSet = requestparams.valueSet();				
			for (Iterator<Entry<String, Object>> iter = valueSet.iterator(); iter.hasNext();) {
				Entry<String, Object> entry = (Entry<String, Object>) iter.next();
				String key = entry.getKey();
				String value = (String)entry.getValue();
				url += key+"="+value;
				if (iter.hasNext()) {
					url += "&";
				}
			}
		}
		
		return url;
//		return Uri.encode(url);
	}
	
	private String buildHttpRequestUrl(String serverUrl)
	{
		String url=null;
		url=getUrl(serverUrl); 
		return url;
	}
	
	public byte[] getResponseBody(HttpResponse response) 
	{

		byte[] buff = null;
		
		if (response != null)	{ 
			
			int status = response.getStatusLine().getStatusCode();
			Log.d(LOGTAG, "getResponseBody status = " + status);
	       
        	/* Read  the data  */
        	InputStream inContent = null;
        	try {
        		HttpEntity entity = response.getEntity();
        		if (entity == null)
        			return null;
        		
        		inContent = entity.getContent();
        		buff = new byte[inContent.available()];
        		inContent.read(buff);
        		//buff = IOUtils.toByteArray(inContent);
        		if(status != 200) {
        			// if status is not 200, send null response
        			buff = null;
        		}
        		
        	} 
        	catch (IOException e) {  
        		Log.e(LOGTAG, "IOException: ", e);
        		e.printStackTrace();     		
        	}  
        	catch (RuntimeException e) 
        	{  	         
        		Log.e(LOGTAG, "Exception: ", e);
        		e.printStackTrace();
        	} 
           	catch (Exception e) 
        	{  	         
           		Log.e(LOGTAG, "Exception: ", e);
        		e.printStackTrace();
        	} 
        	finally {
     			try {
     				if (inContent != null)
     					inContent.close();
     			} 
     			catch (IOException e) {
     				Log.e(LOGTAG, "IOException: ", e);
     	    	}			
     		}
        }	
					
		return buff;
	}
	
	//Send the request to the server.
	public void SendASyncRequest()
	{	
		String url = buildHttpRequestUrl(baseUrl);
//		String alternateURL = buildHttpRequestUrl(alternateUrl);
		Log.i(LOGTAG, "SendRequest with URL = " +url);

		if (url == null || url.length()<=0)
		{
			Log.e(LOGTAG, "dataString is empty");
			OnResponseFailed();
			return;
		}
		
		HttpRequest request = null;
		try
		{  		
			// POST requests to the secure server, GET requests to the public server
			if (requestIsPost){
				request = new HttpPost(url);
				if (body != null) {
					InputStreamEntity reqEntity = new InputStreamEntity(  new ByteArrayInputStream(body), -1);
					Log.i(LOGTAG, "SendRequest Body: " + new String(body));
					
					reqEntity.setContentType("application/json");
		            reqEntity.setChunked(true);

		            ((HttpPost) request).setEntity(reqEntity);
				}
			}
			
			else
				request = new HttpGet(url);
			
		} 
		catch (Exception e) 
		{  	         
			Log.e(LOGTAG, "Exception: ", e);
			e.printStackTrace(); 
			OnResponseFailed();
		}

		
		new AsynchronousSender(request, this).start();
	}
		
	/** Cancel current request */
	public void cancel(){
		Log.d(LOGTAG, "cancel request");
		canceled = true;
	}

	//response to server
	public void onHttpResponseReceived(byte[] response)
	{		
		Log.d(LOGTAG, "onHttpResponseReceived");
		
		// ignore the response if the request was canceled or the handler is null
		if (canceled) // || NGCommLayer.getInstance().getUIHandle()==null)
			return;
				
		if (response==null)	
		{			
			OnResponseFailed();
		}
		else	
		{ 		
			ParseResponseBody(response);				
		}		
	}
	
	/** get the encoded string from the byte array */
	protected String getEncodedString(byte[] response) {
		// encode bytes to String
		String encodeSet;
		String responseData = null;
			        			
		encodeSet = getEncodinCharSet();
		
		try {
			responseData = new String(response, encodeSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		
		Log.i(LOGTAG, "-----------------------------------------------");
		Log.i(LOGTAG, "Response DATA:");
		Log.i(LOGTAG, responseData);
		Log.i(LOGTAG, "-----------------------------------------------");

		return responseData;
	}
	
	// This function can be override if a derived class want to change the encoding char set
	protected String getEncodinCharSet(){
		//return (NGCommLayer.getInstance().isLogIn() ?  "UTF-8" : "iso-8859-8");
//		return "iso-8859-8";
		return "UTF-8";
	}
	
	// Parse the response string
	protected boolean ParseResponseBody(byte[] responseBody)
	{
		boolean bRes = true;
		
		if (responseBody == null) {			
			OnResponseFailed();
			return bRes;
		}	
						
		// get the encoded string
		String responseData = getEncodedString(responseBody);
		bRes = ParseResponseData(responseData);
		
		return bRes;
	}
	
	protected abstract boolean ParseResponseData(String responseData);
}
