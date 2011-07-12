package org.pocketcampus.plugin.social;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.authentication.Authentication;
import org.pocketcampus.plugin.authentication.AuthenticationSessions;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.provider.permissions.IPermissionProvider;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.social.FriendsLists;
import org.pocketcampus.shared.plugin.social.User;
import org.pocketcampus.shared.plugin.social.UserInfo;
import org.pocketcampus.shared.plugin.social.exception.ConflictingPermissionException;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

public class Social implements IPlugin, IMapElementsProvider, IPermissionProvider {

	private static final String DBID_FRIENDSHIP = "friendship";
	private static final String DBID_POSITION = "position";
	
	@PublicMethod
	public FriendsLists send(HttpServletRequest request) {
		FriendsLists lists = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			User target = Authentication.identifyBySciper(targetId);
			try {
				if(
						!user.equals(target) &&
						!SocialDatabase.testFriend(user, target) && 
						!SocialDatabase.testPending(DBID_FRIENDSHIP, user, target) &&
						!SocialDatabase.testPending(DBID_FRIENDSHIP, target, user))
				{
					SocialDatabase.addPending(DBID_FRIENDSHIP, user, target);
				}
				
				lists = getLists(user);
				
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		return lists;
	}

	@PublicMethod
	public FriendsLists delete(HttpServletRequest request) {
		FriendsLists lists = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			User target = Authentication.identifyBySciper(targetId);

			try {
				if(
						SocialDatabase.testFriend(user, target))
				{
					SocialDatabase.removeAllPermissions(user, target);
					SocialDatabase.removeAllPermissions(target, user);
					SocialDatabase.removeFriend(user, target);
				}
				
				lists = getLists(user);
				
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		return lists;
	}

	@PublicMethod
	public FriendsLists accept(HttpServletRequest request) {
		FriendsLists lists = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			User target = Authentication.identifyBySciper(targetId);

			try {
				if(
						SocialDatabase.testPending(DBID_FRIENDSHIP, target, user))
				{
					SocialDatabase.removePending(DBID_FRIENDSHIP, target, user);
					SocialDatabase.addFriend(user, target);
				}
				
				lists = getLists(user);
				
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		
		return lists;
	}

	@PublicMethod
	public FriendsLists ignore(HttpServletRequest request) {
		FriendsLists lists = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			User target = Authentication.identifyBySciper(targetId);

			try {
				if(
						SocialDatabase.testPending(DBID_FRIENDSHIP, target, user))
				{
					SocialDatabase.removePending(DBID_FRIENDSHIP, target, user);
				}
				
				lists = getLists(user);
				
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		return lists;
	}

	@PublicMethod
	public FriendsLists friends(HttpServletRequest request) {
		FriendsLists friendsLists = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");

		if(username != null && sessionId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);

			try {

				
				friendsLists = getLists(user);

			} catch(ServerException e) {
				e.printStackTrace();
				friendsLists = null;
			}
		}
		return friendsLists;
	}

	/**
	 * Returns the different permission types available
	 * @param request
	 * @return
	 */
	@PublicMethod
	public Collection<Permission> permissions(HttpServletRequest request) {
		LinkedList<Permission> permissions = new LinkedList<Permission>();

		Iterator<IPlugin> iter = Core.getInstance().getProvidersOf(IPermissionProvider.class).iterator();

		while(iter.hasNext()) {
			Collection<Permission> list = ((IPermissionProvider) iter.next()).getPermission();
			for(Permission p : list) {
				if(!permissions.contains(p)) {
					permissions.add(p);
				} else {
					throw new ConflictingPermissionException("duplicate "+p+" permission.");
				}
			}
		}

		return permissions;
	}

	/**
	 * Returns the permissions that have been granted to a particular user
	 * @param request
	 * @return
	 */
	@PublicMethod
	public Collection<Permission> getPermissions(HttpServletRequest request) {
		LinkedList<Permission> permissions = null;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String target = request.getParameter("granted_to");

		if(username != null && sessionId != null && target != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			User granted_to = Authentication.identifyBySciper(target);

			try {
				permissions = new LinkedList<Permission>();

				for(String s : SocialDatabase.getPermissions(user, granted_to)) {
					permissions.add(new Permission(s));
				}
			} catch(ServerException e) {
				permissions = null;
			}
		}

		return permissions;
	}

	@PublicMethod
	public FriendsLists updatePermissions(HttpServletRequest request) throws ServerException {
		FriendsLists lists = null;
		
		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		String sN = request.getParameter("n");

		if(username != null && sessionId != null && sN != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			int n = 0;
			try {
				n = Integer.parseInt(sN);
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}

			for(int i = 0; i < n; i++) {
				String service = request.getParameter("permission__"+i);
				String targetId = request.getParameter("user__"+i);
				String operation = request.getParameter("granted__"+i);
				if(service != null && targetId != null && operation != null) {
					User target = Authentication.identifyBySciper(targetId);
					try {
						if(operation.equals("yes") && !SocialDatabase.testPermission(service, user, target)) {
							SocialDatabase.addPermission(service, user, target);
						} else if(operation.equals("no") && SocialDatabase.testPermission(service, user, target)) {
							SocialDatabase.removePermission(service, user, target);
						}
					} catch(ServerException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			lists = getLists(user);
		}
		
		return lists;
	}

	@PublicMethod
	public boolean updatePosition(HttpServletRequest request) {
		boolean status = false;

		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");

		if(username != null && sessionId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			double longitude = 0; 
			double latitude = 0;
			double altitude = 0;

			try {
				longitude = Double.parseDouble(request.getParameter("longitude"));
				latitude = Double.parseDouble(request.getParameter("latitude"));
				altitude = Double.parseDouble(request.getParameter("altitude"));
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
			try {
				SocialDatabase.updatePosition(user, longitude, latitude, altitude);
			} catch(ServerException e) {
				e.printStackTrace();
				return false;
			}
			
			status = true;
		} else {
			status = false;
		}

		return status;
	}
	
	@PublicMethod
	public void requestPositions(HttpServletRequest request) {
		String username = request.getParameter("sciper");
		String sessionId = request.getParameter("sessionId");
		
		if(username != null && sessionId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identifyBySciper(username);
			int n = Integer.parseInt(request.getParameter("n"));
			
			for(int i = 0; i < n; i++) {
				String targetSciper = request.getParameter("target__"+i);
				User target = Authentication.identifyBySciper(targetSciper);
				
				try {
					if(
							AuthenticationSessions.isOnline(targetSciper) && //target is online
							SocialDatabase.testPermission("positioning", target, user) && //target shares position with user
							!SocialDatabase.testPending(DBID_POSITION, user, target)) //user hasn't already sent a request
					{
						SocialDatabase.addPending(DBID_POSITION, user, target);
					}
				} catch(ServerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<MapLayerBean> getLayers() {
		List<MapLayerBean> l = new ArrayList<MapLayerBean>();
		l.add(new MapLayerBean("Amis", "data/map/map_marker_people.png", this, 1, 300, true));
		return l;
	}
	
	@Override
	public List<MapElementBean> getLayerItems(AuthToken token, int layerId) {
		List<MapElementBean> items = new ArrayList<MapElementBean>();
		
		if(token != null) {
			User me = Authentication.identifyBySciper(token.getSciper());
			String serviceId = "positioning";
			long timeout = 1000 * 60 * 60;

			try {
				Iterator<SocialPosition> pos = SocialDatabase.getPositions(SocialDatabase.getVisibleFriends(me, serviceId), timeout).iterator();
				while(pos.hasNext()) {
					SocialPosition position = pos.next();
					items.add(new MapElementBean(
							position.getUser().toString(), 
							"Mise à jour il y a " + toMinutes(position.getTimestamp()) + " minute(s)", 
							position.getPosition().getLatitude(), 
							position.getPosition().getLongitude(), 
							position.getPosition().getAltitude(), 
							layerId,
							position.getUser().getIdFormat().hashCode()));
				}
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		
		return items;
	}
	
	/**
	 * approximate number of minutes elapsed since timestamp
	 * @param timestamp
	 * @return
	 */
	private String toMinutes(Timestamp timestamp) {
		long stamp = timestamp.getTime();
		long now = new Date().getTime();

		long deltaInMinutes = (int)((now - stamp) / (1000 * 60));

		return deltaInMinutes+"";
	}
	
	@Override
	public Collection<Permission> getPermission() {
		LinkedList<Permission> list = new LinkedList<Permission>();
		list.add(new Permission("connection status"));
		
		return list;
	}

	private FriendsLists getLists(User user) throws ServerException {
		HashMap<User, Boolean> friends = SocialDatabase.getFriendsWithStatus(user);
		Collection<User> pendings = SocialDatabase.getPending(DBID_FRIENDSHIP, user);
		Collection<User> pendingsPosition = SocialDatabase.getPending(DBID_POSITION, user);
		
		HashSet<UserInfo> users = new HashSet<UserInfo>();
		for(User friend : friends.keySet()) {
			users.add(new UserInfo(friend, false, friends.get(friend), pendingsPosition.contains(friend)));
		}
		for(User requesting : pendings) {
			users.add(new UserInfo(requesting, true, false, false));
		}
		
		//clear requests
		for(User u : pendingsPosition) {
			SocialDatabase.removePending(DBID_POSITION, u, user);
		}
		
		return new FriendsLists(users);
	}
	
	//Clear all position requests addressed to user. Called at logout.
	public static void clearPositionRequests(User user) throws ServerException {
		Collection<User> pendings = SocialDatabase.getPending(DBID_POSITION, user);
		
		for(User u : pendings) {
			SocialDatabase.removePending(DBID_POSITION, u, user);
		}
	}
}
