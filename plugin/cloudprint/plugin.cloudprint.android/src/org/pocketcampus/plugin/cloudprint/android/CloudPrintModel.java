package org.pocketcampus.plugin.cloudprint.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintModel;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintColorConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintDoubleSidedConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageLayout;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultipleCopies;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintNbPagesPerSheet;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintOrientation;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintPageRange;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;

/**
 * CloudPrintModel
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CloudPrintModel extends PluginModel implements ICloudPrintModel {

	/**
	 * Some constants.
	 */
	private static final String CLOUDPRINT_STORAGE_NAME = "CLOUDPRINT_MULTIPAGE";

	private static final String CLOUDPRINT_COPIES = "CLOUDPRINT_COPIES";
	private static final String CLOUDPRINT_PAGESELECTION = "CLOUDPRINT_PAGESELECTION";
	private static final String CLOUDPRINT_COLOR = "CLOUDPRINT_COLOR";
	private static final String CLOUDPRINT_DOUBLESIDE = "CLOUDPRINT_DOUBLESIDE";
	private static final String CLOUDPRINT_ORIENTATION = "CLOUDPRINT_ORIENTATION";
	private static final String CLOUDPRINT_MULTIPAGE = "CLOUDPRINT_MULTIPAGE";

	private static final String CLOUDPRINT_COPIES_SEL = "CLOUDPRINT_COPIES_SEL";
	private static final String CLOUDPRINT_PAGESELECTION_SEL = "CLOUDPRINT_PAGESELECTION_SEL";
	private static final String CLOUDPRINT_COLOR_SEL = "CLOUDPRINT_COLOR_SEL";
	private static final String CLOUDPRINT_DOUBLESIDE_SEL = "CLOUDPRINT_DOUBLESIDE_SEL";
	private static final String CLOUDPRINT_ORIENTATION_SEL = "CLOUDPRINT_ORIENTATION_SEL";
	private static final String CLOUDPRINT_MULTIPAGE_SEL = "CLOUDPRINT_MULTIPAGE_SEL";

	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	private List<CloudPrintColorConfig> colorConfigList;
	private List<CloudPrintMultipleCopies> multipleCopiesList;
	private List<CloudPrintOrientation> orientationList;
	private List<CloudPrintDoubleSidedConfig> doubleSidedList;
	private List<CloudPrintMultiPageConfig> multiPageList;
	private List<CloudPrintPageRange> pageRangeList;

	private int selColorConfigList;
	private int selMultipleCopiesList;
	private int selOrientationList;
	private int selDoubleSidedList;
	private int selMultiPageList;
	private int selPageRangeList;

	/**
	 * Reference to the Views that need to be notified when the stored data
	 * changes.
	 */
	ICloudPrintView mListeners = (ICloudPrintView) getListeners();

	private Uri fileToPrint;
	private Long printjobId;
	private int pageCount;
	private int currPage;

	private String fileName;
	private Context context;

	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate the SharedPreferences
	 * object in order to use persistent storage.
	 * 
	 * @param context
	 *            is the Application Context.
	 */
	public CloudPrintModel(Context context) {
		iStorage = context.getSharedPreferences(CLOUDPRINT_STORAGE_NAME, Context.MODE_PRIVATE);

		multipleCopiesList = stringToMultipleCopiesList(iStorage.getString(CLOUDPRINT_COPIES, ""));
		pageRangeList = stringToPageRangeList(iStorage.getString(CLOUDPRINT_PAGESELECTION, ""));
		colorConfigList = stringToColorConfigList(iStorage.getString(CLOUDPRINT_COLOR, ""));
		doubleSidedList = stringToDoubleSidedList(iStorage.getString(CLOUDPRINT_DOUBLESIDE, ""));
		orientationList = stringToOrientationList(iStorage.getString(CLOUDPRINT_ORIENTATION, ""));
		multiPageList = stringToMultiPageList(iStorage.getString(CLOUDPRINT_MULTIPAGE, ""));

		if (multipleCopiesList.size() == 0)
			addMultipleCopiesList(null);
		if (pageRangeList.size() == 0)
			addPageRangeList(null);
		if (colorConfigList.size() == 0) {
			addColorConfigList(CloudPrintColorConfig.COLOR);
			addColorConfigList(CloudPrintColorConfig.BLACK_WHITE);
		}
		if (doubleSidedList.size() == 0)
			addDoubleSidedList(null);
		if (orientationList.size() == 0) {
			addOrientationList(CloudPrintOrientation.PORTRAIT);
			addOrientationList(CloudPrintOrientation.LANDSCAPE);
		}
		if (multiPageList.size() == 0)
			addMultiPageList(null);

		selMultipleCopiesList = iStorage.getInt(CLOUDPRINT_COPIES_SEL, 0);
		selPageRangeList = iStorage.getInt(CLOUDPRINT_PAGESELECTION_SEL, 0);
		selColorConfigList = iStorage.getInt(CLOUDPRINT_COLOR_SEL, 0);
		selDoubleSidedList = iStorage.getInt(CLOUDPRINT_DOUBLESIDE_SEL, 0);
		selOrientationList = iStorage.getInt(CLOUDPRINT_ORIENTATION_SEL, 0);
		selMultiPageList = iStorage.getInt(CLOUDPRINT_MULTIPAGE_SEL, 0);

		this.context = context;
	}

	public void setSelColorConfigList(int a) {
		selColorConfigList = a;
		iStorage.edit().putInt(CLOUDPRINT_COLOR_SEL, selColorConfigList).commit();
	}

	public void setSelMultipleCopiesList(int a) {
		selMultipleCopiesList = a;
		iStorage.edit().putInt(CLOUDPRINT_COPIES_SEL, selMultipleCopiesList).commit();
	}

	public void setSelOrientationList(int a) {
		selOrientationList = a;
		iStorage.edit().putInt(CLOUDPRINT_ORIENTATION_SEL, selOrientationList).commit();
	}

	public void setSelDoubleSidedList(int a) {
		selDoubleSidedList = a;
		iStorage.edit().putInt(CLOUDPRINT_DOUBLESIDE_SEL, selDoubleSidedList).commit();
	}

	public void setSelMultiPageList(int a) {
		selMultiPageList = a;
		iStorage.edit().putInt(CLOUDPRINT_MULTIPAGE_SEL, selMultiPageList).commit();
	}

	public void setSelPageRangeList(int a) {
		selPageRangeList = a;
		iStorage.edit().putInt(CLOUDPRINT_PAGESELECTION_SEL, selPageRangeList).commit();
	}

	public void addColorConfigList(CloudPrintColorConfig a) {
		colorConfigList.add(a);
		iStorage.edit().putString(CLOUDPRINT_COLOR, colorConfigListToString(colorConfigList)).commit();
	}

	public void addMultipleCopiesList(CloudPrintMultipleCopies a) {
		multipleCopiesList.add(a);
		iStorage.edit().putString(CLOUDPRINT_COPIES, multipleCopiesListToString(multipleCopiesList)).commit();
	}

	public void addOrientationList(CloudPrintOrientation a) {
		orientationList.add(a);
		iStorage.edit().putString(CLOUDPRINT_ORIENTATION, orientationListToString(orientationList)).commit();
	}

	public void addDoubleSidedList(CloudPrintDoubleSidedConfig a) {
		doubleSidedList.add(a);
		iStorage.edit().putString(CLOUDPRINT_DOUBLESIDE, doubleSidedListToString(doubleSidedList)).commit();
	}

	public void addMultiPageList(CloudPrintMultiPageConfig a) {
		multiPageList.add(a);
		iStorage.edit().putString(CLOUDPRINT_MULTIPAGE, multiPageListToString(multiPageList)).commit();
	}

	public void addPageRangeList(CloudPrintPageRange a) {
		pageRangeList.add(a);
		iStorage.edit().putString(CLOUDPRINT_PAGESELECTION, pageRangeListToString(pageRangeList)).commit();
	}

	public List<CloudPrintColorConfig> getColorConfigList() {
		return colorConfigList;
	}

	public List<CloudPrintMultipleCopies> getMultipleCopiesList() {
		return multipleCopiesList;
	}

	public List<CloudPrintOrientation> getOrientationList() {
		return orientationList;
	}

	public List<CloudPrintDoubleSidedConfig> getDoubleSidedList() {
		return doubleSidedList;
	}

	public List<CloudPrintMultiPageConfig> getMultiPageList() {
		return multiPageList;
	}

	public List<CloudPrintPageRange> getPageRangeList() {
		return pageRangeList;
	}

	public int getSelColorConfigList() {
		return selColorConfigList;
	}

	public int getSelMultipleCopiesList() {
		return selMultipleCopiesList;
	}

	public int getSelOrientationList() {
		return selOrientationList;
	}

	public int getSelDoubleSidedList() {
		return selDoubleSidedList;
	}

	public int getSelMultiPageList() {
		return selMultiPageList;
	}

	public int getSelPageRangeList() {
		return selPageRangeList;
	}

	public void setFileToPrint(Uri file) {
		if (file == null) {
			fileName = null;
			fileToPrint = null;
			return;
		}
		String uriString = file.toString();

		if (uriString.startsWith("content://")) {
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(file, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} finally {
				cursor.close();
			}
		} else if (uriString.startsWith("file://")) {
			File myFile = new File(uriString);
			fileName = myFile.getName();
		} else if (uriString.startsWith("dummy://")) {
			fileName = file.getLastPathSegment();
		}
		fileToPrint = file;
	}

	public String getFileName() {
		return fileName;
	}
	
	public File getFileToUpload() throws IOException{
		if(fileToPrint.toString().startsWith("file://")){
			return new File(fileToPrint.getPath());
		}
		InputStream fileInputStream = context.getContentResolver().openInputStream(fileToPrint);
		File file = new File(context.getFilesDir(), getFileName());
		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(fileInputStream, out);
		out.close();
		return file;
	}
	
	public void fileUploaded(){
		if(fileToPrint.toString().startsWith("file://")){
		}else{
			new File(getFileName()).delete();
		}
	}

	public Uri getFileToPrint() {
		return fileToPrint;
	}

	public void setPrintJobId(Long id) {
		printjobId = id;
	}

	public Long getPrintJobId() {
		return printjobId;
	}

	public void setPageCount(int val) {
		pageCount = val;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setCurrPage(int val) {
		currPage = val;
	}

	public int getCurrPage() {
		return currPage;
	}

	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICloudPrintView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public ICloudPrintView getListenersToNotify() {
		return mListeners;
	}

	/********
	 * HELPERS
	 */

	private static CloudPrintColorConfig stringToColorConfig(String s) {
		if ("_".equals(s))
			return null;
		return CloudPrintColorConfig.findByValue(Integer.parseInt(s));
	}

	private static String colorConfigToString(CloudPrintColorConfig s) {
		if (s == null)
			return "_";
		return "" + s.getValue();
	}

	private static List<CloudPrintColorConfig> stringToColorConfigList(String s) {
		List<CloudPrintColorConfig> l = new LinkedList<CloudPrintColorConfig>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToColorConfig(ss));
		}
		return l;
	}

	private static String colorConfigListToString(List<CloudPrintColorConfig> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintColorConfig ss : s) {
			l.add(colorConfigToString(ss));
		}
		return TextUtils.join(",", l);
	}

	private static CloudPrintMultipleCopies stringToMultipleCopies(String s) {
		if ("_".equals(s))
			return null;
		String[] ss = s.split("[_]");
		return new CloudPrintMultipleCopies(Integer.parseInt(ss[0]), "1".equals(ss[1]));
	}

	private static String multipleCopiesToString(CloudPrintMultipleCopies s) {
		if (s == null)
			return "_";
		return s.getNumberOfCopies() + "_" + (s.isCollate() ? "1" : "0");
	}

	private static List<CloudPrintMultipleCopies> stringToMultipleCopiesList(String s) {
		List<CloudPrintMultipleCopies> l = new LinkedList<CloudPrintMultipleCopies>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToMultipleCopies(ss));
		}
		return l;
	}

	private static String multipleCopiesListToString(List<CloudPrintMultipleCopies> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintMultipleCopies ss : s) {
			l.add(multipleCopiesToString(ss));
		}
		return TextUtils.join(",", l);
	}

	private static CloudPrintOrientation stringToOrientation(String s) {
		if ("_".equals(s))
			return null;
		return CloudPrintOrientation.findByValue(Integer.parseInt(s));
	}

	private static String orientationToString(CloudPrintOrientation s) {
		if (s == null)
			return "_";
		return "" + s.getValue();
	}

	private static List<CloudPrintOrientation> stringToOrientationList(String s) {
		List<CloudPrintOrientation> l = new LinkedList<CloudPrintOrientation>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToOrientation(ss));
		}
		return l;
	}

	private static String orientationListToString(List<CloudPrintOrientation> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintOrientation ss : s) {
			l.add(orientationToString(ss));
		}
		return TextUtils.join(",", l);
	}

	private static CloudPrintDoubleSidedConfig stringToDoubleSided(String s) {
		if ("_".equals(s))
			return null;
		return CloudPrintDoubleSidedConfig.findByValue(Integer.parseInt(s));
	}

	private static String doubleSidedToString(CloudPrintDoubleSidedConfig s) {
		if (s == null)
			return "_";
		return "" + s.getValue();
	}

	private static List<CloudPrintDoubleSidedConfig> stringToDoubleSidedList(String s) {
		List<CloudPrintDoubleSidedConfig> l = new LinkedList<CloudPrintDoubleSidedConfig>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToDoubleSided(ss));
		}
		return l;
	}

	private static String doubleSidedListToString(List<CloudPrintDoubleSidedConfig> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintDoubleSidedConfig ss : s) {
			l.add(doubleSidedToString(ss));
		}
		return TextUtils.join(",", l);
	}

	private static CloudPrintMultiPageConfig stringToMultiPage(String s) {
		if ("_".equals(s))
			return null;
		String[] ss = s.split("[_]");
		return new CloudPrintMultiPageConfig(CloudPrintNbPagesPerSheet.findByValue(Integer.parseInt(ss[0])),
				CloudPrintMultiPageLayout.findByValue(Integer.parseInt(ss[1])));
	}

	private static String multiPageToString(CloudPrintMultiPageConfig s) {
		if (s == null)
			return "_";
		return s.getNbPagesPerSheet().getValue() + "_" + s.getLayout().getValue();
	}

	private static List<CloudPrintMultiPageConfig> stringToMultiPageList(String s) {
		List<CloudPrintMultiPageConfig> l = new LinkedList<CloudPrintMultiPageConfig>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToMultiPage(ss));
		}
		return l;
	}

	private static String multiPageListToString(List<CloudPrintMultiPageConfig> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintMultiPageConfig ss : s) {
			l.add(multiPageToString(ss));
		}
		return TextUtils.join(",", l);
	}

	private static CloudPrintPageRange stringToPageRange(String s) {
		if ("_".equals(s))
			return null;
		String[] ss = s.split("[_]");
		return new CloudPrintPageRange(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
	}

	private static String pageRangeToString(CloudPrintPageRange s) {
		if (s == null)
			return "_";
		return s.getPageFrom() + "_" + s.getPageTo();
	}

	private static List<CloudPrintPageRange> stringToPageRangeList(String s) {
		List<CloudPrintPageRange> l = new LinkedList<CloudPrintPageRange>();
		for (String ss : StringUtils.split(s, ",")) {
			l.add(stringToPageRange(ss));
		}
		return l;
	}

	private static String pageRangeListToString(List<CloudPrintPageRange> s) {
		List<String> l = new LinkedList<String>();
		for (CloudPrintPageRange ss : s) {
			l.add(pageRangeToString(ss));
		}
		return TextUtils.join(",", l);
	}

}
