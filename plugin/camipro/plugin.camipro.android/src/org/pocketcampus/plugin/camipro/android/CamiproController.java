package org.pocketcampus.plugin.camipro.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.req.BalanceAndTransactionsRequest;
import org.pocketcampus.plugin.camipro.android.req.StatsAndLoadingInfoRequest;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Client;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

import android.util.Log;

public class CamiproController extends PluginController implements ICamiproController{

	private String mPluginName = "camipro";
	

	@Override
	public void onCreate() {
		Log.v("DEBUG", "onCreate called on CamiproController");
		// Initializing the model is part of the controller's job...
		mModel = CamiproModel.getInstance();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		//TODO for now, need two clients to be able to issue two concurrent server requests
		mClientBT = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void setCamiproCookie(String sessId) {
		mModel.setCamiproCookie(sessId);
	}
	
	public String getCamiproCookie() {
		return mModel.getCamiproCookie();
	}
	
	public void refreshBalanceAndTransactions() {
		if(mModel.getCamiproCookie() == null)
			return;
		new BalanceAndTransactionsRequest().start(this, mClientBT, buildSessionId());
	}
	
	public void refreshStatsAndLoadingInfo() {
		if(mModel.getCamiproCookie() == null)
			return;
		new StatsAndLoadingInfoRequest().start(this, mClientSL, buildSessionId());
	}
	
	public void reset() {
		mModel = CamiproModel.killInstance();
	}
	
	private SessionId buildSessionId() {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_CAMIPRO);
		sessId.setCamiproCookie(mModel.getCamiproCookie());
		return sessId;
	}
	
	private CamiproModel mModel;
	private Iface mClientBT;
	private Iface mClientSL;
	
}
