package com.BeyondVerbal.comm.data.response;

public class GetRecordingAnalysisResponse {
	public String status;
	public ResponseResult result;
	public FollowupAction followupActions;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetRecordingAnalysisResponse [status=" + status + ", result="
				+ result + ", followupActions=" + followupActions + "]";
	}
	
	
}
