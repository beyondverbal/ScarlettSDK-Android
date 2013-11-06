package com.BeyondVerbal.comm;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;

public class StreamingSocket implements Runnable{

	public interface StreamingListener {
		public void onResponseReceived(byte[] response);
		public void onStreamingStopped();
	}
	
	protected static final String LOGTAG = "NGStreamingSocket";
	private InputStream inputStream;
	
	protected String path;
	
	private StreamingListener listener;
	InputStreamEntity reqEntity;
	
//	public StreamingSocket() {
//	}

	public StreamingSocket(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void open() {
		new Thread(this).start();
	}

/*	public boolean write(byte[] data) {
		boolean result = false;
		if (inputStream != null) {
			try {
				inputStream.write(data);
				Log.i(LOGTAG, "Writing to data stream. length: " + data.length);
				result = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
*/	
	public boolean close() {
		boolean result = false;
		if (listener != null) {
			listener.onStreamingStopped();
		}
		
		try {
			if(inputStream != null){
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		inputStream = null;

		return result;
	}

	@Override
	public void run() {
		
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(path);


            if (inputStream == null) {
                inputStream = new BlockingInputStream();
            }
            
            reqEntity = new InputStreamEntity(inputStream, -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true);

            httppost.setEntity(reqEntity);

            Log.i(LOGTAG, "executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            Log.i(LOGTAG, "request executed: " + response.getStatusLine());
            if (resEntity != null) {
            	Log.d(LOGTAG, "Response content length: " + resEntity.getContentLength());
            	Log.d(LOGTAG, "Chunked?: " + resEntity.isChunked());
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(StreamingListener listener) {
		this.listener = listener;
	}
	
	
}
