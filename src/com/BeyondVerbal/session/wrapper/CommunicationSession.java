package com.BeyondVerbal.session.wrapper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.BeyondVerbal.comm.BlockingInputStream;
import com.BeyondVerbal.comm.CommLayer;
import com.BeyondVerbal.comm.data.request.DataFormat;
import com.BeyondVerbal.comm.data.request.PersonInfo;
import com.BeyondVerbal.comm.data.request.StartRequestBody;
import com.BeyondVerbal.comm.data.response.AnalysisSegment;
import com.BeyondVerbal.comm.data.response.DualValue;
import com.BeyondVerbal.comm.data.response.GetRecordingAnalysisResponse;
import com.BeyondVerbal.comm.data.response.StartResponse;
import com.BeyondVerbal.comm.requests.GetRecordingAnalysisJsonRequest;
import com.BeyondVerbal.comm.requests.GetRecordingSummaryJsonRequest;
import com.BeyondVerbal.comm.requests.RecordingVoiceRequest;
import com.BeyondVerbal.comm.requests.StartRecordJsonRequest;
import com.BeyondVerbal.listener.RecordSummaryListener;
import com.BeyondVerbal.listener.StartRecordListener;
import com.BeyondVerbal.listener.StopRecordListener;
import com.BeyondVerbal.listener.UpdateListListener;

import android.content.Context;

public class CommunicationSession implements StartRecordListener, UpdateListListener, StopRecordListener, RecordSummaryListener {

	interface OnCommunicationListener {
		public void onAnalysisReceived(AnalysisSegment[] samples);
		public void onSummaryReceived(DualValue summary);
	}
	
	interface OnCommunicationSessionListener {
		public void onSessionStarted();
		public void onSessionError(int errorCode, String ErrorMessage);
	}
	
	static String[] ANALYSIS_TYPES = new String[] {"TemperValue", "TemperMeter", "ComposureMeter", "CooperationLevel",
												   "ServiceScore", "CompositMood", "MoodGroup", "MoodGroupSummary"}; 

	String recordingId;
	boolean recordingStarted;
	Context context;
	ArrayList<byte[]> initialBuffer;
	int initialBufferSize;
	RecordingVoiceRequest recordingVoice;
	String baseUrl;
	String upStream;
	String analysisUrl;
	String summaryUrl;
	
	OnCommunicationListener listener;
	OnCommunicationSessionListener sessionListener;
	Timer timer;
	int analysisPollingInterval;
	/**
	 * @param analysisPollingInterval the analysisPollingInterval to set
	 */
	public void setAnalysisPollingInterval(int analysisPollingInterval) {
		this.analysisPollingInterval = analysisPollingInterval;
	}

	boolean ongoingRequest;
	
	public void setListener(OnCommunicationListener listener) {
		this.listener = listener;
	}

	public void setSessionListener(OnCommunicationSessionListener listener) {
		this.sessionListener = listener;
	}
	
	public CommunicationSession(Context context, int analysisPollingInterval) {
		this.context = context;
//		telephony = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
		recordingStarted = false;
		recordingVoice = null;
		timer = null;
		this.analysisPollingInterval = analysisPollingInterval;
	}

	public void startSession(String baseUrl, String apiKey, DataFormat dataFormat, PersonInfo recorderInfo, 
							 String[] analysisTypes) {

		this.baseUrl = baseUrl;
		
		StartRecordJsonRequest jsonRequest = new StartRecordJsonRequest(baseUrl, true, null, this);
		StartRequestBody reqBody = new StartRequestBody();

		reqBody.setDataFormat(dataFormat);
		reqBody.setRecorderInfo(recorderInfo);
		reqBody.setRequiredAnalysisTypes(analysisTypes);
		
		jsonRequest.setParams(apiKey, reqBody);		
 		CommLayer.getInstance(context).SendRequestASync(jsonRequest);
	
		recordingStarted = false;
	}
	
/*	public void sendVoice(byte[] buffer){

		recordingVoice.write(buffer);
	}
	
*/	public void stopSession(String baseUrl, String apiKey, byte[] buffer) {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (recordingVoice != null) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
		 	 		recordingVoice.close();
				}
			}).start();
 		}
		
 		GetRecordingAnalysisJsonRequest request = new GetRecordingAnalysisJsonRequest(analysisUrl, CommunicationSession.this);
 		request.setParams();
 		CommLayer.getInstance(context).SendRequestASync(request);
 		
	}

	public boolean requestSessionSummary() {
		boolean requestSent = false;
		if (summaryUrl != null && summaryUrl.length() > 0) {
	 		GetRecordingSummaryJsonRequest summaryRequest = new GetRecordingSummaryJsonRequest(summaryUrl, this);
	 		summaryRequest.setParams();
	 		CommLayer.getInstance(context).SendRequestASync(summaryRequest);
	 		requestSent = true;
		}
		
		return requestSent;
	}
	
	public boolean isSessionStarted() {
		return recordingStarted;
	}
	

	@Override
	public void onRecordStarted(StartResponse response) {
		upStream = response.followupActions.upStream;
		analysisUrl = response.followupActions.analysis;
		summaryUrl = response.followupActions.summary;
		
		recordingStarted = true;
		if (sessionListener != null) {
			sessionListener.onSessionStarted();
		}
//		recordingVoice = new RecordingVoiceRequest(upStream);
//		recordingVoice.open();
		
//		timer = new Timer();
//		timer.schedule(new GetAnalysisTimerTask(), analysisPollingInterval, analysisPollingInterval);

//		if (listener != null) {
//			listener.onSessionStarted();
//		}
	}

	@Override
	public void onStartRecordFailed() {
		recordingId = null;
		recordingStarted = false;
		if (sessionListener != null) {
			sessionListener.onSessionError(0, "Unable to start session");
		}
	}

	@Override
	public void onStopRecordFailed() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void respondFailed() {
		// TODO Auto-generated method stub
		
	}

	class GetAnalysisTimerTask extends TimerTask {

		@Override
		public void run() {
			if (!ongoingRequest) {
		 		GetRecordingAnalysisJsonRequest request = new GetRecordingAnalysisJsonRequest(analysisUrl, CommunicationSession.this);
		 		request.setParams();
		 		CommLayer.getInstance(context).SendRequestASync(request);
		 		ongoingRequest = true;
			}
		}
		
	}

	@Override
	public void onRecordStopped() {
		recordingStarted = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onAnalysisSampleReceived(GetRecordingAnalysisResponse response) {
		analysisUrl = response.followupActions.analysis;
		if (response.result.analysisSegments != null && response.result.analysisSegments.length != 0) {
			AnalysisSegment[] samples = response.result.analysisSegments;

			if (listener != null) {
				listener.onAnalysisReceived(samples);
			}
		}
		ongoingRequest = false;
	}

	@Override
	public void onRecordSummary(GetRecordingAnalysisResponse response) {
		if (response.result.analysisItems != null) {
			if (listener != null) {
				listener.onSummaryReceived(response.result.analysisItems.MoodGroupSummary);
			}
		}
	}

	void analyze(BlockingInputStream stream) {
		recordingVoice = new RecordingVoiceRequest(upStream, stream);
		recordingVoice.open();
		
		timer = new Timer();
		timer.schedule(new GetAnalysisTimerTask(), analysisPollingInterval, analysisPollingInterval);
	}

}
