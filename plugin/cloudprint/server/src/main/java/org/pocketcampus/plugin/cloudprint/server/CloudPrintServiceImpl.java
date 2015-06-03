package org.pocketcampus.plugin.cloudprint.server;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.server.BackgroundTasker;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.server.TaskRunner;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.cloudprint.shared.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * CloudPrintServiceImpl
 * 
 * The implementation of the server side of the CloudPrint Plugin.
 * It print on EPFL's pool
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CloudPrintServiceImpl implements CloudPrintService.Iface, RawPlugin, StateChecker, TaskRunner {
	
	private static final int PRINT_PREVIEW_TIMEOUT = 30000;
	
	public CloudPrintServiceImpl() {
	}
	
	@Override
	public void schedule(BackgroundTasker.Scheduler tasker) {
		tasker.addTask(60 * 1000, false, new Runnable() {
			public void run() {
				TempFileCleaner.cleanupIfNeeded();
				try {
					verifyPrintersAndJobsAndTakeAction();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public int checkState() throws IOException {
		int status = checkPrintersAndJobs();
		status = ((status == 0) ? 200 : (status + 500));
		if(!checkStorage())
			status += 5000;
		return status;
	}
	
	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			private static final long serialVersionUID = -6760157045775850293L;
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				TempFileCleaner.cleanupIfNeeded();
				long id = Long.parseLong(req.getParameter("file_id"));
				int page = Integer.parseInt(req.getParameter("page"));
				String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(req);
				if (gaspar == null) {
					resp.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}
				String pdf = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR") + "/" + gaspar + "_" + id + ".pdf";
				String png = PocketCampusServer.CONFIG.getString("CLOUDPRINT_CUPSPDF_OUTDIR") + "/" + gaspar + "_" + System.nanoTime() + ".png";
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
				TempFileCleaner.cleanupIfNeeded();
				String gaspar = AuthenticationServiceImpl.authGetUserGasparFromReq(req);
				if (gaspar == null) {
					resp.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}
				long id = System.currentTimeMillis();
				String filePath = PocketCampusServer.CONFIG.getString("CLOUDPRINT_DUMP_DIRECTORY") + "/" + gaspar + "_" + id;
			    synchronized(TempFileCleaner.oldFileCleaner) { // we don't want the cleaner to delete the freshly created directory before we put a file in it
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
		TempFileCleaner.cleanupIfNeeded();
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
		TempFileCleaner.cleanupIfNeeded();
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
					request.getMultiPageConfig().setLayout(rotate90left(request.getMultiPageConfig().getLayout()));
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
	
	private static CloudPrintMultiPageLayout rotate90left(CloudPrintMultiPageLayout layout) {
		// page is rotated 90 degrees to the left
		switch (layout) {
		case BOTTOM_TO_TOP_LEFT_TO_RIGHT:
			return CloudPrintMultiPageLayout.RIGHT_TO_LEFT_BOTTOM_TO_TOP;
		case BOTTOM_TO_TOP_RIGHT_TO_LEFT:
			return CloudPrintMultiPageLayout.RIGHT_TO_LEFT_TOP_TO_BOTTOM;
		case LEFT_TO_RIGHT_BOTTOM_TO_TOP:
			return CloudPrintMultiPageLayout.BOTTOM_TO_TOP_RIGHT_TO_LEFT;
		case LEFT_TO_RIGHT_TOP_TO_BOTTOM:
			return CloudPrintMultiPageLayout.BOTTOM_TO_TOP_LEFT_TO_RIGHT;
		case RIGHT_TO_LEFT_BOTTOM_TO_TOP:
			return CloudPrintMultiPageLayout.TOP_TO_BOTTOM_RIGHT_TO_LEFT;
		case RIGHT_TO_LEFT_TOP_TO_BOTTOM:
			return CloudPrintMultiPageLayout.TOP_TO_BOTTOM_LEFT_TO_RIGHT;
		case TOP_TO_BOTTOM_LEFT_TO_RIGHT:
			return CloudPrintMultiPageLayout.LEFT_TO_RIGHT_BOTTOM_TO_TOP;
		case TOP_TO_BOTTOM_RIGHT_TO_LEFT:
			return CloudPrintMultiPageLayout.LEFT_TO_RIGHT_TOP_TO_BOTTOM;
		}
		return layout;
	}

	/*
	private static String rotate90left(String layout) {
		StringBuilder sb = new StringBuilder();
		for(char c : layout.toCharArray()) {
			switch (c) {
			case 'b':
				sb.append('r');
				break;
			case 't':
				sb.append('l');
				break;
			case 'l':
				sb.append('b');
				break;
			case 'r':
				sb.append('t');
				break;
			default:
				break;
			}
		}
		return sb.toString();
	}
	*/

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

	///////////////////
	
	private int checkPrintersAndJobs() throws IOException {
		int status = 0, id = 0;
		for(Map.Entry<String, PrintJobChecker> e : jobCheckers.entrySet()) {
			id++;
			if(!PrinterStateChecker.checkPrinterEnabled(e.getKey())) {
				status += id;
			}
			if(e.getValue().checkJobStuck(5l) != null) {
				status += 10 * id;
			}
		}
		return status;
	}
	private void verifyPrintersAndJobsAndTakeAction() throws IOException {
		for(Map.Entry<String, PrintJobChecker> e : jobCheckers.entrySet()) {
			PrinterStateChecker.verifyPrinterAndTakeAction(e.getKey());
			e.getValue().verifyJobAndTakeAction(3l);
		}
	}
	private boolean checkStorage() throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{ "df", "-h" });
		String [] out = IOUtils.toString(proc.getInputStream(), "UTF-8").split("\n");
		int i = -1, j = -1;
		for(String l : out) {
			if(i == -1) {
				String [] cols = l.split("\\s+");
				for(String c : cols) {
					if(c.contains("%")) {
						i = l.indexOf(c);
						j = i + c.length();
						break;
					}
				}
				continue;
			}
			if(i >= 0 && i < l.length() && j >= 0 && j < l.length()) {
				String f = l.substring(i, j);
				if(f.contains("%")) {
					int percent = Integer.parseInt(f.replace('%', ' ').trim());
					if(percent > 90) {
						System.out.println("PARTITION ALMOST FULL: " + l);
						return false;
					}
				}
			}
		}
		return true;
	}
	private final Map<String, PrintJobChecker> jobCheckers = generateInitialJobCheckersMap(); 
	private Map<String, PrintJobChecker> generateInitialJobCheckersMap() {
		Map<String, PrintJobChecker> jobCheckers = new HashMap<String, PrintJobChecker>();
		jobCheckers.put("mainPrinter", new PrintJobChecker("mainPrinter"));
		jobCheckers.put("Cups-PDF", new PrintJobChecker("Cups-PDF"));
		return jobCheckers;
	}
	
}
