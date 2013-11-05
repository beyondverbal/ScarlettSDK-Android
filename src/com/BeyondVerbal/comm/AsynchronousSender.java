package com.BeyondVerbal.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;


import android.util.Log;
 
public class AsynchronousSender extends Thread {

	private static final  String LOGTAG="NGAsynchronousSender";
	
	private final DefaultHttpClient httpClient = new DefaultHttpClient();
 
	private HttpRequest  request;
	private ICommLayerListener callback;
//	private boolean isError;
	 
	protected AsynchronousSender(HttpRequest request, ICommLayerListener callback) {
		this.request  = request;
		this.callback = callback;
	}
 
	public void run() {
//		isError = false;
		HttpRequest httpRequest = request;
		try {
			byte[] responseBody;
			final HttpResponse response;
			synchronized (httpClient) {
				
				response = httpClient.execute((HttpUriRequest) httpRequest);
				// get the body (must be inside the synchronized section)
				responseBody = getResponseBody(response);
			}
			
			if (responseBody != null){
				// process response
//				isError = true;
				callback.onHttpResponseReceived(responseBody);
			} else {
				Log.i(LOGTAG, "Error OnResponseFailed");
				callback.OnResponseFailed();
			}
		}
		catch (ClientProtocolException e) {
			// TODO: test if getting redirect when logged in then need to logout 
			if (e.getCause() instanceof CircularRedirectException) {
				Log.i(LOGTAG, "CircularRedirectException");
			}
			Log.e(LOGTAG, "ClientProtocolException" , e);
		}
		catch (IOException e) {
			Log.e(LOGTAG, "IOException" , e);
		}
		catch (Exception e) {
			Log.e(LOGTAG, "exception" , e);
		}

//		if (isError)
//		{
//			Log.i(LOGTAG, "Error OnResponseFailed");
//			callback.OnResponseFailed();
//		}
	}
	
	public byte[] getResponseBody(HttpResponse response) {

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
        		if(status == 200) {
        			if (entity.getContentLength() > 0) {
        				buff = new byte[(int) entity.getContentLength()];
        				inContent.read(buff);
        			} else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inContent));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = null;

                        while ((line = reader.readLine()) != null) {
                          stringBuilder.append(line);
                        }

                        String str = stringBuilder.toString();
                 		buff = str.getBytes();
            			Log.e(LOGTAG, str);
        			}
        		} else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inContent));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                      stringBuilder.append(line);
                    }

                    String str = stringBuilder.toString();
        			Log.e(LOGTAG, str);
        			return null;
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
        	catch (Throwable e) {
        		Log.e(LOGTAG, "Exception: " + e.getLocalizedMessage(), e);
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
	
}