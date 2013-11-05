package com.BeyondVerbal.comm.requests;

import android.content.ContentValues;

import com.BeyondVerbal.comm.RequestBaseJson;
import com.BeyondVerbal.comm.data.response.GetRecordingAnalysisResponse;
import com.BeyondVerbal.listener.UpdateListListener;

public class GetRecordingAnalysisJsonRequest extends RequestBaseJson {

	private static final String FROM_MILLI_SECOND_PARAMETER = "fromMS";
	
	UpdateListListener listener;

	public GetRecordingAnalysisJsonRequest(String url) {
		super(url, false, null);
		requestName = "";
	}

	public GetRecordingAnalysisJsonRequest(String baseURL, UpdateListListener listener) 
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
			listener.onAnalysisSampleReceived((GetRecordingAnalysisResponse)responseObject);
		}
	}

	@Override
	public void OnResponseFailed() {

	}
}
