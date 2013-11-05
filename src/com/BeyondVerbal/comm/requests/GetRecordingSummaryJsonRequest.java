package com.BeyondVerbal.comm.requests;

import com.BeyondVerbal.comm.RequestBaseJson;
import com.BeyondVerbal.comm.data.response.GetRecordingAnalysisResponse;
import com.BeyondVerbal.listener.RecordSummaryListener;

import android.content.ContentValues;

public class GetRecordingSummaryJsonRequest extends RequestBaseJson {

	RecordSummaryListener listener;

	public GetRecordingSummaryJsonRequest(String url) {
		super(url, false, null);
		requestName = "";
	}

	public GetRecordingSummaryJsonRequest(String baseURL, RecordSummaryListener listener) 
	{
		super(baseURL, false, null);
		this.listener = listener;
		requestName = "";
	}

	public void setParams() {
		
		if (requestparams != null)
			requestparams = null;
		
		requestparams = new ContentValues();
		
		responseObject = new GetRecordingAnalysisResponse();
	}

	@Override
	protected void OnResponseReceived() {
		if (listener != null) {
			listener.onRecordSummary((GetRecordingAnalysisResponse) responseObject);
		}
	}

	@Override
	public void OnResponseFailed() {

	}
}
