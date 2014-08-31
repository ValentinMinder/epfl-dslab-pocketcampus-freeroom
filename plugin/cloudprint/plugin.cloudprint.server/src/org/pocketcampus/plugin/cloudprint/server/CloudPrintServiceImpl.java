package org.pocketcampus.plugin.cloudprint.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintStatusCode;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentResponse;

import com.google.gson.Gson;

/**
 * CloudPrintServiceImpl
 * 
 * The implementation of the server side of the CloudPrint Plugin.
 * It print on EPFL's pool
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CloudPrintServiceImpl implements CloudPrintService.Iface, RawPlugin {
	
	public CloudPrintServiceImpl() {
		System.out.println("Starting CloudPrint plugin server ...");
	}


	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			private static final long serialVersionUID = -6760157045775850293L;
			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(request);
				if (gaspar == null) {
					response.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}
				long id = System.currentTimeMillis();
				String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + id;
			    new File(filePath).mkdirs();
				//String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
			    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
				if (filePart == null) {
					response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
					return;
				}
			    String filename = getFilenameFromContentDisposition(filePart.getHeader("Content-Disposition"));
			    InputStream filecontent = filePart.getInputStream();
			    FileOutputStream fos = new FileOutputStream(filePath + "/" + filename);
			    IOUtils.copy(filecontent, fos);
			    response.getOutputStream().write(new Gson().toJson(new CloudPrintUploadResponse(id)).getBytes());
			}
		};
	}
	
	public static class CloudPrintUploadResponse {
		public long file_id;
		public CloudPrintUploadResponse(long file_id){
			this.file_id = file_id;
		}
	}

	@Override
	public PrintDocumentResponse printDocument(PrintDocumentRequest request) throws TException {
		String gaspar = AuthenticationServiceImpl.authGetUserGaspar();
		if (gaspar == null) {
			return new PrintDocumentResponse(CloudPrintStatusCode.AUTHENTICATION_ERROR);
		}
		String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + request.getDocumentId();
		String [] files = new File(filePath).list();
		if(files == null || files.length == 0) {
			return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);	    	
	    }
		List<String> command = new LinkedList<String>();
		command.add("lpr");
		command.add("-P");command.add("mainPrinter");
		command.add("-U");command.add(gaspar);
		command.add("-#");command.add(request.getNumberOfCopies() + "");
		command.add("-r");
		if(request.isSetPageSelection()) {
			command.add("-o");command.add("page-ranges=" + request.getPageSelection().getPageFrom() + "-" + request.getPageSelection().getPageTo());
		}
		if(request.isDoubleSided()) {
			command.add("-o");command.add("sides=two-sided-long-edge");			
		}
		if(request.isBlackAndWhite()) {
			command.add("-o");command.add("JCLColorCorrection=BlackWhite");
		}
		
		command.add("-T");command.add(files[0]);
		command.add(filePath + "/" + files[0]);
		try {
			Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
			new PrintDocumentResponse(CloudPrintStatusCode.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
	}

	public static String getFilenameFromContentDisposition(String contentDisposition) {
	    for (String cd : contentDisposition.split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}
}
