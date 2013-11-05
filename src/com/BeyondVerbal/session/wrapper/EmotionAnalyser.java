package com.BeyondVerbal.session.wrapper;

import com.BeyondVerbal.session.wrapper.Session.OnSessionInitListener;

import android.content.Context;

public class EmotionAnalyser implements OnSessionInitListener {

	public interface SessionInitListener {
		void onSessioninitialized(Session session);
		void onSessioninitializedFailed(int errorCode, String errorMessage);
	}
	
	private SessionInitListener listener;
	private Session session;
	
	public EmotionAnalyser() {
		// TODO Auto-generated constructor stub
	}
	
	public void InitiateSession(Context context, String apiKey, AnalysisParameters params, SessionInitListener listener) {
		this.listener = listener;
		session = new Session(context, apiKey, this);
		session.setAnalysisPollingInterval(params.analysisPollingInterval);
		session.setDataFormat(params.dataFormat);
		session.setAnalysisTypes(params.analysisTypes);
		session.setRecorderInfo(params.recorderInfo);
		session.setSubjectInfo(params.subjectInfo);
		session.Start();
	}
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(SessionInitListener listener) {
		this.listener = listener;
	}

	@Override
	public void onSessionInitialized() {
		if (listener != null) {
			listener.onSessioninitialized(session);
		}
	}

	@Override
	public void onSessionError(int errorCode, String errorMessage) {
		if (listener != null) {
			listener.onSessioninitializedFailed(errorCode, errorMessage);
		}
	}

	
}
