package com.BeyondVerbal.listener;

import com.BeyondVerbal.comm.data.response.GetRecordingAnalysisResponse;

public interface UpdateListListener {
	
	public void onAnalysisSampleReceived(GetRecordingAnalysisResponse response);
	public void respondFailed();

}
