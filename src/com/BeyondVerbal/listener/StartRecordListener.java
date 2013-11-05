package com.BeyondVerbal.listener;

import java.util.List;

import org.apache.http.cookie.Cookie;

import com.BeyondVerbal.comm.data.response.StartResponse;

public interface StartRecordListener {
	
	public void onRecordStarted(StartResponse response);
	
	public void onStartRecordFailed();

}
