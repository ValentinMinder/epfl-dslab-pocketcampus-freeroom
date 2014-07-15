package org.pocketcampus.plugin.moodle.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.moodle.shared.Constants;

/**
 * Implementation of FileService using Moodle's web service API.
 * 
 * TODO: Check if the user has access to the requested file!
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class FileServiceImpl implements FileService {
	// Guards between the file name in the link we receive
	private static final String FILE_NAME_LEFT_GUARD = "pluginfile.php";
	private static final String FILE_NAME_RIGHT_GUARD = "?";

	// Prefix for the download URL of a file, using Moodle's web service.
	private static final String DOWNLOAD_URL_PREFIX = "http://moodle.epfl.ch/webservice/pluginfile.php";

	// The key of the token parameter to download files
	private static final String TOKEN_KEY = "token";

	// Missing from Apache's HttpHeaders constant for some reason
	private static final String HTTP_CONTENT_DISPOSITION = "Content-Disposition";

	private final String token;

	public FileServiceImpl(final String token) {
		this.token = token;
	}

	@Override
	public void download(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(request);
		if (gaspar == null) {
			response.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
			return;
		}

		final String action = request.getParameter(Constants.MOODLE_RAW_ACTION_KEY);
		String filePath = request.getParameter(Constants.MOODLE_RAW_FILE_PATH);

		if (!Constants.MOODLE_RAW_ACTION_DOWNLOAD_FILE.equals(action) || filePath == null) {
			response.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
			return;
		}

		filePath = StringUtils.getSubstringBetween(filePath, FILE_NAME_LEFT_GUARD, FILE_NAME_RIGHT_GUARD);
		filePath = DOWNLOAD_URL_PREFIX + filePath;

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(filePath).openConnection();
			conn.setDoOutput(true);

			final byte[] bytes = new PostDataBuilder()
					.addParam(TOKEN_KEY, token)
					.toString()
					.getBytes();
			conn.getOutputStream().write(bytes);

			conn.connect();

			response.setContentType(conn.getContentType());
			response.setContentLength(conn.getContentLength());
			// "a means for the origin server to suggest a default filename if the user requests that the content is saved to a file"
			response.addHeader(HTTP_CONTENT_DISPOSITION, conn.getHeaderField(HTTP_CONTENT_DISPOSITION));

			IOUtils.copy(conn.getInputStream(), response.getOutputStream());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}