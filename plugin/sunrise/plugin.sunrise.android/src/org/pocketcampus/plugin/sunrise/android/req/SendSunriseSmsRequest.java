package org.pocketcampus.plugin.sunrise.android.req;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.sunrise.android.SunriseController;

import org.pocketcampus.plugin.sunrise.android.SunriseController.SunriseSmsSend;
import org.pocketcampus.plugin.sunrise.android.SunriseController.SunriseSmsSendResult;
import org.pocketcampus.plugin.sunrise.android.SunriseModel;

import android.text.Html;

/**
 * SendSunriseSmsRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SendSunriseSmsRequest extends Request<SunriseController, DefaultHttpClient, SunriseSmsSend, SunriseSmsSendResult> {
	
	@Override
	protected SunriseSmsSendResult runInBackground(DefaultHttpClient client, SunriseSmsSend param) throws Exception {
		client.setRedirectHandler(SunriseController.redirectNoFollow);
		SunriseSmsSendResult result = new SunriseSmsSendResult();
		
		
		HttpPost loginPost = new HttpPost("https://www1.sunrise.ch/is-bin/INTERSHOP.enfinity/WFS/Sunrise-Residential-Site/fr_CH/-/CHF/ViewApplication-Login");
		List<NameValuePair> cred = new LinkedList<NameValuePair>();
		cred.add(new BasicNameValuePair("LoginForm_Login", param.credentials.username));
		cred.add(new BasicNameValuePair("LoginForm_Password", param.credentials.password));
		loginPost.setEntity(new UrlEncodedFormEntity(cred));
		HttpResponse loginResp = client.execute(loginPost);
		if(loginResp.getStatusLine().getStatusCode() != 302) {
			result.status = 407; // wrong credentials
			return result;
		}
		
		
		HttpGet initGet1 = new HttpGet("https://www1.sunrise.ch/is-bin/INTERSHOP.enfinity/WFS/Sunrise-Residential-Site/fr_CH/-/CHF/ViewStandardCatalog-Browse?CatalogCategoryID=cK7AqFI.H90AAAEvTK41fuRr");
		client.execute(initGet1);
		
		
		HttpGet initGet2 = new HttpGet("http://mip.sunrise.ch/mip/dyn/login/smsMeinKonto?lang=fr");
		HttpResponse initResp2 = client.execute(initGet2);
		if(initResp2.getStatusLine().getStatusCode() != 200) {
			result.status = 500; // server error
			return result;
		}
		String initReply2 = convertInputStreamToString(initResp2.getEntity().getContent());
		//System.out.println("before remainingFree " + getSubstringBetween(initReply2, "Gratuits ", " "));
		//System.out.println("before sentPaid " + getSubstringBetween(initReply2, "Pay&#233;(s) ", " "));
		String currentMsisdn = getSubstringBetween(initReply2, "name=\"currentMsisdn\" value=\"", "\"");
		if(param.loginOnly) {
			result.credentials = param.credentials;
			result.remainingFreeSms = Integer.parseInt(getSubstringBetween(initReply2, "Gratuits ", " "));
			result.status = 220;
			return result;
		}
		
		
		HttpPost sendPost = new HttpPost("http://mip.sunrise.ch/mip/dyn/login/smsMeinKonto?lang=fr");
		List<NameValuePair> smsPost = new LinkedList<NameValuePair>();
		smsPost.add(new BasicNameValuePair("recipient", param.recipient));
		smsPost.add(new BasicNameValuePair("type", "sms"));
		smsPost.add(new BasicNameValuePair("message", param.message));
		smsPost.add(new BasicNameValuePair("task", "send"));
		smsPost.add(new BasicNameValuePair("currentMsisdn", currentMsisdn));
		sendPost.setEntity(new UrlEncodedFormEntity(smsPost));
		HttpResponse sendResp = client.execute(sendPost);
		if(sendResp.getStatusLine().getStatusCode() != 200) {
			result.status = 500; // server error
			return result;
		}
		String sendReply = convertInputStreamToString(sendResp.getEntity().getContent());
		String smsSentTo = getSubstringBetween(sendReply, "Un SMS a &#233;t&#233; envoy&#233; &#224; ", ".");
		String remainingFree = getSubstringBetween(sendReply, "Gratuits ", " ");
		String sentPaid = getSubstringBetween(sendReply, "Pay&#233;(s) ", " ");
		System.out.println("message " + Html.fromHtml(getSubstringBetween(sendReply, "<span id=\"errorBlock\" class=\"error\">", "<")).toString());
		System.out.println("after remainingFree " + remainingFree);
		System.out.println("after sentPaid " + sentPaid);
		if(!smsSentTo.equals(param.recipient)) {
			result.status = 500; // server error
			return result;
		}
		result.remainingFreeSms = Integer.parseInt(remainingFree);
		result.status = 200;
		return result;
	}

	@Override
	protected void onResult(SunriseController controller, SunriseSmsSendResult result) {
		SunriseModel am = ((SunriseModel) controller.getModel());
		if(result.status == 200) {
			am.getListenersToNotify().smsSent();
			am.setRemainingFreeSms(result.remainingFreeSms);
		} else if(result.status == 220) {
			am.setSunriseCredentials(result.credentials);
			am.setRemainingFreeSms(result.remainingFreeSms);
		} else if(result.status == 407) {
			am.getListenersToNotify().badCredentials();
		} else { // 500
			am.getListenersToNotify().serverErrorOccurred();
		}
	}
	
	@Override
	protected void onError(SunriseController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

	/**
	 * HELPER FUNCTIONS
	 */
	
	private String convertInputStreamToString(InputStream is) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);// + "\n"
		}
		return total.toString();
	}
	
	private String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}
	
}
