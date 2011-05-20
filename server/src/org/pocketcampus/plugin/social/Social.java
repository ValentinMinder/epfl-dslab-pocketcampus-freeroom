package org.pocketcampus.plugin.social;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
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
import org.pocketcampus.shared.plugin.social.exception.ConflictingPermissionException;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

public class Social implements IPlugin, IMapElementsProvider {

	@PublicMethod
	public boolean send(HttpServletRequest request) {
		boolean status = false;

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			User target = new User(targetId);

			try {
				if(
						!user.equals(target) &&
						!SocialDatabase.testFriend(user, target) && 
						!SocialDatabase.testPending(user, target) &&
						!SocialDatabase.testPending(target, user))
				{
					status = SocialDatabase.addPending(user, target);
				}
			} catch(ServerException e) {
				e.printStackTrace();
				status = false;
			}
		}
		return status;
	}

	@PublicMethod
	public boolean delete(HttpServletRequest request) {
		boolean status = false;

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			User target = new User(targetId);

			try {
				if(
						SocialDatabase.testFriend(user, target))
				{
					status = SocialDatabase.removeFriend(user, target);
				}
			} catch(ServerException e) {
				e.printStackTrace();
				status = false;
			}
		}
		return status;
	}

	@PublicMethod
	public boolean accept(HttpServletRequest request) {
		boolean status = false;

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			User target = new User(targetId);

			try {
				if(
						SocialDatabase.testPending(target, user))
				{
					status = SocialDatabase.removePending(target, user) && SocialDatabase.addFriend(user, target);
				}
			} catch(ServerException e) {
				e.printStackTrace();
				status = false;
			}
		}
		return status;
	}

	@PublicMethod
	public boolean ignore(HttpServletRequest request) {
		boolean status = false;

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String targetId = request.getParameter("target");

		if(username != null && sessionId != null && targetId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			User target = new User(targetId);

			try {
				if(
						SocialDatabase.testPending(target, user))
				{
					status = SocialDatabase.removePending(target, user);
				}
			} catch(ServerException e) {
				e.printStackTrace();
				status = false;
			}
		}
		return status;
	}

	@PublicMethod
	public FriendsLists friends(HttpServletRequest request) {
		FriendsLists friendsLists = null;

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");

		if(username != null && sessionId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);

			try {

				LinkedList<User> friends = new LinkedList<User>(SocialDatabase.getFriends(user));
				LinkedList<User> pendings = new LinkedList<User>(SocialDatabase.getPending(user));

				friendsLists = new FriendsLists(friends, pendings);

			} catch(ServerException e) {
				e.printStackTrace();
				friendsLists = null;
			}
		}
		Toolkit.getDefaultToolkit().beep(); //==============================================================================
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

		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String target = request.getParameter("granted_to");

		if(username != null && sessionId != null && target != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			User granted_to = new User(target);

			try {
				permissions = new LinkedList<Permission>();

				for(String s : SocialDatabase.getPermissions(user, granted_to)) {
					System.out.println("\n"+s);
					permissions.add(new Permission(s));
				}
			} catch(ServerException e) {
				permissions = null;
			}
		}

		return permissions;
	}

	@PublicMethod
	public void updatePermissions(HttpServletRequest request) {
		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		String sN = request.getParameter("n");

		if(username != null && sessionId != null && sN != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
			int n = 0;
			try {
				n = Integer.parseInt(sN);
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}

			for(int i = 0; i < n; i++) {
				String service = request.getParameter("permission__"+i);
				String target = request.getParameter("user__"+i);
				String operation = request.getParameter("granted__"+i);
				if(service != null && target != null && operation != null) {
					try {
						if(operation.equals("yes") && !SocialDatabase.testPermission(service, user, new User(target))) {
							SocialDatabase.addPermission(service, user, new User(target));
						} else if(operation.equals("no") && SocialDatabase.testPermission(service, user, new User(target))) {
							SocialDatabase.removePermission(service, user, new User(target));
						}
					} catch(ServerException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@PublicMethod
	public boolean updatePosition(HttpServletRequest request) {
		boolean status = false;
		
		Toolkit.getDefaultToolkit().beep(); 
		
		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		
		if(username != null && sessionId != null && AuthenticationSessions.authenticateSession(username, sessionId)) {
			User user = Authentication.identify(username);
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
		}
		
		return status;
	}

	@Override
	public List<MapLayerBean> getLayers() {
		List<MapLayerBean> l = new ArrayList<MapLayerBean>();
		l.add(new MapLayerBean("Amis", "data/map/map_marker.png", this, 1, 300, true));
		return l;
	}

	@Override
	public List<MapElementBean> getLayerItems(AuthToken token, int layerId) {
		List<MapElementBean> items = new ArrayList<MapElementBean>();
		
		items.add(new MapElementBean("Your token", token.getSessionId(), 46.520101, 6.565189, 0, 1, 0));
		
		return items;
	}
}
