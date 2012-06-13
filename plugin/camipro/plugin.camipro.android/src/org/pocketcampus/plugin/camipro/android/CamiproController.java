package org.pocketcampus.plugin.camipro.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.req.BalanceAndTransactionsRequest;
import org.pocketcampus.plugin.camipro.android.req.GetCamiproSessionRequest;
import org.pocketcampus.plugin.camipro.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.camipro.android.req.SendLoadingInfoByEmailRequest;
import org.pocketcampus.plugin.camipro.android.req.StatsAndLoadingInfoRequest;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproRequest;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Client;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * CamiproController - Main logic for the Camipro Plugin.
 * 
 * This class issues requests to the Camipro PocketCampus
 * server to get the Camipro data of the logged in user.
 * It also allows the users to get the e-banking information
 * via email.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproController extends PluginController implements ICamiproController{

	private String mPluginName = "camipro";
	
	private CamiproModel mModel;
	private Iface mClient;
	private Iface mClientBT;
	private Iface mClientSL;
	private Iface mClientLE;
	
	@Override
	public void onCreate() {
		mModel = new CamiproModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientBT = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientLE = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE".equals(aIntent.getAction())) {
			Uri intentUri = aIntent.getData();
			if(intentUri != null && "pocketcampus-authenticated".equals(intentUri.getScheme())) {
				Bundle extras = aIntent.getExtras();
				if(extras != null && extras.getString("tequilatoken") != null) {
					mModel.getListenersToNotify().tokenAuthenticationFinished();
				} else {
					// TODO figure out what to do
				}
			}
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getTequilaToken() {
		new GetTequilaTokenRequest().start(this, mClient, null);
	}
	
	public void getCamiproSession() {
		new GetCamiproSessionRequest().start(this, mClient, mModel.getTequilaToken());
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
	
	public void sendEmailWithLoadingDetails() {
		if(mModel.getCamiproCookie() == null)
			return;
		new SendLoadingInfoByEmailRequest().start(this, mClientLE, buildSessionId());
	}
	
	private CamiproRequest buildSessionId() {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_CAMIPRO);
		sessId.setCamiproCookie(mModel.getCamiproCookie());
		CamiproRequest cr = new CamiproRequest();
		cr.setILanguage(Locale.getDefault().getLanguage());
		cr.setISessionId(sessId);
		return cr;
	}
	
}
