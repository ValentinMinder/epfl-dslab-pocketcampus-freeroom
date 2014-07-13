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
import ch.epfl.tequila.client.service.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Filter thats checks if the user has been authenticated. If it's not the case, it redirects him/her to tequila.
 *
 * @author Laurent Boatto
 */
public class TequilaFilter implements Filter
{
  private static final ClientConfig _clientConfig = new ClientConfig();
  private static final TequilaService _tequilaService = TequilaService.instance();

  /**
   * The name of the session attribute holding the tequila key.
   */
  public static final String TEQUILA_KEY = "TEQUILA_KEY";

  /**
   * The name of the session attribute holding the TequilaPrincipal after successful authentification.
   */
  public static final String TEQUILA_PRINCIPAL = "TEQUILA_PRINCIPAL";

  public void init(FilterConfig config) throws ServletException
  {
    // client initialization according to web.xml
    _clientConfig.setHost(config.getInitParameter("host"));
    _clientConfig.setWish(config.getInitParameter("wish"));
    _clientConfig.setRequire(config.getInitParameter("require"));
    _clientConfig.setLanguage(config.getInitParameter("language"));
    _clientConfig.setService(config.getInitParameter("service"));
    _clientConfig.setRequest(config.getInitParameter("request"));
    _clientConfig.setWantright(config.getInitParameter("wantright"));
    _clientConfig.setWantrole(config.getInitParameter("wantrole"));
    _clientConfig.setAllows(config.getInitParameter("allows"));
    _clientConfig.setOrg(config.getInitParameter("org"));
    _clientConfig.setAuthstrength(config.getInitParameter("authstrength"));
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
  {
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpSession session = request.getSession();

    // the user has no Tequila key, we redirect him/her to the Tequila login page
    if (session.getAttribute(TEQUILA_KEY) == null)
    {
      String requestKey = _tequilaService.createRequest(_clientConfig, getRequestUrl(request));
      session.setAttribute(TEQUILA_KEY, requestKey);
      response.sendRedirect("https://" + _clientConfig.getHost() + "/cgi-bin/tequila/requestauth?requestkey=" + requestKey);
      return;
    }

    TequilaPrincipal principal = (TequilaPrincipal) session.getAttribute(TEQUILA_PRINCIPAL);

    // the user has a key, but it has not been validated yet
    if (principal == null)
    {
      try
      {
        principal = _tequilaService.validateKey(_clientConfig, (String)session.getAttribute(TEQUILA_KEY));
      }
      catch (SecurityException e)
      {
        // the key was invalid
        session.setAttribute(TEQUILA_KEY, null);

        doFilter(servletRequest, servletResponse, filterChain);
      }

      session.setAttribute(TEQUILA_PRINCIPAL, principal);
    }

    filterChain.doFilter(new TequilaHttpServletRequest(request, principal), response);
  }

  /**
   * Returns the requested url. Looks if we are behind a proxy.
   *
   * @param request the request.
   * @return the requested url
   */
  private String getRequestUrl(HttpServletRequest request)
  {
    StringBuffer result = new StringBuffer();

    result.append(request.getScheme() + "://");

    // are we behind a proxy?
    // todo this is set by mod_proxy, what about other proxies?
    String forwardedHost = request.getHeader("X-Forwarded-Host");

    if (forwardedHost != null)
    {
      result.append(forwardedHost);
    }
    else
    {
      result.append(request.getServerName());

      if (request.getServerPort() != 80)
      {
        result.append(":" + request.getServerPort());
      }
    }

    result.append(request.getRequestURI());

    String queryString = request.getQueryString();

    if (queryString != null)
    {
      result.append("?" + queryString);
    }

    return result.toString();
  }


  public void destroy()
  {
  }
}