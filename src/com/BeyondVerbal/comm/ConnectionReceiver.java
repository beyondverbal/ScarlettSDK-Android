package com.BeyondVerbal.comm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class ConnectionReceiver extends BroadcastReceiver {

	private static final String LOGTAG = "NGConnectionReceiver";
	
	boolean GprsConnected = false;
	boolean WifiConnected = false;

	boolean isConnected = false;
	
	// The flag that notify the user if the network was disconnected
	private boolean notifyDisconnected = false;

	public ConnectionReceiver() {
		super();
	}

	private void setCurrentState(Context context) {
		boolean wasConnected = isConnected;
		
		//  sets the booleans in current state		 
		ConnectivityManager manager = null;
		manager = (ConnectivityManager) (context)
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		GprsConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		WifiConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		if (GprsConnected || WifiConnected) {
			isConnected = true;

		} else {
			isConnected = false;
		}
		
		checkDisconnected(context, wasConnected);
	}
	
	private void checkDisconnected(Context context, boolean wasConnected){
		// check if it was disconnected
		Log.d(LOGTAG, "oldState = " + wasConnected + ", newSatate = " + isConnected);
		
		// If there is Internet connection - no need to notify the user, we are fine.. 
		if (isConnected==true)
			notifyDisconnected = false;
		
		// If the connection was lost - need to notify the user
		if (wasConnected==true && isConnected==false && notifyDisconnected==false){
			notifyDisconnected = true;
		}
	}

	public boolean isNotifyDisconnected() {
		return notifyDisconnected;
	}

	public void setNotifyDisconnected(boolean notifyDisconnected) {
		this.notifyDisconnected = notifyDisconnected;
	}
	
	public boolean IsConnected() {
		return isConnected;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		setCurrentState(context);

	}

}
