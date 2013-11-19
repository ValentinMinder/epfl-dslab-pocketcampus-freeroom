package org.pocketcampus.plugin.satellite.server;

import java.nio.charset.StandardCharsets;

import org.pocketcampus.platform.sdk.shared.HttpClient;
import org.pocketcampus.plugin.satellite.shared.SatelliteAffluence;

/**
 * Gets the affluence at Satellite from their API.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class AffluenceGetterImpl implements AffluenceGetter {
	private static final String AFFLUENCE_URL = "http://sat.epfl.ch/affluence";

	private final HttpClient _client;

	public AffluenceGetterImpl(HttpClient client) {
		_client = client;
	}

	@Override
	public SatelliteAffluence get() {
		int result = -1;
		try {
			String resultString = _client.getString(AFFLUENCE_URL, StandardCharsets.UTF_8);
			result = Integer.parseInt(resultString);
		} catch (Exception e) {
			return SatelliteAffluence.ERROR;
		}

		SatelliteAffluence affluence = SatelliteAffluence.findByValue(result);
		return affluence == null ? SatelliteAffluence.ERROR : affluence;
	}
}