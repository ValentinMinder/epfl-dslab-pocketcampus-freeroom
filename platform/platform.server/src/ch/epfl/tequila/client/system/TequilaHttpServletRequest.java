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
package ch.epfl.tequila.client.system;

import ch.epfl.tequila.client.model.*;

import javax.servlet.http.*;
import java.security.*;

/**
 * Simple wrapper around HttpServletRequest used to return the TequilaPrincipal for <code>request.getUserPrincipal()</code>.
 *
 * @author Laurent Boatto
 */
public class TequilaHttpServletRequest extends HttpServletRequestWrapper
{
  private Principal _principal;

  public TequilaHttpServletRequest(HttpServletRequest request, TequilaPrincipal principal)
  {
    super(request);
    _principal = principal;
  }

  public String getRemoteUser()
  {
    return _principal.getName();
  }

  public Principal getUserPrincipal()
  {
    return _principal;
  }

  public void setUserPrincipal(Principal principal)
  {
    _principal = principal;
  }
}
