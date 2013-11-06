package com.BeyondVerbal.comm.requests;

import java.io.InputStream;

import com.BeyondVerbal.comm.StreamingSocket;

public class RecordingVoiceRequest extends StreamingSocket {

	protected String streamUrl;

	public RecordingVoiceRequest(String streamUrl, InputStream inputStream) {
		super(inputStream);
		this.streamUrl = streamUrl;
	}

	/* (non-Javadoc)
	 * @see com.BeyondVerbal.comm.NGStreamingSocket#open()
	 */
	@Override
	public void open() {
		path = streamUrl;
		super.open();
	}

}
