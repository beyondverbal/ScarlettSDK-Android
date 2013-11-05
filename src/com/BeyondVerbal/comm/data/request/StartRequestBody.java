package com.BeyondVerbal.comm.data.request;

public class StartRequestBody {

	public DataFormat data_format;
	public PersonInfo recorder_info;
	public String[] requiredAnalysisTypes;
	
	public StartRequestBody() {
		data_format = new DataFormat();
		recorder_info = new PersonInfo();
	}

	/**
	 * @return the data_format
	 */
	public DataFormat getDataFormat() {
		return data_format;
	}

	/**
	 * @param data_format the data_format to set
	 */
	public void setDataFormat(DataFormat data_format) {
		this.data_format = data_format;
	}

	/**
	 * @return the recorder_info
	 */
	public PersonInfo getRecorderInfo() {
		return recorder_info;
	}

	/**
	 * @param recorder_info the recorder_info to set
	 */
	public void setRecorderInfo(PersonInfo recorder_info) {
		this.recorder_info = recorder_info;
	}

	/**
	 * @return the requiredAnalysisTypes
	 */
	public String[] getRequiredAnalysisTypes() {
		return requiredAnalysisTypes;
	}

	/**
	 * @param requiredAnalysisTypes the requiredAnalysisTypes to set
	 */
	public void setRequiredAnalysisTypes(String[] requiredAnalysisTypes) {
		this.requiredAnalysisTypes = requiredAnalysisTypes;
	}

}
