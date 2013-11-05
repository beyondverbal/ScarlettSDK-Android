package com.BeyondVerbal.comm.data.response;

import java.util.Arrays;

public class ResponseResult {
	public int duration; 
	public AnalysisSegment[] analysisSegments;
	public AnalysisItems analysisItems;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResponseResult [duration=" + duration + ", analysisSegments="
				+ Arrays.toString(analysisSegments) + ", analysisItems="
				+ analysisItems + "]";
	}
}
