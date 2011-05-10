package org.pocketcampus.plugin.social;

import java.awt.Toolkit;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.authentication.Authentication;
import org.pocketcampus.plugin.authentication.AuthenticationSessions;
import org.pocketcampus.shared.plugin.social.FriendsLists;
import org.pocketcampus.shared.plugin.social.User;

public class Social implements IPlugin {

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
				Toolkit.getDefaultToolkit().beep();
				if(!friends.isEmpty()) {
					Toolkit.getDefaultToolkit().beep();
					Toolkit.getDefaultToolkit().beep();
					Toolkit.getDefaultToolkit().beep();
				}
				friendsLists = new FriendsLists(friends, pendings);

			} catch(ServerException e) {
				e.printStackTrace();
				friendsLists = null;
			}
		}
		return friendsLists;
	}
}
