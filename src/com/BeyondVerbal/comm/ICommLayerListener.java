package com.BeyondVerbal.comm;

public interface ICommLayerListener {

	/** Handle the response using the response string data 
	 * @param cookies */
	public void onHttpResponseReceived(byte[] responseBody);
		
	/*
	 * In case an error occurred when sending request 
	 * or receiving the response the class will
	 * notify the caller of the error
	 * */
	void OnResponseFailed();
	
}