package org.pocketcampus.plugin.qaforum.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;




import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;

import org.pocketcampus.plugin.qaforum.shared.QATequilaToken;
import org.pocketcampus.plugin.qaforum.shared.QAforumService;
import org.pocketcampus.plugin.qaforum.shared.s_accept;
import org.pocketcampus.plugin.qaforum.shared.s_answer;
import org.pocketcampus.plugin.qaforum.shared.s_ask;
import org.pocketcampus.plugin.qaforum.shared.s_delete;
import org.pocketcampus.plugin.qaforum.shared.s_feedback;
import org.pocketcampus.plugin.qaforum.shared.s_latest;
import org.pocketcampus.plugin.qaforum.shared.s_relation;
import org.pocketcampus.plugin.qaforum.shared.s_report;
import org.pocketcampus.plugin.qaforum.shared.s_request;
import org.pocketcampus.plugin.qaforum.shared.s_session;
import org.pocketcampus.plugin.qaforum.shared.s_tag;

/**
 * QAforumServiceImpl
 * 
 * The implementation of the server side of the QAforum Plugin.
 * 
 * It fetches the user's QAforum data from the QAforum servers.
 * 
 * @author Susheng Shi <susheng.shi@epfl.ch>
 *
 */
public class QAforumServiceImpl implements QAforumService.Iface {
	private String urlString = "http://liapc3.epfl.ch";
	public QAforumServiceImpl() {
		System.out.println("Starting QAforum plugin server...");
	}
	
