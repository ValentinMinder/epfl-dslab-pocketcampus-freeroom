package org.pocketcampus.plugin.moodle.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.shared.utils.PostDataBuilder;
import org.pocketcampus.plugin.moodle.shared.Constants;

/**
 * Implementation of FileService.
 * 
 * TODO: Check if the user has access to the requested file!
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class FileServiceImpl implements FileService {
	@Override
	public void download(HttpServletRequest request, HttpServletResponse response) {
		try {
			String gaspar = PocketCampusServer.authGetUserGasparFromReq(request);
			if (gaspar == null) {
				response.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
				return;
			}

			String action = request.getParameter(Constants.MOODLE_RAW_ACTION_KEY);
			String filePath = request.getParameter(Constants.MOODLE_RAW_FILE_PATH);
			
			if (!Constants.MOODLE_RAW_ACTION_DOWNLOAD_FILE.equals(action) || filePath == null) {
				response.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
				return;
			}

			filePath = StringUtils.substringBetween(filePath, "pluginfile.php", "?");
			filePath = "http://moodle.epfl.ch/webservice/pluginfile.php" + filePath;

			HttpURLConnection conn = (HttpURLConnection) new URL(filePath).openConnection();
			conn.setDoOutput(true);

			PostDataBuilder builder = new PostDataBuilder().addParam("token", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN"));
			conn.getOutputStream().write(builder.toBytes());

			response.setContentType(conn.getContentType());
			response.setContentLength(conn.getContentLength());
			// "a means for the origin server to suggest a default filename if the user requests that the content is saved to a file"
			response.addHeader("Content-Disposition", conn.getHeaderField("Content-Disposition"));

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