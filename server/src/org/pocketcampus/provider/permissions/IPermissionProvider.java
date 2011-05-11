package org.pocketcampus.provider.permissions;

import java.util.Collection;

import org.pocketcampus.core.provider.IProvider;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

public interface IPermissionProvider extends IProvider {
	abstract public Collection<Permission> getPermission();
}
