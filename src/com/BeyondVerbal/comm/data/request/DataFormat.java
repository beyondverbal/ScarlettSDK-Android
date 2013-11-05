package com.BeyondVerbal.comm.data.request;

public class DataFormat {

	public enum AudioDataFormat {
		PCM,
		WAV
	}
	
	public String type;
	public int channels;
	public int sample_rate;
	public int bits_per_sample;
}
