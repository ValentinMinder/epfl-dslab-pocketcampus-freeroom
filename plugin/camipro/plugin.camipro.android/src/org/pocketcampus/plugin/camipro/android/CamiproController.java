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

public class CamiproController extends PluginController implements ICamiproController{

	private String mPluginName = "camipro";
	

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new CamiproModel();
		
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
		new BalanceAndTransactionsRequest().start(this, mClientBT, buildSessionId());
	}
	
	public void refreshStatsAndLoadingInfo() {
		new StatsAndLoadingInfoRequest().start(this, mClientSL, buildSessionId());
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
