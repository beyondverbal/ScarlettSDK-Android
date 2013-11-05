package com.BeyondVerbal.session.wrapper;

import com.BeyondVerbal.comm.data.request.DataFormat;
import com.BeyondVerbal.comm.data.request.PersonInfo;
import com.BeyondVerbal.session.wrapper.Session.eAnalysisType;

public class AnalysisParameters {
	
	public DataFormat dataFormat;
	public PersonInfo recorderInfo;
	public PersonInfo subjectInfo;
	public eAnalysisType[] analysisTypes;
	public int analysisPollingInterval;

	public AnalysisParameters() {
		dataFormat = new DataFormat();
		recorderInfo = new PersonInfo();
		subjectInfo = new PersonInfo();
		analysisTypes = new eAnalysisType[8];
	}

}