	@Override
	public String questionMatching(String question) throws TException {
		
		String respString = "";
		try {
			question =question.replaceAll("\\\\n", " ");
			question = question.replaceAll("[^A-Za-z0-9 ]", "");
			question = question.replaceAll(" ", "%20");
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+":1111/python?sentence="+question).openConnection();
			respString = IOUtils.toString(conn2.getInputStream(),"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to get response from Server");
		}
		return respString;
	}

	private void pushnotification(java.util.List<String> gasparList, String messageString, String notificationid) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("alert", messageString);
		hashMap.put("notificationid", notificationid);
		PocketCampusServer.pushNotifSend("qaforum", gasparList, hashMap);
	}

	private ArrayList<JSONObject> requestQAserver(JSONObject input){
		
		JSONObject mesJsonObject = null;
		ArrayList<JSONObject> messageJsonObjects = new ArrayList<JSONObject>();
	    try {
	        // instantiate the URL object with the target URL of the resource to request
	    	URL url = new URL(urlString+"/qaforum/index.php");
	        // instantiate the HttpURLConnection with the URL object - A new connection is opened every time by calling the openConnection method of the protocol handler for this URL.
	        // 1. This is the point where the connection is opened.
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        // set connection output to true
	        connection.setDoOutput(true);

	        // instead of a GET, we're going to send using method="POST"
	        connection.setRequestMethod("POST");

	        // instantiate OutputStreamWriter using the output stream, returned from getOutputStream, that writes to this connection.
	        // 2. This is the point where you'll know if the connection was successfully established. If an I/O error occurs while creating the output stream, you'll see an IOException.
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

	        // write data to the connection. This is data that you are sending to the server
	        // 3. No. Sending the data is conducted here. We established the connection with getOutputStream
	        writer.write(input.toString());
	        // Closes this output stream and releases any system resources associated with this stream. At this point, we've sent all the data. Only the outputStream is closed at this point, not the actual connection
	        writer.close();

	        // if there is a response code AND that response code is 200 OK, do stuff in the first if block
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                	if(!line.contains("{")||line.equals("no users"))
                		continue;
                    sb.append(line+"\n");
                    mesJsonObject=new JSONObject(line);
                    messageJsonObjects.add(mesJsonObject);
                }
                br.close();
	        } else {
	        	System.out.println("Failed to send the message to QAforum");
	        }
	    } catch (MalformedURLException e) {
	    } catch (IOException e) {
	    } catch (JSONException e) {
			e.printStackTrace();
		}
	    return messageJsonObjects;
	}
	
	public void appendToFailedDevicesList(List<String> resp) {
		JSONObject dataJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "failedDevice");
			dataJsonObject.put("devices", resp);
			System.out.println(dataJsonObject.toString());
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public QATequilaToken getTequilaTokenForQAforum() throws TException {
		System.out.println("getTequilaTokenForQAforum");
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+"/qaforum/token.php").openConnection();
			String respString = IOUtils.toString(conn2.getInputStream(),"UTF-8");
			QATequilaToken teqToken = new QATequilaToken(respString);
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}
	}

		
	@Override
	public s_session getSessionid(QATequilaToken token) throws TException {
		String respString = null;
		JSONObject dataJsonObject=new JSONObject();
		s_session dataS_session=null;
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+"/qaforum/login.php?token=" + token.getITequilaKey()).openConnection();
			respString = IOUtils.toString(conn2.getInputStream(),"UTF-8");
			System.out.println(respString);
			JSONObject messageJsonObject= new JSONObject(respString);
			if (messageJsonObject.getString("userid").equals("invalid")) {
				return null;
			}
			PocketCampusServer.pushNotifMap(token, "qaforum", messageJsonObject.getString("gaspar"));
			dataJsonObject.put("type", "online");
			dataJsonObject.put("userid", messageJsonObject.getString("userid"));
			dataJsonObject.put("online", 1);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			dataS_session=new s_session(messageJsonObject.getString("userid"), messageJsonObject.getInt("accept"), messageJsonObject.getInt("resttime"), messageJsonObject.getString("language"), messageJsonObject.getString("topic"), messageJsonObject.getInt("asktopic"), messageJsonObject.getInt("askexpirytime"),messageJsonObject.getInt("intro"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return dataS_session;
	} 
	
	private JSONObject dealwithmessage(ArrayList<JSONObject> messageArrayList) throws JSONException {
		JSONObject forwardObject=new JSONObject();
		if(messageArrayList.equals(null))
			return null;
		for(JSONObject messageObject : messageArrayList){
			String type=messageObject.getString("type");
			if (type.equals("notification")) {
				String gaspar=messageObject.getString("userid");
				int notificationid=messageObject.getInt("notificationid");
				String contentString=messageObject.getString("content");	
				pushnotification(Arrays.asList(gaspar), contentString, Integer.toString(notificationid));
			}
			else {
				forwardObject=messageObject;
			}
		}
		return forwardObject;
	}
	
	private boolean invalidSessionCheck(ArrayList<JSONObject> messageArrayList) throws JSONException {
		if(messageArrayList.equals(null))
			return true;
		for(JSONObject messageObject : messageArrayList){
			String type=messageObject.getString("type");
			if (type.equals("invalid")) {
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public String acceptNotif(s_accept accept) throws TException {
		JSONObject forwardObject=new JSONObject();
		JSONObject dataJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "accept");
			dataJsonObject.put("notificationid", accept.notificationid);
			dataJsonObject.put("reply", accept.accept);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			if(messageArrayList==null)
				return null;
			forwardObject=dealwithmessage(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (accept.accept==0) {
			return "declined";
		}
		else {			
			return forwardObject.toString();
		}
	}

	@Override
	public int askQuestion(s_ask ask) throws TException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    boolean validCheck=true;
		JSONObject dataJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "ask");
			dataJsonObject.put("language", "wait to implement");
			dataJsonObject.put("location", "wait to implement");
			dataJsonObject.put("userid", ask.sessionid);
			dataJsonObject.put("time", strDate);
			dataJsonObject.put("content",ask.content);
			dataJsonObject.put("topic",ask.topic);
			dataJsonObject.put("tags",ask.tags);
			dataJsonObject.put("expirytime",ask.expirytime);
			dataJsonObject.put("quesid",ask.quesid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {	
			e.printStackTrace();
		}
		if (validCheck) {
			return 1;
		}
		else {
			return 0;
		}
	}

	

	@Override
	public int answerQuestion(s_answer answer) throws TException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    boolean validCheck=true;
		JSONObject dataJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "answer");
			dataJsonObject.put("forwardid", answer.forwardid);
			dataJsonObject.put("userid", answer.sessionid);
			dataJsonObject.put("answer", answer.answer);
			dataJsonObject.put("time",strDate);
			dataJsonObject.put("typeid",answer.typeid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public int feedbackQuestion(s_feedback feedback) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "feedback");
			dataJsonObject.put("userid", feedback.sessionid);
			dataJsonObject.put("forwardid", feedback.forwardid);
			dataJsonObject.put("feedback", feedback.feedback);
			dataJsonObject.put("rate", feedback.rate);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public String requestQuestion(s_request request) throws TException {
		//unused in this version.
		JSONObject dataJsonObject=new JSONObject();
		JSONObject forwardObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "request");
			dataJsonObject.put("topics", request.topics);
			dataJsonObject.put("tags", request.tags);
			dataJsonObject.put("userid", request.sessionid);
			dataJsonObject.put("location", "wait to implement");			
			ArrayList<JSONObject>messageArrayList=requestQAserver(dataJsonObject);
			forwardObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			return forwardObject.toString();
		}
		else {
			return "0";
		}
	}

	@Override
	public int reportQuestion(s_report report) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "report");
			dataJsonObject.put("userid", report.sessionid);
			dataJsonObject.put("forwardid", report.forwardid);
			dataJsonObject.put("reportType", report.type);
			dataJsonObject.put("comment", report.comment);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public int updateSetting(s_session setting) throws TException {
		boolean validCheck=true;
		JSONObject dataJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "setting");
			dataJsonObject.put("userid", setting.sessionid);
			dataJsonObject.put("language", setting.language);
			dataJsonObject.put("accept", setting.accept);
			dataJsonObject.put("resttime", setting.resttime);
			dataJsonObject.put("topic", setting.topic);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public String requestInformation(String sessionid) throws TException {
		//unused in this version.
		JSONObject dataJsonObject=new JSONObject();
		JSONObject informationJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "information");
			dataJsonObject.put("userid", sessionid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			informationJsonObject=dealwithmessage(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return informationJsonObject.toString();
	}

	@Override
	public String myQuestions(String userid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		boolean validCheck=true;
		JSONObject informationJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "myquestions");
			dataJsonObject.put("userid", userid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			informationJsonObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return informationJsonObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String oneQuestion(int questionid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		JSONObject informationJsonObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "onequestion");
			dataJsonObject.put("questionid", questionid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			informationJsonObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return informationJsonObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String myAnswers(String userid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		boolean validCheck=true;
		JSONObject informationJsonObject=new JSONObject();
		try {
			dataJsonObject.put("type", "myanswers");
			dataJsonObject.put("userid", userid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			informationJsonObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return informationJsonObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String oneAnswer(int forwardid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		JSONObject informationJsonObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "oneanswer");
			dataJsonObject.put("forwardid", forwardid);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			informationJsonObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			return informationJsonObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String latestQuestions(String userid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		JSONObject forwardObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "latestquestion");
			dataJsonObject.put("userid", userid);			
			ArrayList<JSONObject>messageArrayList=requestQAserver(dataJsonObject);
			forwardObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return forwardObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String oneLatestQuestion(s_latest onelatest) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		JSONObject forwardObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "onelatest");
			dataJsonObject.put("userid", onelatest.userid);
			dataJsonObject.put("quesid", onelatest.quesid);
			ArrayList<JSONObject>messageArrayList=requestQAserver(dataJsonObject);
			forwardObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			return forwardObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public String pendingNotifications(String userid) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		JSONObject forwardObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "pendingnotification");
			dataJsonObject.put("userid", userid);			
			ArrayList<JSONObject>messageArrayList=requestQAserver(dataJsonObject);
			forwardObject=dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		if (validCheck) {
			return forwardObject.toString();
		}
		else {
			return "invalid";
		}
	}

	@Override
	public int deleteNotification(s_delete deleteinfo) throws TException {
		JSONObject dataJsonObject=new JSONObject();
		boolean validCheck=true;
		try {
			dataJsonObject.put("type", "delete");
			dataJsonObject.put("userid", deleteinfo.userid);
			dataJsonObject.put("forwardid", deleteinfo.forwardid);
			dataJsonObject.put("questiontype", deleteinfo.type);
			ArrayList<JSONObject> messageArrayList=requestQAserver(dataJsonObject);
			dealwithmessage(messageArrayList);
			validCheck=invalidSessionCheck(messageArrayList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (validCheck) {
			//new version changes(handling with logging out)
			System.out.println("Logging out request...");
			if(deleteinfo.type==-1)
				return -1;
			else {
				return 1;
			}
		}
		else {
			return 0;
		}
	}

	@Override
	public String relationship(s_relation relation) throws TException {
		
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+":1111/relationship?my="+relation.myuserid+"&other="+relation.otheruserid).openConnection();
			String relationship = IOUtils.toString(conn2.getInputStream(),"UTF-8");
			return relationship;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to get response from Cherry Server");
		}
	}

	@Override
	public int tagInterested(s_tag taguser) throws TException {
		//used in this version
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+":1111/tagInterested?userid="+taguser.userid+"&tag="+taguser.tag).openConnection();
			IOUtils.toString(conn2.getInputStream(),"UTF-8");
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to get response from Cherry Server");
		}
	}

	@Override
	public int closeIntro(String userid) throws TException {
		//unused in Android version
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(urlString+":1111/closeIntro?userid="+userid).openConnection();
			IOUtils.toString(conn2.getInputStream(),"UTF-8");
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to get response from Cherry Server");
		}
	}
}
