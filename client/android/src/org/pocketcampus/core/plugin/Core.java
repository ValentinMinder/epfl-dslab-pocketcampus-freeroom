package org.pocketcampus.core.plugin;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.pocketcampus.core.provider.IProvider;
import org.pocketcampus.plugin.mainscreen.IAllowsID;

import android.content.Context;
import android.content.Intent;

/**
 * Main Core class, creates the list of all plugins available and handles the launch of the new activities.
 * This class is a singleton.
 * 
 * @status incomplete but working
 * @author florian
 * @license 
 *
 */

public class Core {
	private static Core instance_ = null;
	private static ReleaseMode applicationMode_;
	private Vector<PluginBase> availablePlugins_;

	public enum ReleaseMode {
		DEVELOPMENT, RELEASE
	}

	public static Core getInstance() {
		if(instance_ == null) {
			return new Core();
		} else {
			return instance_;
		}
	}

	private Core() {
		applicationMode_ = ReleaseMode.DEVELOPMENT;
		availablePlugins_ = PluginDiscoverer.discoverPlugins(applicationMode_);

		authorizeSelfSignedCertificates();
	}

	private void authorizeSelfSignedCertificates() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}});
				
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayPlugin(Context ctx, PluginBase plugin) {
		Intent intent = new Intent(ctx, plugin.getClass());
		startActivity(ctx, intent);
	}

	public static void startPluginWithID(Context ctx, String plugin, int id) {
		Intent intent = new Intent();
		intent.setClassName(ctx, plugin);
		intent.putExtra("id", id);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public static void startPluginWithID(Context ctx, PluginBase plugin, int id) {
		Intent intent = new Intent(ctx, plugin.getClass());
		if(plugin instanceof IAllowsID) {
			intent.putExtra("id", id);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public void configurePlugin(Context ctx, PluginBase plugin) {
		Intent intent = new Intent(ctx, plugin.getPluginPreference().getClass());
		startActivity(ctx, intent);
	}

	private void startActivity(Context ctx, Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public Vector<PluginBase> getAvailablePlugins() {
		return availablePlugins_;
	}

	/**
	 * Returns a list of instances of all plugins implementing a given interface.
	 * @param providerInterface
	 * @return
	 */
	public ArrayList<PluginBase> getProvidersOf(Class<? extends IProvider> providerInterface) {

		// we want an interface
		if(!providerInterface.isInterface()) {
			return null;
		}

		ArrayList<PluginBase> providers = new ArrayList<PluginBase>();

		// find all appropriate plugins
		for(PluginBase plugin : availablePlugins_) {
			if(providerInterface.isInstance(plugin)) {
				providers.add(plugin);
			}
		}

		return providers;
	}

	public String getServerUrl() {
		// Change this to your local IP.
		return "http://128.178.252.49:8080/";
	}

	public static ReleaseMode getApplicationMode() {
		return applicationMode_;
	}
}
