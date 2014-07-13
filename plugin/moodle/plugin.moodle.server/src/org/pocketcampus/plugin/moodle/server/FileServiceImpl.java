package org.pocketcampus.plugin.moodle.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
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
	public void download(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			final String gaspar = PocketCampusServer.authGetUserGasparFromReq(request);
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

			filePath = StringUtils.substringBetween(filePath, FILE_NAME_LEFT_GUARD, FILE_NAME_RIGHT_GUARD);
			filePath = DOWNLOAD_URL_PREFIX + filePath;

			final HttpURLConnection conn = (HttpURLConnection) new URL(filePath).openConnection();
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

			InputStream in = null;
			OutputStream out = null;
			try {
				in = conn.getInputStream();
				out = response.getOutputStream();

				IOUtils.copy(in, out);
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}

		} catch (Exception _) {
			// This is ugly, but it shouldn't ever be triggered anyway
			response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}
}