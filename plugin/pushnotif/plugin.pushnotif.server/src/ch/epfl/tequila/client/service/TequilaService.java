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
package ch.epfl.tequila.client.service;

import ch.epfl.tequila.client.model.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Service for interacting with the Tequila server.
 *
 * @author Laurent Boatto
 */
public class TequilaService
{
  private static final TequilaService _instance = new TequilaService();

  /**
   * Private constructor, use instance instead.
   */
  private TequilaService()
  {
  }

  /**
   * Returns the instance of the TequilaService (singleton).
   *
   * @return the instance of the TequilaService (singleton).
   */
  public static final TequilaService instance()
  {
    return _instance;
  }

  /**
   * Creates a request on the Tequila server and returns the corresponding session key.
   *
   * @param config the ClientConfig.
   * @param urlaccess the url where the user is redirected after successful authentification.
   * @return the corresponding Tequila session key.
   * @throws IOException if the connection with the Tequila server cannot be made.
   */
  public String createRequest(ClientConfig config, String urlaccess) throws IOException
  {
    // the parameters
    StringBuffer parameters = new StringBuffer();

    appendParameter(parameters, "wish", config.getWish());
    appendParameter(parameters, "require", config.getRequire());
    appendParameter(parameters, "language", config.getLanguage());
    appendParameter(parameters, "service", config.getService());
    appendParameter(parameters, "request", config.getRequest());
    appendParameter(parameters, "wantright", config.getWantright());
    appendParameter(parameters, "wantrole", config.getWantrole());
    appendParameter(parameters, "org", config.getOrg());
    appendParameter(parameters, "allows", config.getAllows());
    appendParameter(parameters, "urlaccess", urlaccess);

    // we connect to the tequila server and POST the parameters
    URL url = new URL("http://" + config.getHost() + "/cgi-bin/tequila/createrequest");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("POST");

    connection.setDoOutput(true);

    OutputStream out = connection.getOutputStream();
    out.write(parameters.toString().getBytes());
    out.flush();
    out.close();

    // we get back the Tequila session key in the form "key=thekey"
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    String key = in.readLine();

    return key.substring(key.indexOf('=') + 1);
  }

  /**
   * Validates the given sesion key with the Tequila server and returns the corresponding TequilaPrincipal, holding
   * the known authenticated user informations.
   *
   * @param config the ClientConfig.
   * @param sessionKey the Tequila session key you get with createRequest.
   * @return the TequilaPrincipal holding the known authenticated user informations.
   * @throws IOException if the connection with the Tequila server cannot be made.
   * @throws SecurityException if the key is invalid.
   * @see #createRequest(ch.epfl.tequila.client.model.ClientConfig, String)
   */
  public TequilaPrincipal validateKey(ClientConfig config, String sessionKey) throws IOException
  {
    // first we connect to the Tequila server and POST the given session key
    URL url = new URL("http://" + config.getHost() + "/cgi-bin/tequila/validatekey");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("POST");

    connection.setDoOutput(true);

    String parameters = "key=" + sessionKey;

    OutputStream out = connection.getOutputStream();
    out.write(parameters.getBytes());
    out.flush();
    out.close();

    // if the response code is not 200, we have an invalid or expired key
    if (connection.getResponseCode() != 200) {
	return null;
	//throw new SecurityException("Invalid session key : " + sessionKey);
    }

    // next we get the response and we create a TequilaPrincipal with the returned informations
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    TequilaPrincipal principal = new TequilaPrincipal();

    Map attributes = new HashMap();

    // data is in the form key=value, one per line
    String line = in.readLine();
    while (line != null)
    {
      String[] keyValue = line.split("=");

      // check if we have a value
      if (keyValue.length > 1)
      {
        attributes.put(keyValue[0], keyValue[1]);
      }

      line = in.readLine();
    }

    // we have special getters/setters for these attributes..
    principal.setHost((String) attributes.get("host"));
    principal.setOrg((String) attributes.get("org"));
    principal.setUser((String) attributes.get("user"));

    // .. so we remove them from the Map
    attributes.remove("key");
    attributes.remove("host");
    attributes.remove("org");
    attributes.remove("user");

    principal.setAttributes(attributes);

    return principal;
  }

  /**
   * Appends the given key - value parameter if the value is not empty.
   *
   * @param buffer the buffer to append to.
   * @param key the parameter key.
   * @param value the parameter value.
   */
  private void appendParameter(StringBuffer buffer, String key, String value)
  {
    if (value != null && value.trim().length() > 0)
    {
      buffer.append(key + "=" + value + '\n');
    }
  }
}
