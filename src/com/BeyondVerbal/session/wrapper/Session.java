package com.BeyondVerbal.session.wrapper;

import java.io.InputStream;
import java.util.ArrayList;

import com.BeyondVerbal.comm.data.request.DataFormat;
import com.BeyondVerbal.comm.data.request.PersonInfo;
import com.BeyondVerbal.comm.data.response.AnalysisSegment;
import com.BeyondVerbal.comm.data.response.DualValue;
import com.BeyondVerbal.session.wrapper.CommunicationSession.OnCommunicationSessionListener;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class Session implements OnCommunicationSessionListener {

	public interface SessionListener {
		void onAnalysisResults(AnalysisSegment[] samples);
		void onSummary(DualValue summary);
		void onError(int errorCode, String errorMessage);
		void onSessionStopped(int reason);
	}
	
	interface OnSessionInitListener {
		public void onSessionInitialized();
		public void onSessionError(int errorCode, String ErrorMessage);
	}
	
	public enum eAnalysisType {
		TemperValue,
		TemperMeter,
		ComposureMeter,
		CooperationLevel,
		ServiceScore,
		CompositMood,
		MoodGroup,
		MoodGroupSummary
	}
	
	public enum eModeType{
		ModeInCall,
		ModeOutCall,
		ModeRecord
	}
	
	private static final int DEFAULT_POLLING_INTERVAL = 6000;
	private static final int SAMPLE_RATE = 8000;
	private static final int CHANNELS = 1;
	private static final int BITS_PER_SAMPLE = 16;
	private static final String BASE_URL = "https://beta.beyondverbal.com/v1";
	private TelephonyManager telephony;
	private SessionListener sessionListener;
	private String apiKey;

	/**
	 * @param sessionListener the sessionListener to set
	 */
	public void setSessionListener(SessionListener sessionListener) {
		this.sessionListener = sessionListener;
	}

	CommunicationSession commSession;
	CommListener commListener;
	OnSessionInitListener sessionInitListener;
	
	private DataFormat dataFormat;
	private PersonInfo recorderInfo;
	private PersonInfo subjectInfo;
	private String[] analysisTypes;
	private int analysisPollingInterval;

	Session(Context context, String apiKey, OnSessionInitListener sessionInitListener) {
		this.sessionInitListener = sessionInitListener;
		this.apiKey = apiKey;


		telephony = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

		analysisPollingInterval = DEFAULT_POLLING_INTERVAL;
		
		commSession = new CommunicationSession(context, analysisPollingInterval);
		initData();
	}

	private void initData() {
		dataFormat = new DataFormat();
		dataFormat.type = "pcm";
		dataFormat.sample_rate = SAMPLE_RATE;
		dataFormat.channels = CHANNELS;
		dataFormat.bits_per_sample = BITS_PER_SAMPLE;
		
		recorderInfo = new PersonInfo();
		recorderInfo.device_info = getDeviceName();
		recorderInfo.device_id = telephony.getDeviceId();

	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return model;
		} else {
			return manufacturer + " " + model;
		}
	}
	
	

	
	boolean Start() {
		commListener = new CommListener();
		commSession.setListener(commListener);
		commSession.setSessionListener(this);
		commSession.startSession(BASE_URL, apiKey, dataFormat, recorderInfo, analysisTypes);

		return true;
	}

	public boolean Stop() {
		
		commSession.stopSession(BASE_URL, apiKey);
		return true;
	}

/*	public void sendVoice(byte[] buffer){
		byte[] newBuffer;
		//send voice to the server
		if (recordingStarted == true) {
			if (firstPacket) {
				firstPacket = false;
				newBuffer = new byte[buffer.length + initialBufferSize];
				int pos = 0;
				for (int i = 0; i < initialBuffer.size(); i++) {
					System.arraycopy(initialBuffer.get(i), 0, newBuffer, pos, initialBuffer.get(i).length);
					pos += initialBuffer.get(i).length;
				}
				System.arraycopy(buffer, 0, newBuffer, pos, buffer.length);

				initialBuffer.clear();
				initialBufferSize = 0;
			} else {
				newBuffer = buffer;
			}
			
			commSession.sendVoice(newBuffer);
			
		} else {
			byte[] temp = new byte[buffer.length];
			System.arraycopy(buffer, 0, temp, 0, buffer.length);
			initialBuffer.add(temp);
			initialBufferSize += temp.length;
			if (initialBufferSize > INITIAL_BUFFER_LIMIT) {
				if (sessionListener != null) {
					sessionListener.onError(7, "buffer Overflow.");
					initialBuffer.clear();
					initialBufferSize = 0;
				}
			}
		}
	}
	
*/	
	public boolean requestSessionSummary() {
		return commSession.requestSessionSummary();
	}
	
	
	
	/**
	 * @return the recorderInfo
	 */
	PersonInfo getRecorderInfo() {
		return recorderInfo;
	}

	/**
	 * @param recorderInfo the recorderInfo to set
	 */
	void setRecorderInfo(PersonInfo recorderInfo) {
		this.recorderInfo = recorderInfo;
		//this.recorderInfo.device_info = "Web";
		recorderInfo.device_info = getDeviceName();
		recorderInfo.device_id = telephony.getDeviceId();
	}

	/**
	 * @param analysisOptions the analysisOptions to set
	 */
	void setAnalysisTypes(eAnalysisType[] types) {
		this.analysisTypes = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			analysisTypes[i] = types[i].name();
		}
	}

	/**
	 * @return the subjectInfo
	 */
	PersonInfo getSubjectInfo() {
		return subjectInfo;
	}

	/**
	 * @param subjectInfo the subjectInfo to set
	 */
	void setSubjectInfo(PersonInfo subjectInfo) {
		this.subjectInfo = subjectInfo;
	}

	public void analyze(InputStream stream) {
		commSession.analyze(stream);
	}
	
	class CommListener implements CommunicationSession.OnCommunicationListener {

		@Override
		public void onAnalysisReceived(AnalysisSegment[] samples) {
			if (sessionListener != null) {
				sessionListener.onAnalysisResults(samples);
			}
		}

		@Override
		public void onSummaryReceived(DualValue summary) {
			if (sessionListener != null) {
				sessionListener.onSummary(summary);
			}
		}
		
	}
	
	/**
	 * @param dataFormat the dataFormat to set
	 */
	public void setDataFormat(DataFormat dataFormat) {
		this.dataFormat = dataFormat;
	}

	/**
	 * @param analysisPollingInterval the analysisPollingInterval to set
	 */
	public void setAnalysisPollingInterval(int analysisPollingInterval) {
		this.analysisPollingInterval = analysisPollingInterval;
		if (commSession != null) {
			commSession.setAnalysisPollingInterval(analysisPollingInterval);
		}
	}

	@Override
	public void onSessionStarted() {
		if (sessionInitListener != null) {
			sessionInitListener.onSessionInitialized();
		}
		
	}

	@Override
	public void onSessionError(int errorCode, String ErrorMessage) {
		if (sessionInitListener != null) {
			sessionInitListener.onSessionError(0, "Failed to initialized Session");
		}
		
	}

	
}
