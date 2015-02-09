package org.pocketcampus.plugin.edx.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.thrift.TException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pocketcampus.platform.shared.utils.Cookie;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.edx.shared.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * EdXServiceImpl
 * 
 * The implementation of the server side of the EdX Plugin.
 * 
 * It fetches the user's EdX data from the EdX servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXServiceImpl implements EdXService.Iface {

	final String LANDINGPAGE_URL = "https://edge.edx.org/";
	final String LOGIN_URL = "https://edge.edx.org/login_ajax";
	final String DASHBOARD_URL = "https://edge.edx.org/dashboard";
	final String COURSEWARE_URL = "https://edge.edx.org/courses/%s/courseware/";
	final String MODULE_URL = "https://edge.edx.org/courses/%s/courseware/%s/";
	final String CSRF_COOKIE = "csrftoken";
	final String CSRF_HEADER = "X-CSRFToken";

	static JsonParser jsonParser = new JsonParser();

	public EdXServiceImpl() {
		System.out.println("Starting EdX plugin server ...");
		new Thread(cleaner).start();
	}

	private Cookie getLoginCookie() {
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL(LANDINGPAGE_URL).openConnection();
			// conn2.setInstanceFollowRedirects(false);
			conn2.getInputStream();
			/*
			 * URL url = new URL(conn2.getHeaderField("Location"));
			 * MultiMap<String> params = new MultiMap<String>();
			 * UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
			 * TequilaToken teqToken = new TequilaToken(params.getString("requestkey"));
			 */
			Cookie cookie = new Cookie();
			for (String header : conn2.getHeaderFields().get("Set-Cookie")) {
				cookie.addFromHeader(header);
			}
			// System.out.println("cookie: " + cookie.cookie());
			return cookie;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private boolean doLogin(String email, String password, Cookie cookie) {
		try {
			HttpClient httpclient;
			HttpPost httppost;
			ArrayList<NameValuePair> postParameters;
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(LOGIN_URL);

			httppost.setHeader("Cookie", cookie.cookie());
			httppost.setHeader(CSRF_HEADER, cookie.cookie.get(CSRF_COOKIE));
			httppost.setHeader("Referer", LANDINGPAGE_URL);

			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("email", email));
			postParameters.add(new BasicNameValuePair("password", password));

			httppost.setEntity(new UrlEncodedFormEntity(postParameters));

			HttpResponse response = httpclient.execute(httppost);
			for (Header header : response.getHeaders("Set-Cookie")) {
				cookie.addFromHeader(header.getValue());
			}
			String rep = StringUtils.fromStream(response.getEntity().getContent(), "UTF-8");
			// System.out.println("Resp: " + rep);

			JsonObject jo = jsonParser.parse(rep).getAsJsonObject();
			return jo.get("success").getAsBoolean();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	private String getUserName(String cookie) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(DASHBOARD_URL);
			get.setHeader("Cookie", cookie);
			HttpContext context = new BasicHttpContext();

			HttpResponse response = httpclient.execute(get, context);
			if (getLastRedirectUrl(context).contains("login")) {
				return null;
			}
			String rep = StringUtils.fromStream(response.getEntity().getContent(), "UTF-8");

			Document doc = Jsoup.parse(rep, "UTF-8");
			for (Element e : doc.select("h1[class=user-name]")) {
				return e.html();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private String getLastRedirectUrl(HttpContext context) {
		HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
		return currentUrl;
	}

	private int getUserCourses(String cookie, List<EdxCourse> courses) {
		// System.out.println("cookie: " + cookie);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(DASHBOARD_URL);
			get.setHeader("Cookie", cookie);
			HttpContext context = new BasicHttpContext();

			HttpResponse response = httpclient.execute(get, context);
			if (getLastRedirectUrl(context).contains("login")) {
				return 407;
			}
			String rep = StringUtils.fromStream(response.getEntity().getContent(), "UTF-8");
			// System.out.println("Resp: " + rep);

			Document doc = Jsoup.parse(rep, "UTF-8");

			for (Element e : doc.select("li[class*=course-item]")) {
				Element e1 = e.getElementsByAttribute("data-course-id").get(0);
				// System.out.println("courseid = " + e1.attr("data-course-id"));
				// System.out.println("coursenumber = " + e1.attr("data-course-number"));
				Element e2 = e.getElementsByTag("h3").get(0).getElementsByAttribute("href").get(0);
				// System.out.println("coursetitle = " + e2.html());
				courses.add(new EdxCourse(e1.attr("data-course-id"), e2.html(), e1.attr("data-course-number")));
			}

			return 200;
		} catch (IOException e) {
			e.printStackTrace();
			return 500;
		}

	}

	private int getCourseSections(String courseid, String cookie, List<EdxSection> sections) {
		// System.out.println("cookie: " + cookie);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(String.format(COURSEWARE_URL, courseid));
			get.setHeader("Cookie", cookie);
			HttpContext context = new BasicHttpContext();

			HttpResponse response = httpclient.execute(get, context);
			if (getLastRedirectUrl(context).contains("login")) {
				return 407;
			}
			String rep = StringUtils.fromStream(response.getEntity().getContent(), "UTF-8");
			// System.out.println("Resp: " + rep);

			Document doc = Jsoup.parse(rep, "UTF-8");
			for (Element e : doc.select("div[class*=chapter]")) {
				Element e1 = e.getElementsByAttribute("aria-label").get(0);
				// System.out.println("sectiontitle = " + e1.attr("aria-label"));
				List<EdxModule> modules = new LinkedList<EdxModule>();
				for (Element li : e.getElementsByTag("li")) {
					Element li1 = li.getElementsByAttribute("href").get(0);
					String url = li1.attr("href");
					url = url.substring(url.indexOf("courseware") + 11, url.length() - 1);
					// System.out.println("moduleurl = " + url);
					Element li2 = li.getElementsByTag("p").get(0);
					// System.out.println("moduletitle = " + li2.html());
					modules.add(new EdxModule(url, li2.html()));
				}
				sections.add(new EdxSection(e1.attr("aria-label"), modules));
			}

			return 200;
		} catch (IOException e) {
			e.printStackTrace();
			return 500;
		}

	}

	private int getModuleDetails(String courseid, String moduleurl, String cookie, List<EdxSequence> sequencesArg) {
		// System.out.println("cookie: " + cookie);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(String.format(MODULE_URL, courseid, moduleurl));
			get.setHeader("Cookie", cookie);
			HttpContext context = new BasicHttpContext();

			HttpResponse response = httpclient.execute(get, context);
			if (getLastRedirectUrl(context).contains("login")) {
				return 407;
			}
			String rep = StringUtils.fromStream(response.getEntity().getContent(), "UTF-8");
			// System.out.println("Resp: " + rep);

			Document doc = Jsoup.parse(rep, "UTF-8");
			//

			List<EdxSequence> sequences = new LinkedList<EdxSequence>();

			for (Element e : doc.select("div[class*=seq_contents]")) {
				Document d = Jsoup.parse(StringEscapeUtils.unescapeHtml4(e.html()), "UTF-8");
				EdxSequence seq = new EdxSequence(); // invalid coz without verticalId
				EdxItemVideo edxVideo = null;
				for (Element e2 : d.getElementsByAttribute("data-block-type")) {
					if ("vertical".equals(e2.attr("data-block-type"))) {
						// System.out.println("vertid = " + e2.attr("data-usage-id"));
						edxVideo = null;
						seq = new EdxSequence(e2.attr("data-usage-id"), new LinkedList<EdxItemType>());
						seq.setVideoItems(new HashMap<Integer, EdxItemVideo>());
						seq.setHtmlItems(new HashMap<Integer, EdxItemHtml>());
						seq.setProblemItems(new HashMap<Integer, EdxItemProblem>());
						sequences.add(seq);
						continue;
					}
					// System.out.println("itemid = " + e2.attr("data-usage-id"));
					// System.out.println("itemtype = " + e2.attr("data-block-type"));
					String itemId = e2.attr("data-usage-id");
					if ("video".equals(e2.attr("data-block-type"))) {
						Element e3 = e2.getElementsByAttribute("data-streams").get(0);
						Element e4 = e2.getElementsByTag("h2").get(0);
						String youtubeId = e3.attr("data-streams").split("[:]")[1];
						String vidTitle = StringEscapeUtils.unescapeHtml4(e4.html());
						// System.out.println("videoid = " + youtubeId);
						edxVideo = new EdxItemVideo(itemId, youtubeId, vidTitle);
						seq.getVideoItems().put(seq.getItemsSize(), edxVideo);
						seq.getItems().add(EdxItemType.VIDEO);
					} else if ("html".equals(e2.attr("data-block-type"))) {
						String htmlContent = e2.html();
						if (edxVideo != null)
							edxVideo.setHtml(htmlContent);
						// System.out.println("html = " + htmlContent);
						// seq.getHtmlItems().put(seq.getItemsSize(), new EdxItemHtml(itemId, htmlContent));
						// seq.getItems().add(EdxItemType.HTML);
					} else if ("problem".equals(e2.attr("data-block-type"))) {
						//
						// seq.getProblemItems().put(seq.getItemsSize(), new EdxItemProblem(itemId));
						// seq.getItems().add(EdxItemType.PROBLEM);

					}
				}

			}
			for (Iterator<EdxSequence> iter = sequences.iterator(); iter.hasNext();) {
				EdxSequence element = iter.next();
				if (element.getItemsSize() == 0)
					iter.remove();
			}
			/*
			 * for(Element e : doc.select("nav[class*=sequence-nav]")) {
			 * Element e1 = e.getElementsByTag("ol").get(0);
			 * for(Element a : e1.getElementsByAttribute("data-id")) {
			 * System.out.println("seqid = " + a.attr("data-id"));
			 * System.out.println("seqindex = " + a.attr("data-element"));
			 * System.out.println("seqtype = " + a.attr("class").split(" ")[0]);
			 * }
			 * }
			 */
			sequencesArg.addAll(sequences);
			return 200;

		} catch (IOException e) {
			e.printStackTrace();
			return 500;
		}

	}

	@Override
	public EdxLoginResp doLogin(EdxLoginReq req) throws TException {
		System.out.println("doLogin");
		Cookie cookie = getLoginCookie();
		if (!doLogin(req.getEmail(), req.getPassword(), cookie)) {
			return new EdxLoginResp(407);
		}
		EdxLoginResp resp = new EdxLoginResp(200);
		resp.setSessionId(cookie.cookie());
		resp.setUserName(getUserName(cookie.cookie()));
		return resp;
	}

	@Override
	public EdxResp getUserCourses(EdxReq req) throws TException {
		System.out.println("getUserCourses");
		EdxResp resp = new EdxResp();
		resp.setUserCourses(new LinkedList<EdxCourse>());
		resp.setStatus(getUserCourses(req.getSessionId(), resp.getUserCourses()));
		return resp;
	}

	@Override
	public EdxResp getCourseSections(EdxReq req) throws TException {
		System.out.println("getCourseSections");
		if (!req.isSetCourseId())
			return new EdxResp(400);
		// getCourseSections("EPFL/CS305/Software_Engineering", req.getSessionId());
		EdxResp resp = new EdxResp();
		resp.setCourseSections(new LinkedList<EdxSection>());
		resp.setStatus(getCourseSections(req.getCourseId(), req.getSessionId(), resp.getCourseSections()));
		return resp;
	}

	@Override
	public EdxResp getModuleDetails(EdxReq req) throws TException {
		System.out.println("getModuleDetails");
		if (!req.isSetCourseId() || !req.isSetModuleUrl())
			return new EdxResp(400);
		// getModuleDetails("EPFL/CS305/Software_Engineering", "eaf1ca597c7444c58a1e23de729ffc95/2a676d099f124157b0b7b5f859a0ffda", req.getSessionId());
		EdxResp resp = new EdxResp();
		resp.setModuleDetails(new LinkedList<EdxSequence>());
		resp.setStatus(getModuleDetails(req.getCourseId(), req.getModuleUrl(), req.getSessionId(), resp.getModuleDetails()));
		return resp;
	}

	/****************
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	int BUFFER_SIZE = 4000; // = 0.5 second
	long MESSAGE_WINDOW = 60000; // 1 min // older parcels will be dropped
	long AUDIO_WINDOW = 10000; // 10 sec
	int PRIORITY_DELAY = 500; // delay over which a packet is considered hipri
	long MAX_WAIT_TIME = 2000; // timeout for long polling
	long INACTIVITY_TIMEOUT = 10000; // 10 sec

	// long URGENT_PRIORITY = 1L << 40;

	enum ParticipantTrackState {
		NODATA,
		NORMAL,
		SPEEDUP,
	}

	class AudioFrameState {
		int samples;
		boolean urgent;

		AudioFrameState(int s, boolean u) {
			samples = s;
			urgent = u;
		}
	}

	class AudioParcel {
		long receivedAt;
		long id;
		short[] audio;

		AudioParcel(long id, short[] audio) {
			this.receivedAt = System.currentTimeMillis();
			this.id = id;
			this.audio = audio;
		}
	}

	class MessageParcel {
		long receivedAt;
		long id;
		String from;
		String header;
		String body;

		MessageParcel(long id, String from, String header, String body) {
			this.receivedAt = System.currentTimeMillis();
			this.id = id;
			this.from = from;
			this.header = header;
			this.body = body;
		}
	}

	class ConcurrentAudioList extends ConcurrentLinkedQueue<AudioParcel> {
		private static final long serialVersionUID = 7880869819756446683L;
		long nextInsertedId = System.currentTimeMillis();

		long addAudio(short[] data) {
			synchronized (this) {
				AudioParcel ap = new AudioParcel(nextInsertedId, data);
				add(ap);
				nextInsertedId += ap.audio.length;
				return ap.id;
			}
		}

		void cleanUp() {
			synchronized (this) {
				AudioParcel ap;
				while ((ap = peek()) != null && ap.receivedAt < System.currentTimeMillis() - AUDIO_WINDOW) {
					poll();
				}
			}
		}

		AudioFrameState peekAudio(long seenId) {
			int samples = 0;
			boolean urgent = false;
			for (AudioParcel ap : this) {
				if (ap.id + ap.audio.length - 1 > seenId) {
					if (ap.id > seenId) {
						samples += ap.audio.length;
					} else {
						samples += ((int) (ap.id - seenId)) + ap.audio.length - 1;
					}
					if (ap.receivedAt < System.currentTimeMillis() - PRIORITY_DELAY)
						urgent = true;
				}
			}
			return new AudioFrameState(samples, urgent);
		}

		long getAudio(long seenId, short[] buffer, short factor, boolean speedup) {
			int index = 0;
			int count = 0;
			for (AudioParcel ap : this) {
				if (ap.id + ap.audio.length - 1 > seenId) {
					for (int i = 0; i < ap.audio.length; i++) {
						if (speedup && (++count % 3 == 0))
							continue;
						if (ap.id + i > seenId) {
							buffer[index++] += ap.audio[i] / factor;
							if (index >= buffer.length)
								return ap.id + i;
						}
					}
				}
			}
			throw new RuntimeException("not enough audio data!");
		}
	}

	class ConcurrentMessageList extends ConcurrentLinkedQueue<MessageParcel> {
		private static final long serialVersionUID = -8061512395401528651L;
		long nextInsertedId = System.currentTimeMillis();

		long addMessage(String from, String header, String body) {
			synchronized (this) {
				MessageParcel mp = new MessageParcel(nextInsertedId, from, header, body);
				add(mp);
				nextInsertedId += 1;
				return mp.id;
			}
		}

		void cleanUp() {
			synchronized (this) {
				MessageParcel mp;
				while ((mp = peek()) != null && mp.receivedAt < System.currentTimeMillis() - MESSAGE_WINDOW) {
					poll();
				}
			}
		}

		List<MsgPsgMessage> pollMessages(long seenId) {
			List<MsgPsgMessage> resp = new LinkedList<MsgPsgMessage>();
			for (MessageParcel mp : this) {
				if (mp.id > seenId) {
					MsgPsgMessage msg = new MsgPsgMessage(mp.id, mp.from, mp.header, MsgPsgMessageType.MESSAGE);
					msg.setTextBody(mp.body);
					resp.add(msg);
				}
			}
			if (resp.size() > 0)
				System.out.println("SEND MESSAGE messageListSize=" + size());
			return resp;

		}
	}

	class ConcurrentParticipantMap extends ConcurrentHashMap<String, Participant> {
		private static final long serialVersionUID = -4584846519464778294L;

		Participant checkParticipant(String partiRef, StudyRoom roomToNotify) {
			synchronized (this) {
				if (!containsKey(partiRef)) {
					System.out.println("              NEW PARTI " + partiRef);
					Participant parti = new Participant(partiRef, roomToNotify);
					put(partiRef, parti);
					return parti;
				} else {
					return get(partiRef);
				}
			}
		}
	}

	class ConcurrentRoomMap extends ConcurrentHashMap<String, StudyRoom> {
		private static final long serialVersionUID = 2706916050381008335L;

		StudyRoom checkRoom(String roomRef) {
			synchronized (this) {
				if (!containsKey(roomRef)) {
					System.out.println("              NEW ROOM " + roomRef);
					StudyRoom room = new StudyRoom();
					put(roomRef, room);
					return room;
				} else {
					return get(roomRef);
				}
			}
		}
	}

	class Participant {
		Map<String, Long> readProgress; // for AUDIO we keep track on server, for MESSAGE client keeps track
		// List<AudioParcel> audioParcels;
		ConcurrentAudioList audioParcels;
		String userName;
		long lastPoll;

		Participant(String partiRef, StudyRoom roomToNotify) {
			// readProgress = new ConcurrentHashMap<String, Long>();
			// audioParcels = Collections.synchronizedList(new LinkedList<AudioParcel>());
			readProgress = Collections.synchronizedMap(new HashMap<String, Long>()); // should not be accessed concurrently
			audioParcels = new ConcurrentAudioList();

			userName = getUserName(partiRef);
			roomToNotify.messages.addMessage("SYSTEM", "TEXT", "<p><i>" + userName + " joined</i></p>");
		}
		/*
		 * synchronized long addAudio(short [] data) {
		 * // figure out ID
		 * long id = System.currentTimeMillis();
		 * if(audioParcels.size() > 0) {
		 * AudioParcel ap = audioParcels.get(audioParcels.size() - 1);
		 * id = ap.id + ap.audio.length;
		 * }
		 * // trim old stuff
		 * while(audioParcels.size() > 0 && audioParcels.get(0).receivedAt < System.currentTimeMillis() - AUDIO_WINDOW)
		 * audioParcels.remove(0);
		 * // do add
		 * audioParcels.add(new AudioParcel(id, data));
		 * return id;
		 * }
		 */
	}

	class StudyRoom {
		// Map<String, Participant> participants;
		// List<MessageParcel> messages;
		ConcurrentParticipantMap participants;
		ConcurrentMessageList messages;

		StudyRoom() {
			// TODO Concurrent or not?
			// participants = new ConcurrentHashMap<String, Participant>();
			// participants = Collections.synchronizedMap(new HashMap<String, Participant>());
			// messages = new ConcurrentLinkedQueue<MessageParcel>();
			// messages = Collections.synchronizedList(new LinkedList<MessageParcel>());
			participants = new ConcurrentParticipantMap();
			messages = new ConcurrentMessageList();
		}
		/*
		 * synchronized long addMessage(String from, String header, String body) {
		 * // figure out ID
		 * long id = System.currentTimeMillis();
		 * if(messages.size() > 0)
		 * id = messages.get(messages.size() - 1).id + 1;
		 * // trim old stuff
		 * while(messages.size() > 0 && messages.get(0).receivedAt < System.currentTimeMillis() - MESSAGE_WINDOW)
		 * messages.remove(0);
		 * // do add
		 * messages.add(new MessageParcel(id, from, header, body));
		 * return id;
		 * }
		 * void checkParticipant (String partiRef) {
		 * // TODO replace this by putIfAbsent
		 * if(!participants.containsKey(partiRef))
		 * participants.put(partiRef, new Participant());
		 * }
		 */
	}

	class RoomManager {
		// Map<String, StudyRoom> rooms;
		ConcurrentRoomMap rooms;

		RoomManager() {
			// rooms = new ConcurrentHashMap<String, StudyRoom>();
			rooms = new ConcurrentRoomMap();
		}
		/*
		 * void checkRoom (String roomRef) {
		 * // TODO replace this by putIfAbsent
		 * if(!rooms.containsKey(roomRef))
		 * rooms.put(roomRef, new StudyRoom());
		 * }
		 */
	}

	RoomManager roomManager = new RoomManager();

	Runnable cleaner = new Runnable() {
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (Iterator<Entry<String, StudyRoom>> ri = roomManager.rooms.entrySet().iterator(); ri.hasNext();) {
					Entry<String, StudyRoom> re = ri.next();
					re.getValue().messages.cleanUp();
					for (Iterator<Entry<String, Participant>> pi = re.getValue().participants.entrySet().iterator(); pi.hasNext();) {
						Entry<String, Participant> pe = pi.next();
						pe.getValue().audioParcels.cleanUp();
						if (pe.getValue().lastPoll < System.currentTimeMillis() - INACTIVITY_TIMEOUT && pe.getValue().audioParcels.size() == 0) {
							System.out.println("               CLEANING UP    Participant " + pe.getValue().userName);
							re.getValue().messages.addMessage("SYSTEM", "TEXT", "<p><i>" + pe.getValue().userName + " left</i></p>");
							pi.remove();
						}
					}
					// if(re.getValue().participants.size() == 0 && re.getValue().messages.size() == 0) {
					if (!re.getValue().participants.entrySet().iterator().hasNext() && !re.getValue().messages.iterator().hasNext()) {
						System.out.println("               CLEANING UP    Room " + re.getKey());
						ri.remove();
					}
				}
			}
		}
	};

	@Override
	public MsgPsgSendBroadcastResp sendBroadcast(MsgPsgSendBroadcastReq req) throws TException {
		System.out.println("sendBroadcast sender=" + req.getSenderRef() + " type=" + req.getMessageType() + " header=" + req.getMessageHeader() + " body=" + req.getTextBody());

		if (req.getMessageType() == MsgPsgMessageType.AUDIO) {
			if (!req.isSetBinaryBody())
				return new MsgPsgSendBroadcastResp(400);
			StudyRoom room = roomManager.rooms.checkRoom(req.getRoomRef());
			// roomManager.checkRoom(req.getRoomRef());
			// StudyRoom room = roomManager.rooms.get(req.getRoomRef());
			Participant parti = room.participants.checkParticipant(req.getSenderRef(), room);
			// room.checkParticipant(req.getSenderRef());
			// Participant parti = room.participants.get(req.getSenderRef());
			byte[] bytes = req.getBinaryBody();
			short[] shorts = new short[bytes.length / 2];
			ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
			// long newRef = parti.addAudio(shorts);
			long newRef = parti.audioParcels.addAudio(shorts);
			MsgPsgSendBroadcastResp resp = new MsgPsgSendBroadcastResp(200);
			resp.setMessageRef(newRef);
			return resp;

		} else if (req.getMessageType() == MsgPsgMessageType.MESSAGE) {
			if (!req.isSetTextBody())
				return new MsgPsgSendBroadcastResp(400);
			StudyRoom room = roomManager.rooms.checkRoom(req.getRoomRef());
			// roomManager.checkRoom(req.getRoomRef());
			// StudyRoom room = roomManager.rooms.get(req.getRoomRef());
			long newRef = room.messages.addMessage(req.getSenderRef(), req.getMessageHeader(), req.getTextBody());
			// long newRef = room.addMessage(req.getSenderRef(), req.getMessageHeader(), req.getTextBody());
			MsgPsgSendBroadcastResp resp = new MsgPsgSendBroadcastResp(200);
			resp.setMessageRef(newRef);
			return resp;

		} else {
			return new MsgPsgSendBroadcastResp(400);
		}
	}

	/*
	 * boolean messageExists(String roomRef, long lastRead) {
	 * roomManager.checkRoom(roomRef);
	 * StudyRoom room = roomManager.rooms.get(roomRef);
	 * return (room.messages.size() > 0 && room.messages.get(room.messages.size() - 1).id > lastRead);
	 * }
	 */

	/*
	 * List<MsgPsgMessage> getMessages(String roomRef, long lastRead) {
	 * List<MsgPsgMessage> resp = new LinkedList<MsgPsgMessage>();
	 * System.out.println("MESSAGES=" + roomManager.rooms.get(roomRef).messages.size());
	 * for(MessageParcel m : roomManager.rooms.get(roomRef).messages){
	 * if(m.id > lastRead) {
	 * MsgPsgMessage msg = new MsgPsgMessage(m.id, m.from, m.header, MsgPsgMessageType.MESSAGE);
	 * msg.setTextBody(m.body);
	 * resp.add(msg);
	 * }
	 * }
	 * return resp;
	 * }
	 */

	/*
	 * Map<String, ParticipantTrackState> audioExists(String roomRef, String receiverRef) {
	 * roomManager.checkRoom(roomRef);
	 * StudyRoom room = roomManager.rooms.get(roomRef);
	 * 
	 * room.checkParticipant(receiverRef);
	 * Map<String, Long> readProg = room.participants.get(receiverRef).readProgress;
	 * 
	 * boolean oneIsEmpty = false;
	 * boolean priorityExists = false;
	 * boolean atLeastOne = false;
	 * 
	 * Map<String, ParticipantTrackState> tracks = new HashMap<String, ParticipantTrackState>();
	 * 
	 * for(Entry<String, Participant> e : room.participants.entrySet()) {
	 * if(receiverRef.equals(e.getKey()))
	 * continue;
	 * atLeastOne = true;
	 * long frames;
	 * boolean thisIsPrio = false;
	 * if(e.getValue().audioParcels.size() == 0) {
	 * frames = 0;
	 * } else if(!readProg.containsKey(e.getKey())) {
	 * AudioParcel first = e.getValue().audioParcels.get(0);
	 * AudioParcel last = e.getValue().audioParcels.get(e.getValue().audioParcels.size() - 1);
	 * frames = last.id - first.id + last.audio.length;
	 * if(first.receivedAt < System.currentTimeMillis() - PRIORITY_DELAY)
	 * thisIsPrio = true;
	 * } else {
	 * AudioParcel last = e.getValue().audioParcels.get(e.getValue().audioParcels.size() - 1);
	 * frames = last.id + last.audio.length - readProg.get(e.getKey()) - 1;
	 * int index = e.getValue().audioParcels.size() - 1;
	 * while(readProg.get(e.getKey()) < e.getValue().audioParcels.get(index).id - 1) {
	 * index--;
	 * if(index == 0)
	 * break;
	 * }
	 * if(e.getValue().audioParcels.get(index).receivedAt < System.currentTimeMillis() - PRIORITY_DELAY)
	 * thisIsPrio = true;
	 * }
	 * if(frames < BUFFER_SIZE) {
	 * oneIsEmpty = true;
	 * tracks.put(e.getKey(), ParticipantTrackState.NODATA);
	 * } else if(frames < 2 * BUFFER_SIZE) {
	 * tracks.put(e.getKey(), ParticipantTrackState.NORMAL);
	 * if(thisIsPrio) priorityExists = true;
	 * } else {
	 * tracks.put(e.getKey(), ParticipantTrackState.SPEEDUP);
	 * if(thisIsPrio) priorityExists = true;
	 * }
	 * }
	 * if(!atLeastOne) return null;
	 * if(priorityExists || !oneIsEmpty) return tracks;
	 * return null;
	 * }
	 */

	MsgPsgMessage getAudio2(String roomRef, String receiverRef) {

		StudyRoom room = roomManager.rooms.checkRoom(roomRef);
		Participant parti = room.participants.checkParticipant(receiverRef, room);

		boolean oneIsEmpty = false;
		boolean priorityExists = false;
		boolean atLeastOne = false;

		Map<String, ParticipantTrackState> tracks = new HashMap<String, ParticipantTrackState>();

		for (Entry<String, Participant> e : room.participants.entrySet()) {
			if (receiverRef.equals(e.getKey()))
				continue;
			atLeastOne = true;
			Long seen = parti.readProgress.get(e.getKey());

			AudioFrameState result = e.getValue().audioParcels.peekAudio(seen == null ? 0 : seen);

			if (result.urgent && result.samples >= BUFFER_SIZE)
				priorityExists = true;
			if (result.samples < BUFFER_SIZE) {
				tracks.put(e.getKey(), ParticipantTrackState.NODATA);
				oneIsEmpty = true;
			} else if (result.samples < 3 * BUFFER_SIZE) {
				tracks.put(e.getKey(), ParticipantTrackState.NORMAL);
			} else {
				tracks.put(e.getKey(), ParticipantTrackState.SPEEDUP);
			}
		}

		if (!atLeastOne || !priorityExists && oneIsEmpty)
			return null;

		System.out.println("SEND AUDIO " + tracks);

		short[] buffer = new short[BUFFER_SIZE];
		short occupancy = (short) tracks.size();

		for (Entry<String, Participant> e : room.participants.entrySet()) {
			if (!tracks.containsKey(e.getKey()))
				continue;
			if (tracks.get(e.getKey()) == ParticipantTrackState.NODATA)
				continue;
			boolean speedup = (tracks.get(e.getKey()) == ParticipantTrackState.SPEEDUP);
			Long seen = parti.readProgress.get(e.getKey());

			long newSeen = e.getValue().audioParcels.getAudio(seen == null ? 0 : seen, buffer, occupancy, speedup);

			parti.readProgress.put(e.getKey(), newSeen);

		}

		MsgPsgMessage msg = new MsgPsgMessage(0, "", "", MsgPsgMessageType.AUDIO);
		byte[] bytes = new byte[buffer.length * 2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buffer);
		msg.setBinaryBody(bytes);
		return msg;

	}

	/*
	 * MsgPsgMessage getAudio(String roomRef, String receiverRef, Map<String, ParticipantTrackState> tracks) {
	 * System.out.println(tracks);
	 * Map<String, Participant> parti = roomManager.rooms.get(roomRef).participants;
	 * Map<String, Long> readProg = parti.get(receiverRef).readProgress;
	 * short [] shorts = new short [BUFFER_SIZE];
	 * for(Entry<String, Participant> e : parti.entrySet()) {
	 * if(!tracks.containsKey(e.getKey()))
	 * continue;
	 * if(tracks.get(e.getKey()) == ParticipantTrackState.NODATA)
	 * continue;
	 * boolean speedup = (tracks.get(e.getKey()) == ParticipantTrackState.SPEEDUP);
	 * // TODO here we assumed that data did not disappear, but it might
	 * int parcelIndex = 0;
	 * int frameIndex = 0;
	 * if(readProg.containsKey(e.getKey())) {
	 * long prog = readProg.get(e.getKey());
	 * while(prog >= e.getValue().audioParcels.get(parcelIndex).id + e.getValue().audioParcels.get(parcelIndex).audio.length - 1)
	 * parcelIndex++;
	 * frameIndex = (int) (prog - e.getValue().audioParcels.get(parcelIndex).id + 1);
	 * }
	 * for(int i = 0; i < BUFFER_SIZE; i++) {
	 * shorts[i] += e.getValue().audioParcels.get(parcelIndex).audio[frameIndex];
	 * readProg.put(e.getKey(), e.getValue().audioParcels.get(parcelIndex).id + frameIndex);
	 * frameIndex++;
	 * if(speedup)
	 * frameIndex++;
	 * if(frameIndex >= e.getValue().audioParcels.get(parcelIndex).audio.length) {
	 * frameIndex = frameIndex - e.getValue().audioParcels.get(parcelIndex).audio.length;
	 * parcelIndex++;
	 * }
	 * 
	 * }
	 * 
	 * }
	 * MsgPsgMessage msg = new MsgPsgMessage(0, "", "", MsgPsgMessageType.AUDIO);
	 * byte[] bytes = new byte [shorts.length * 2];
	 * ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);
	 * msg.setBinaryBody(bytes);
	 * return msg;
	 * }
	 */

	@Override
	public MsgPsgReceiveBroadcastResp receiveBroadcast(MsgPsgReceiveBroadcastReq req) throws TException {
		System.out.println("receiveBroadcast receiver=" + req.getReceiverRef() + " lastMsg=" + req.getLastMessageRef());

		long reqArrival = System.currentTimeMillis();

		StudyRoom room = roomManager.rooms.checkRoom(req.getRoomRef());
		Participant parti = room.participants.checkParticipant(req.getReceiverRef(), room);
		parti.lastPoll = reqArrival;

		// boolean r1;
		// Map<String, ParticipantTrackState> r2;
		List<MsgPsgMessage> r1;
		MsgPsgMessage r2;
		while (true) {
			r1 = roomManager.rooms.checkRoom(req.getRoomRef()).messages.pollMessages(req.getLastMessageRef());
			r2 = getAudio2(req.getRoomRef(), req.getReceiverRef());
			// r1 = messageExists(req.getRoomRef(), req.getLastMessageRef());
			// r2 = audioExists(req.getRoomRef(), req.getReceiverRef());
			if (r1.size() > 0 || r2 != null || System.currentTimeMillis() - reqArrival > MAX_WAIT_TIME)
				break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * List<MsgPsgMessage> resp = new LinkedList<MsgPsgMessage>();
		 * if(r1)
		 * resp.addAll(getMessages(req.getRoomRef(), req.getLastMessageRef()));
		 * if(r2 != null)
		 * resp.add(getAudio(req.getRoomRef(), req.getReceiverRef(), r2));
		 */

		if (r2 != null)
			r1.add(r2);

		MsgPsgReceiveBroadcastResp reply = new MsgPsgReceiveBroadcastResp(200);
		reply.setMessages(r1);
		return reply;

		/***
		 * struct MsgPsgReceiveBroadcastReq {
		 * 1: required string receiverRef;
		 * 2: required string roomRef;
		 * 3: required i64 lastMessageRef;
		 * 4: required i32 pendingAudio;
		 * }
		 * 
		 * struct MsgPsgMessage {
		 * 1: required i64 messageRef;
		 * 2: required string senderRef;
		 * 3: required string messageHeader;
		 * 4: required MsgPsgMessageType messageType;
		 * 5: optional string textBody;
		 * 6: optional binary binaryBody;
		 * }
		 * 
		 * struct MsgPsgReceiveBroadcastResp {
		 * 1: required i32 status;
		 * 2: optional list<MsgPsgMessage> messages;
		 * }
		 **/
	}

	@Override
	public MsgPsgGetActiveRoomsResp getActiveRooms(MsgPsgGetActiveRoomsReq req) throws TException {
		System.out.println("getActiveRooms");
		// TODO Auto-generated method stub
		return null;
	}

}
