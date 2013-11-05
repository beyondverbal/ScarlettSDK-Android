package com.BeyondVerbal.comm.requests;

import java.util.List;

import org.apache.http.cookie.Cookie;
import android.content.ContentValues;

import com.BeyondVerbal.comm.RequestBaseJson;
import com.BeyondVerbal.comm.data.request.StartRequestBody;
import com.BeyondVerbal.comm.data.response.StartResponse;
import com.BeyondVerbal.listener.StartRecordListener;
import com.google.gson.Gson;

public class StartRecordJsonRequest extends RequestBaseJson {

	private static final String REQUEST_NAME = "/recording/start";
	
	private static final String API_KEY_PARAMETER = "api_key";
	StartRecordListener listener;

	public StartRecordJsonRequest(String baseUrl, boolean requestIsPost, byte[] body, StartRecordListener listener) {
		super(baseUrl, requestIsPost, body);
		requestName = REQUEST_NAME;
		this.listener = listener;
	}

	public void setParams(String apiKey, StartRequestBody requestBody) {
		
		if (requestparams != null)
			requestparams = null;
		
		requestparams = new ContentValues();
		requestparams.put(API_KEY_PARAMETER, apiKey);
		
		Gson gson = new Gson();
		String json = gson.toJson(requestBody);
		body = json.getBytes();

		responseObject = new StartResponse();

	}
	@Override
	protected void OnResponseReceived() {
		if (listener != null) {
			listener.onRecordStarted((StartResponse) responseObject);
		}
	}

	@Override
	public void OnResponseFailed() {
		// TODO Auto-generated method stub

	}


}
