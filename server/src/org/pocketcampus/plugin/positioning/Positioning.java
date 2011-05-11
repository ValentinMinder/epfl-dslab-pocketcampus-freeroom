package org.pocketcampus.plugin.positioning;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.permissions.IPermissionProvider;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

public class Positioning implements IPlugin, IPermissionProvider {
	
	@PublicMethod
	public boolean updatePosition(HttpServletRequest request) {
		String sLongitude = request.getParameter("longitude");
		String sLatitude = request.getParameter("latitude");
		return true;
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello";
	}
	
	@Override
	public Collection<Permission> getPermission() {
		LinkedList<Permission> list = new LinkedList<Permission>();
		list.add(new Permission("positioning"));
		
		return list;
	}

}
