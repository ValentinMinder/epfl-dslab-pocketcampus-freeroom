package org.pocketcampus.plugin.moodle.server;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.PCConstants;
import org.pocketcampus.plugin.cloudprint.server.CloudPrintServiceImpl;
import org.pocketcampus.plugin.cloudprint.server.CloudPrintServiceImpl.CloudPrintUploadResponse;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileResponse2;
import org.pocketcampus.plugin.moodle.shared.MoodleStatusCode2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of PrintService.
 * 
 * TODO: Make sure the user has access to the file.
 * 
 * @author Amer Chamseddine <amer@pocketcampus.org>
 */

public class PrintFileServiceImpl implements PrintFileService {

	private final Authenticator authenticator;
	private final String token;

	public PrintFileServiceImpl(final Authenticator authenticator, final String token) {
		this.authenticator = authenticator;
		this.token = token;
	}

	@Override
	public MoodlePrintFileResponse2 printFile(final MoodlePrintFileRequest2 request) {
		final String sciper = authenticator.getSciper();
		if (sciper == null) {
			return new MoodlePrintFileResponse2(MoodleStatusCode2.AUTHENTICATION_ERROR);
		}
		
		String pcSessionId = PocketCampusServer.getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		

		

		try {
			// DOWNLOAD FROM

			
			HttpURLConnection conn = (HttpURLConnection) new URL(FileServiceImpl.convertFilePath(request.getFileUrl())).openConnection();
			conn.setDoOutput(true);
			conn.getOutputStream().write(FileServiceImpl.preparePostData(token));

			InputStream fileInputStream = conn.getInputStream();

			
			String fileName = CloudPrintServiceImpl.getFilenameFromContentDispositionWithoutAccents(conn.getHeaderField(FileServiceImpl.HTTP_CONTENT_DISPOSITION));
			String contentType = conn.getContentType();
			int contentLength = conn.getContentLength();
			
			// UPLOAD TO
			
			String serverPort = PocketCampusServer.CONFIG.getString("LISTEN_ON_PORT");
			String uriPrefix = PocketCampusServer.CONFIG.getString("SERVER_URI_PREFIX");
			String urlToConnect = "http://localhost:" + serverPort + "/" + uriPrefix + "/raw-cloudprint";
			String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

			HttpURLConnection connection = (HttpURLConnection) new URL(urlToConnect).openConnection();
			
			connection.setDoOutput(true); // This sets request method to POST.
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.addRequestProperty(PCConstants.HTTP_HEADER_AUTH_PCSESSID, pcSessionId);
			
			OutputStream os = connection.getOutputStream();
			os.write(("--" + boundary + "\r\n").getBytes());
			os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n").getBytes());
			os.write(("Content-Type: " + contentType + "\r\n").getBytes());
			os.write(("Content-Length: " + contentLength + "\r\n").getBytes());
			os.write("\r\n".getBytes());
			IOUtils.copy(fileInputStream, os);
			os.write(("--" + boundary + "--\r\n").getBytes());

			// Connection is lazily executed whenever you request any status.
			int responseCode = connection.getResponseCode();
			if(responseCode != 200) {
				System.out.println("ERROR: returned status " + responseCode);
				return new MoodlePrintFileResponse2(MoodleStatusCode2.NETWORK_ERROR);
			}
			InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
			CloudPrintUploadResponse resp = new Gson().fromJson(isr, CloudPrintUploadResponse.class);
			MoodlePrintFileResponse2 ret = new MoodlePrintFileResponse2(MoodleStatusCode2.OK);
			ret.setPrintJobId(resp.file_id);
			return ret;
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return new MoodlePrintFileResponse2(MoodleStatusCode2.NETWORK_ERROR);
		}
		
	}

}
