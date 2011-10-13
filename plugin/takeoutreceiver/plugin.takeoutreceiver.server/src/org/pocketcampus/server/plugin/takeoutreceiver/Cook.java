package org.pocketcampus.server.plugin.takeoutreceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class Cook {

	private static Cook cook;

	public static void createCook(String deviceToken, String name) {
		cook = new Cook(deviceToken, name);
	}

	public static boolean dispatchToCook(long order) {
		if (cook == null) {
			return false;
		}
		try {
			cook.sendReceivedOrder(order);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private String deviceToken;
	private String name;

	public Cook(String deviceToken, String name) {
		super();
		this.deviceToken = deviceToken;
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void sendReceivedOrder(long orderId) throws Exception {
		System.out.println("Order ID = "+orderId);
		String message = "" + orderId;
		String msg = "data.payload=" + message;
		String colapseData = "collapse_key=0";
		String headerKey = "Authorization";
		String headerValue = "GoogleLogin auth=DQAAAPYAAAAc89AnFtZXfImBToUwSRdlQE_IQBoy0pBpJ9r9y9k2WhTOxn3n9mAB1EaLgFvbgDFiRqgM6aG2fgHdb6yu7RE9V20ZtmlS14Y4BSKGxktZnK8QNDu7nLaSz-CKPgH0iuOX5ouhHH1aqW_RJLdE2_vMtR28K54j4VaSQzUytopBjdto3vJdi9b7C1dpqGm2TX5FpkZckb9rODZHzjG0IHhmecyHFpt7AsjeifAI3uKBWxrHu9aUPFVVAeUaJYPmXlv9HiGUfsGRW72zZB0POaIuA9okRJoJssMWmZLRtWILLBx_6g4C0sQS4CvXCFDepmv4893NOa32_r8psy3Eqxtb";
		System.out.println("=============" + deviceToken + "=============");
		String reg = "registration_id=" + deviceToken;
		String params = msg + "&" + reg + "&" + colapseData;
		HttpsURLConnection url = (HttpsURLConnection) new URL("https://android.apis.google.com/c2dm/send").openConnection();
		url.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		url.setRequestMethod("POST");
		url.setRequestProperty(headerKey, headerValue);
		url.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(url.getOutputStream());
		out.write(params);
		out.flush();
		out.close();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				url.getInputStream()));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cook other = (Cook) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
