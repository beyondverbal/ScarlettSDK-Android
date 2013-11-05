package com.BeyondVerbal.comm.data.response;

public class Analysis {
	public DualValue CompositMood;
	public DualValue MoodGroup;
	public DualValue MoodGroupSummary;
	public IntValue ComposureMeter;
	public StringValue TemperMeter;
	public IntValue TemperValue;
	public IntValue CooperationLevel;
	public IntValue ServiceScore;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Analysis [CompositMood=" + CompositMood + ", MoodGroup="
				+ MoodGroup + ", MoodGroupSummary=" + MoodGroupSummary
				+ ", ComposureMeter=" + ComposureMeter + ", TemperMeter="
				+ TemperMeter + ", TemperValue=" + TemperValue
				+ ", CooperationLevel=" + CooperationLevel + ", ServiceScore="
				+ ServiceScore + "]";
	}	
}
