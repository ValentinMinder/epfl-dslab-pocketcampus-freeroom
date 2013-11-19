package org.pocketcampus.plugin.satellite.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.charset.Charset;

import org.pocketcampus.platform.sdk.shared.HttpClient;
import org.pocketcampus.plugin.satellite.server.AffluenceGetterImpl;
import org.pocketcampus.plugin.satellite.shared.*;

/**
 * Tests for AffluenceGetterImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class AffluenceGetterTests {
	// 0 is empty
	@Test
	public void zeroIsEmpty() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("0")).get();
		
		assertEquals(SatelliteAffluence.EMPTY, affluence);
	}
	
	// 1 is medium
	@Test
	public void oneIsMedium() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("1")).get();
		
		assertEquals(SatelliteAffluence.MEDIUM, affluence);
	}
	
	// 2 is crowded
	@Test
	public void twoIsCrowded() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("2")).get();
		
		assertEquals(SatelliteAffluence.CROWDED, affluence);
	}
	
	// 3 is full
	@Test
	public void threeIsFull() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("3")).get();
		
		assertEquals(SatelliteAffluence.FULL, affluence);
	}
	
	
	// 4 is closed
	@Test
	public void fourIsClosed() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("4")).get();
		
		assertEquals(SatelliteAffluence.CLOSED, affluence);
	}
	
	// another number is an error
	@Test
	public void anotherNumberIsError() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("42")).get();
		
		assertEquals(SatelliteAffluence.ERROR, affluence);
	}
	
	// not a number is an error
	@Test
	public void notANumberIsError() {
		SatelliteAffluence affluence = new AffluenceGetterImpl(getClient("ABC")).get();
		
		assertEquals(SatelliteAffluence.ERROR, affluence);
	}

	private static HttpClient getClient(final String returnValue){
		return new HttpClient(){
			@Override
			public String getString(String url, Charset charset) throws Exception {
				return returnValue;
			}		
		};
	}
}