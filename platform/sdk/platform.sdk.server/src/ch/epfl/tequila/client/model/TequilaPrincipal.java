/**
 * Client for Tequila (federated authentication and access control, http://tequila.epfl.ch) 
 * Copyright (C) EPFL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */
package ch.epfl.tequila.client.model;

import java.security.*;
import java.util.*;

/**
 * Principal holding the values returned by the Tequila server after a successful authentification.
 *
 * @author Laurent Boatto
 * @see java.security.Principal
 */
public final class TequilaPrincipal implements Principal
{
  private String _user;
  private String _host;
  private String _org;
  private Map _attributes;

  /**
   * Returns the user name.
   *
   * @return the user name.
   */
  public String getUser()
  {
    return _user;
  }

  /**
   * Sets the user name.
   *
   * @param user the user name.
   */
  public void setUser(String user)
  {
    _user = user;
  }

  /**
   * Returns the user name.
   *
   * @return the user name.
   */
  public String getName()
  {
    return _user;
  }

  /**
   * Returns the host the user is coming from. Beware it can be a proxy.
   *
   * @return the host the user is coming from. Beware it can be a proxy.
   */
  public String getHost()
  {
    return _host;
  }

  /**
   * Sets the user host.
   *
   * @param host the user host.
   */
  public void setHost(String host)
  {
    _host = host;
  }

  /**
   * Returns the organization that did the actual authentication.
   *
   * @return the organization that did the actual authentication.
   */
  public String getOrg()
  {
    return _org;
  }

  /**
   * Sets the organization that did the actual authentication.
   *
   * @param org the organization that did the actual authentication.
   */
  public void setOrg(String org)
  {
    _org = org;
  }

  /**
   * Returns the user attributes.
   *
   * @return the user attributes.
   */
  public Map getAttributes()
  {
    return _attributes;
  }

  /**
   * Sets the user attributes.
   *
   * @param attributes the user attributes.
   */
  public void setAttributes(Map attributes)
  {
    _attributes = attributes;
  }

  /**
   * Returns the attribute having the given name.
   *
   * @param name the attribute name.
   * @return the attribute having the given name.
   */
  public String getAttribute(String name)
  {
    return (String)_attributes.get(name);
  }

  public String toString()
  {
    return "[user=" + _user + ", org=" + _org + ", host=" + _host + ", attributes=" + _attributes + "]";
  }
}