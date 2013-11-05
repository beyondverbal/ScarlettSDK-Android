package com.BeyondVerbal.comm;


import com.google.gson.Gson;


public abstract class RequestBaseJson extends RequestBase {

	protected Object responseObject;
	
	public RequestBaseJson(String baseUrl, boolean requestIsPost, byte[] body) {
		super(baseUrl, requestIsPost, body);
		// TODO Auto-generated constructor stub
	}

	protected abstract void OnResponseReceived();
	public abstract void OnResponseFailed();
	
	protected boolean ParseResponseData(String responseData) {
		if (responseData!=null)
		{
			Gson gson = new Gson();
			responseObject = gson.fromJson(responseData, responseObject.getClass());
			OnResponseReceived();
			
			return true;
		}
		return false;
	}

}
