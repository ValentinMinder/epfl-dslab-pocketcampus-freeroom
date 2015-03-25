package org.pocketcampus.platform.server.tests;

import org.junit.Test;
import org.pocketcampus.platform.server.HttpClientImpl;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/** 
 * Tests for HttpClientImpl.
 * This requires an active Internet connection,
 * and assumes perdu.com doesn't change (it's unlikely...this site is old)
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class HttpClientTests {
	// I don't know any small test pages for charsets... the EPFL website's encoding is messed up anyway, it's not that important.
	
    @Test
    public void getStringWorks() throws Exception {
    	HttpClientImpl client = new HttpClientImpl();
    	
    	String s = client.get("http://perdu.com", Charset.forName("UTF-8"));
    	
    	assertEquals("<html><head><title>Vous Etes Perdu ?</title></head><body><h1>Perdu sur l'Internet ?</h1><h2>Pas de panique, on va vous aider</h2><strong><pre>    * <----- vous &ecirc;tes ici</pre></strong></body></html>", s.trim());
    }
}