package org.pocketcampus.plugin.cloudprint.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintColorConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageLayout;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintOrientation;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintStatusCode;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentResponse;
import org.pocketcampus.plugin.cloudprint.shared.PrintPreviewDocumentResponse;

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
public class CloudPrintServiceImpl implements CloudPrintService.Iface, RawPlugin, StateChecker {
	
	private static final int PRINT_PREVIEW_TIMEOUT = 30000;
	
	public CloudPrintServiceImpl() {
		System.out.println("Starting CloudPrint plugin server ...");
	}

	@Override
	public int checkState() throws IOException {
		cleanupIfNeeded();
		return (checkPrinterEnabled("mainPrinter") && checkPrinterEnabled("Cups-PDF") ? 200 : 500 );
	}
	
	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			private static final long serialVersionUID = -6760157045775850293L;
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				cleanupIfNeeded();
				long id = Long.parseLong(req.getParameter("file_id"));
				int page = Integer.parseInt(req.getParameter("page"));
				String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(req);
				if (gaspar == null) {
					resp.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}
				String filename = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR") + "/" + gaspar + "_" + id;
				String pdf = filename + ".pdf";
				String png = filename + ".png";
				if(!new File(pdf).exists()) {
					resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
					return;
				}
				Map<String, String> pdfInfo = getPdfInfo(pdf);
				if(!pdfInfo.containsKey("Pages")) {
					resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
					return;
				}
				if(page < 0 || page >= Integer.parseInt(pdfInfo.get("Pages"))) {
					resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
					return;
				}
				String [] command = new String[]{"convert", String.format("%s[%d]", pdf, page), png};
				System.out.println("$ " + StringUtils.join(command, " "));
				try {
					int exitVal = Runtime.getRuntime().exec(command).waitFor();
					if(exitVal != 0) {
						resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
					return;
				}
				if(!new File(png).exists()) {
					resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
					return;
				}
				
				resp.setContentType("image/png");
				InputStream in = new FileInputStream(new File(png));
				OutputStream out = resp.getOutputStream();
				IOUtils.copy(in, out);
				out.close();
				
				new File(png).delete();
			}
			@Override
			protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				cleanupIfNeeded();
				String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(req);
				if (gaspar == null) {
					resp.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}
				long id = System.currentTimeMillis();
				String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + id;
			    synchronized(oldFileCleaner) { // we don't want the cleaner to delete the freshly created directory before we put a file in it
					new File(filePath).mkdirs();
					//String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
				    Part filePart = req.getPart("file"); // Retrieves <input type="file" name="file">
					if (filePart == null) {
						resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
						return;
					}
				    String filename = getFilenameFromContentDispositionWithoutAccents(filePart.getHeader("content-disposition"));
				    InputStream filecontent = filePart.getInputStream();
				    FileOutputStream fos = new FileOutputStream(filePath + "/" + filename);
				    IOUtils.copy(filecontent, fos);
				    fos.close();
			    }

