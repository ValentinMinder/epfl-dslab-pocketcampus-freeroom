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

/**
 * Bean holding the client configuration. See <a href="http://tequila.epfl.ch">tequila documentation</a>
 * for more informations about the attributes.
 *
 * @author Laurent Boatto
 */
public class ClientConfig
{
  private String _host;
  private String _wish;
  private String _require;
  private String _language;
  private String _service;
  private String _request;
  private String _wantright;
  private String _wantrole;
  private String _org;
  private String _allows;

  public String getHost()
  {
    return _host;
  }

  public void setHost(String host)
  {
    _host = host;
  }

  public String getWish()
  {
    return _wish;
  }

  public void setWish(String wish)
  {
    _wish = wish;
  }

  public String getRequire()
  {
    return _require;
  }

  public void setRequire(String require)
  {
    _require = require;
  }

  public String getLanguage()
  {
    return _language;
  }

  public void setLanguage(String language)
  {
    _language = language;
  }

  public String getService()
  {
    return _service;
  }

  public void setService(String service)
  {
    _service = service;
  }

  public String getRequest()
  {
    return _request;
  }

  public void setRequest(String request)
  {
    _request = request;
  }

  public String getWantright()
  {
    return _wantright;
  }

  public void setWantright(String wantright)
  {
    _wantright = wantright;
  }

  public String getWantrole()
  {
    return _wantrole;
  }

  public void setWantrole(String wantrole)
  {
    _wantrole = wantrole;
  }

  public String getOrg()
  {
    return _org;
  }

  public void setOrg(String org)
  {
    _org = org;
  }

  public String getAllows()
  {
    return _allows;
  }

  public void setAllows(String allows)
  {
    _allows = allows;
  }
}