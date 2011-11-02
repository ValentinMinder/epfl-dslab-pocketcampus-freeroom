package org.pocketcampus.plugin.camipro.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.req.BalanceRequest;
import org.pocketcampus.plugin.camipro.android.req.EbankingRequest;
import org.pocketcampus.plugin.camipro.android.req.TransactionsRequest;
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
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void refreshBalance() {
		new BalanceRequest().start(this, mClient, (Object)null);
	}
	
	public void refreshEbanking() {
		new EbankingRequest().start(this, mClient, (Object)null);
	}
	
	public void refreshTransactions() {
		new TransactionsRequest().start(this, mClient, (Object)null);
	}
	
	private CamiproModel mModel;
	private Iface mClient;
	
}
