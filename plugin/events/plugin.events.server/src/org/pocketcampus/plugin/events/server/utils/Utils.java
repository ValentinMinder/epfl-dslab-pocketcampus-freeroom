package org.pocketcampus.plugin.events.server.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.pushnotif.server.PushNotifServiceImpl;

public class Utils {


	public static <T> String join(Collection<T> coll, String separator) {
		if (coll == null)
			return null;
		StringBuilder sb = new StringBuilder();
		boolean looped = false;
		for (T t : coll) {
			if (looped)
				sb.append(separator);
			sb.append(t.toString());
			looped = true;
		}
		return sb.toString();
	}

	public static List<String> split(String blob, String regex) {
		if (blob == null)
			return null;
		return Arrays.asList(blob.split(regex));
	}

	public static String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if (b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if (a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}

	

	public static String getResizedPhotoUrl(String image, int newSize) {
		if (image == null)
			return null;
		if (image.contains("memento.epfl.ch/image")) {
			image = Utils.getSubstringBetween(image, "image/", "/"); // get the image id
			image = "http://memento.epfl.ch/image/" + image + "/" + newSize + "x" + newSize + ".jpg";
		} else if (image.contains("secure.gravatar.com")) {
			image = Utils.getSubstringBetween(image, "avatar/", "?"); // get the image id
			image = "http://secure.gravatar.com/avatar/" + image + "?s=" + newSize;
		}
		return image;
	}


	public static void fixCategAndTags(EventItem e) {
		if (!e.isSetEventCateg())
			e.setEventCateg(1000000); // uncategorized
		if (!e.isSetEventTags() || e.getEventTags().size() == 0)
			e.setEventTags(oneItemList("unlabeled")); // unlabeled
	}

	public static <T> List<T> oneItemList(T obj) {
		List<T> list = new LinkedList<T>();
		list.add(obj);
		return list;
	}

	public static <T> List<T> arrayToList(T[] a) {
		return Arrays.asList(a);
	}

	

	public static String convertMapUrl(String mapUrl) {
		try {
			URL url = new URL(mapUrl);
			if ("plan.epfl.ch".equals(url.getHost())) {
				String qStr = url.getQuery();
				if (qStr != null) {
					String[] params = qStr.split("&");
					for (String p : params) {
						String[] param = p.split("=");
						if (param.length == 2 && ("room".equalsIgnoreCase(param[0]) || "q".equalsIgnoreCase(param[0]))) {
							return "pocketcampus://map.plugin.pocketcampus.org/search?q=" + param[1];
						}
					}

				}
			}
		} catch (MalformedURLException e) {
		}
		return mapUrl;
	}

	public static void registerForPush(List<String> tokens) {
		for (String t : tokens)
			PushNotifServiceImpl.pushNotifMap("events", t);
	}

}