				resp.setContentType("application/json");
				resp.getOutputStream().write(new Gson().toJson(new CloudPrintUploadResponse(id)).getBytes());
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
		cleanupIfNeeded();
		String gaspar = AuthenticationServiceImpl.authGetUserGaspar();
		if (gaspar == null) {
			return new PrintDocumentResponse(CloudPrintStatusCode.AUTHENTICATION_ERROR);
		}
		String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + request.getDocumentId();
		String [] files = new File(filePath).list();
		if(files == null || files.length == 0) {
			return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);	    	
	    }
		try {
			boolean succ = printFile("mainPrinter", gaspar, files[0], filePath + "/" + files[0], request, null, false);
			if(!succ)
				return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
			return new PrintDocumentResponse(CloudPrintStatusCode.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new PrintDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
		}
	}
	
	@Override
	public PrintPreviewDocumentResponse printPreview(PrintDocumentRequest request) throws TException {
		cleanupIfNeeded();
		String gaspar = AuthenticationServiceImpl.authGetUserGaspar();
		if (gaspar == null) {
			return new PrintPreviewDocumentResponse(CloudPrintStatusCode.AUTHENTICATION_ERROR);
		}
		String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + request.getDocumentId();
		String [] files = new File(filePath).list();
		if(files == null || files.length == 0) {
			return new PrintPreviewDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);	    	
	    }
		try {
			boolean succ = printFile("Cups-PDF", null, gaspar + "_" + request.getDocumentId(), filePath + "/" + files[0], request, null, true);
			if(!succ)
				return new PrintPreviewDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
			String pdfFilename = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR") + "/" + gaspar + "_" + request.getDocumentId() + ".pdf";
			Map<String, String> pdfInfo = getPdfInfo(pdfFilename);
			if(!pdfInfo.containsKey("Pages"))
				return new PrintPreviewDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
			return new PrintPreviewDocumentResponse(CloudPrintStatusCode.OK).setNumberOfPages(Integer.parseInt(pdfInfo.get("Pages")));
		} catch (IOException e) {
			e.printStackTrace();
			return new PrintPreviewDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new PrintPreviewDocumentResponse(CloudPrintStatusCode.PRINT_ERROR);
		}
	}

	private static boolean printFile(String printer, String gaspar, String jobTitle, String filePath, PrintDocumentRequest request, List<String> extraOptions, boolean wait) throws IOException, InterruptedException {
		if(request.isSetMultiPageConfig() && (request.getMultiPageConfig().getNbPagesPerSheet().getValue() == 2 || request.getMultiPageConfig().getNbPagesPerSheet().getValue() == 6)) {
			List<String> extras = new LinkedList<String>();
			if(request.isSetOrientation()) {
				switch (request.getOrientation()) {
				case PORTRAIT:
				case REVERSE_PORTRAIT:
					extras.add("-o");extras.add("media=Custom.600x450");
					//extras.add("-o");extras.add("media=Custom.700x660");
					break;
				case LANDSCAPE:
				case REVERSE_LANDSCAPE:
					extras.add("-o");extras.add("media=Custom.450x600");
					//extras.add("-o");extras.add("media=Custom.660x700");
					break;
				}
			}
			String fileName = "ihate6pagespersheet_" + System.nanoTime() + ".pdf";
			boolean succ = printFile("Cups-PDF", null, fileName, filePath, new PrintDocumentRequest(), extras, true);
			if(!succ)
				return false;
			request.setOrientation(CloudPrintOrientation.PORTRAIT);
			filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR") + "/" + fileName;
		}
		String[] command = buildLpCommand(printer, gaspar, jobTitle, filePath, request, extraOptions);
		System.out.println("$ " + StringUtils.join(command, " "));
		Process proc = Runtime.getRuntime().exec(command);
		String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
		System.out.println(status);
		if(!checkLpOutput(status))
			return false;
		if(!wait)
			return true;
		return waitForJob(extractJobId(status));
	}
	
	private static String[] buildLpCommand(String printer, String gaspar, String jobTitle, String filePath, PrintDocumentRequest request, List<String> extraOptions) {
		List<String> command = new LinkedList<String>();
		command.add("lp");
		command.add("-d");command.add(printer);
		if(gaspar != null) {
			command.add("-U");command.add(gaspar);
		}
		command.add("-o");command.add("fit-to-page");
		if(request.isSetPageSelection()) {
			command.add("-o");command.add("page-ranges=" + request.getPageSelection().getPageFrom() + "-" + request.getPageSelection().getPageTo());
		}

		if(request.isSetDoubleSided()) {
			switch (request.getDoubleSided()) {
			case LONG_EDGE:
				command.add("-o");command.add("sides=two-sided-long-edge");			
				break;
			case SHORT_EDGE:
				command.add("-o");command.add("sides=two-sided-short-edge");			
				break;
			}
		}
		if(request.isSetMultiPageConfig()) {
			int nup = request.getMultiPageConfig().getNbPagesPerSheet().getValue();
			String layout = decodeLayout(request.getMultiPageConfig().getLayout());
			command.add("-o");command.add("number-up=" + nup);
			command.add("-o");command.add("number-up-layout=" + layout);
		}
		if(request.isSetOrientation()) {
			switch (request.getOrientation()) {
			case PORTRAIT:
			case REVERSE_PORTRAIT:
				command.add("-o");command.add("media=Custom.8.27x11.69in");
				break;
			case LANDSCAPE:
			case REVERSE_LANDSCAPE:
				command.add("-o");command.add("media=Custom.11.69x8.27in");
				break;
			}
		}
		if(request.isSetMultipleCopies()) {
			command.add("-n");command.add("" + request.getMultipleCopies().getNumberOfCopies());
			if(request.getMultipleCopies().isCollate()) {
				command.add("-o");command.add("Collate=True");
			}
		}
		if(request.isSetColorConfig()) {
			if(request.getColorConfig() == CloudPrintColorConfig.BLACK_WHITE) {
				command.add("-o");command.add("JCLColorCorrection=BlackWhite");
				//command.add("-o");command.add("blackplot");
			}
		}
		if(extraOptions != null) {
			command.addAll(extraOptions);
		}
		command.add("-t");command.add(jobTitle);
		command.add(filePath);
		return command.toArray(new String[command.size()]);
	}
	
	/** 
		convert 
			LEFT_TO_RIGHT_TOP_TO_BOTTOM
			TOP_TO_BOTTOM_LEFT_TO_RIGHT
			BOTTOM_TO_TOP_LEFT_TO_RIGHT
			BOTTOM_TO_TOP_RIGHT_TO_LEFT
			LEFT_TO_RIGHT_BOTTOM_TO_TOP
			RIGHT_TO_LEFT_BOTTOM_TO_TOP
			RIGHT_TO_LEFT_TOP_TO_BOTTOM
			TOP_TO_BOTTOM_RIGHT_TO_LEFT
		to
			lrtb
			tblr
			btlr
			btrl
			lrbt
			rlbt
			rltb
			tbrl
	 */
	private static String decodeLayout(CloudPrintMultiPageLayout layout) {
		return layout.name().toLowerCase(Locale.US).replace("to_", "").replaceAll("([a-z])[a-z]+_([a-z])[a-z]+_([a-z])[a-z]+_([a-z])[a-z]+", "$1$2$3$4");
	}

	public static String getFilenameFromContentDispositionWithoutAccents(String contentDisposition) {
	    for (String cd : contentDisposition.split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            filename =  filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	            return org.pocketcampus.platform.shared.utils.StringUtils.removeAccents(filename);
	        }
	    }
	    return null;
	}

	private static Map<String, String> getPdfInfo(String pathToPdf) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"pdfinfo", pathToPdf});
		String output = IOUtils.toString(proc.getInputStream(), "UTF-8");
		Map<String, String> info = new HashMap<String, String>();
		for(String line : output.split("\\n")) {
			String [] splitted = line.split("[:]", 2);
			if(splitted.length < 2)
				continue;
			info.put(splitted[0].trim(), splitted[1].trim());
		}
		return info;
	}
	
	private static boolean checkLpOutput(String lpOutput) {
		// e.g., request id is Cups-PDF-3396 (1 file(s))
		return lpOutput.contains("request id is");
	}
	
	private static String extractJobId(String lpOutput) {
		// e.g., request id is Cups-PDF-3396 (1 file(s))
		return StringUtils.substringBetween(lpOutput, "request id is ", " (");
	}
	
	private static boolean waitForJob(String jobId) throws InterruptedException, IOException {
		int wait = 0;
		while(true) {
			Thread.sleep(500);
			wait += 500;
			Process proc = Runtime.getRuntime().exec(new String[]{"lpstat", "-o"});
			String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
			if(!status.contains(jobId))
				return true;
			if(wait > PRINT_PREVIEW_TIMEOUT)
				return false;
		}
	}
	
	private static boolean checkPrinterEnabled(String printerName) throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{"lpstat", "-p", printerName});
		String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
		return status.contains("enabled");
	}



	
	
	private static final long OLD_FILE_CLEAN_PERIOD = 1l; // in minutes
	private static final long STUCK_JOB_CHECK_PERIOD = 1l; // in minutes (if a job lasts for more than x minutes, it is considered stuck)
	
	
	private final Runnable oldFileCleaner = new Runnable() {
		public synchronized void run() {
			String cupsPdfOutDir = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR");
			String cloudPrintDumpDir = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY");
			try {
				// delete all MultiPart* files in /tmp/ that are more than 60 minutes old; these files are created by Jetty to temporarily store uploaded files
				Runtime.getRuntime().exec(new String[]{"find", "/tmp/", "-maxdepth", "1", "-name", "MultiPart*", "-cmin", "+60", "-delete"}).waitFor();
				// delete all PDFs in /tmp/CloudPrintPDF/ that are more than 60 minutes old; these are generated by cups-pdf for print preview
				Runtime.getRuntime().exec(new String[]{"find", cupsPdfOutDir, "-type", "f", "-cmin", "+60", "-delete"}).waitFor();
				
				// delete all files in cloudprint_files/ that are more than 60 minutes old
				Runtime.getRuntime().exec(new String[]{"find", cloudPrintDumpDir, "-type", "f", "-cmin", "+60", "-delete"}).waitFor();
				// delete all empty directories in cloudprint_files/
				Runtime.getRuntime().exec(new String[]{"find", cloudPrintDumpDir, "-mindepth", "1", "-type", "d", "-empty", "-delete"}).waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	private final Runnable stuckJobDetector = new Runnable() {
		private Map<String, String> lastPrinterStatus = generateInitialStatusMap();
		public synchronized void run() {
			try {
				for(Entry<String, String> e : lastPrinterStatus.entrySet()) {
					Process proc = Runtime.getRuntime().exec(new String[]{"lpstat", "-p", e.getKey()});
					String status = IOUtils.toString(proc.getInputStream(), "UTF-8");
					if(!status.contains("idle") && status.equals(e.getValue())) { // stuck!
						System.out.println("PRINTER IS STUCK!");
						System.out.println(e.getValue());
						// parse job id
						int jobId = Integer.parseInt(StringUtils.substringBetween(status, "-", "."));
						System.out.println("attempting to cancel job " + jobId);
						Runtime.getRuntime().exec(new String[]{"cancel", jobId + ""});
					}
					e.setValue(status);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private Map<String, String> generateInitialStatusMap() {
			Map<String, String> statuses = new HashMap<String, String>();
			statuses.put("mainPrinter", "");
			statuses.put("Cups-PDF", "");
			return statuses;
		}
	};
	private long lastCleaned = getCurrentTimeDivision(OLD_FILE_CLEAN_PERIOD);
	private long lastChecked = getCurrentTimeDivision(STUCK_JOB_CHECK_PERIOD);
	private void cleanupIfNeeded() {
		synchronized(this) {
			long currCleaningSlot = getCurrentTimeDivision(OLD_FILE_CLEAN_PERIOD);
			if(currCleaningSlot != lastCleaned) {
				lastCleaned = currCleaningSlot;
				new Thread(oldFileCleaner).start();
			}
		}
		synchronized(this) {
			long currCheckingSlot = getCurrentTimeDivision(STUCK_JOB_CHECK_PERIOD);
			if(currCheckingSlot != lastChecked) {
				lastChecked = currCheckingSlot;
				new Thread(stuckJobDetector).start();
			}
		}
	}
	private long getCurrentTimeDivision(long periodInMinutes) {
		return System.currentTimeMillis() / 1000l / 60l / periodInMinutes;
	}

}
