package com.BeyondVerbal.comm;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;


public class CommLayer {	
	
	private static final String LOGTAG = "NGCommLayer";
	
	// Communications receivers
	private ConnectionReceiver connectionListener;

	public ConnectionReceiver getConnectionListener() {
		return connectionListener;
	}


	/*Singleton*/
	private static CommLayer singleInstanse = null;
	
	private  CommLayer()	 
	{
		
	};
	
	public static CommLayer getInstance(Context context) 
	{
		if(singleInstanse == null)
		{
			singleInstanse = new CommLayer();
			singleInstanse.init(context);
		}	
		return singleInstanse;
	}

	public Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}
	/*Singleton*/
	

	/** initiate main variables */
	public void init(Context context) {
		clear(context);
		registerReceivers(context);
	}
	
	/** Clear CommLayer */
	public void clear(Context context) 
	{
		unregisterReceivers(context);
	}

	/** register the communication receivers */
	private void registerReceivers(Context context)
	{
		Context appContext = context.getApplicationContext();
		// register the connection listener and send it action
		connectionListener = new ConnectionReceiver();
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		appContext.registerReceiver(connectionListener, filter);
	}
	
	/** unregister the communication receivers */
	private void unregisterReceivers(Context context)
	{
		Context appContext = context.getApplicationContext();
		if (connectionListener!=null)
		{
			appContext.unregisterReceiver(connectionListener);
			connectionListener = null;
		}
	}
	
	/** Is the Internet (wifi/gprs) connection on */ 
	public boolean hasInternet() 
	{
		if (connectionListener==null)
			return false;
		return connectionListener.IsConnected();
	}
	
	
	/** 
	 * There is no Internet connection, log out if needed. 
	 * The console will show the connection error dialog 
	 */
	public void onNetworkError()
	{
		Log.d(LOGTAG, "onNetworkError");
		if (connectionListener!=null && connectionListener.isNotifyDisconnected() == true){			
			connectionListener.setNotifyDisconnected(false);			
		}
	}

	public void SendRequestASync(RequestBase request) {
		request.SendASyncRequest();
	}
	
}