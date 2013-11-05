package com.BeyondVerbal.listener;

import com.BeyondVerbal.comm.data.response.GetRecordingAnalysisResponse;

public interface RecordSummaryListener {
	public void onRecordSummary(GetRecordingAnalysisResponse response);
}
