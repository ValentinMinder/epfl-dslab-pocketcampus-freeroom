package org.pocketcampus.plugin.moodle.server;

import org.apache.commons.io.IOUtils;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.moodle.shared.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
	public static final String TOKEN_KEY = "token";

	// Missing from Apache's HttpHeaders constant for some reason
	public static final String HTTP_CONTENT_DISPOSITION = "Content-Disposition";

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

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(convertFilePath(filePath)).openConnection();
			conn.setDoOutput(true);

			conn.getOutputStream().write(preparePostData(token));

			conn.connect();

			response.setContentType(conn.getContentType());
			response.setContentLength(conn.getContentLength());
			// "a means for the origin server to suggest a default filename if the user requests that the content is saved to a file"
			if (conn.getHeaderField(HTTP_CONTENT_DISPOSITION) != null) {
				response.addHeader(HTTP_CONTENT_DISPOSITION, conn.getHeaderField(HTTP_CONTENT_DISPOSITION));
			}

			IOUtils.copy(conn.getInputStream(), response.getOutputStream());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	public static String convertFilePath(String filePath) {
		return DOWNLOAD_URL_PREFIX + StringUtils.getSubstringBetween(filePath, FILE_NAME_LEFT_GUARD, FILE_NAME_RIGHT_GUARD);
	}
	
	public static byte[] preparePostData(String token) {
		return new PostDataBuilder().addParam(TOKEN_KEY, token).toString().getBytes();
	}
	
}
