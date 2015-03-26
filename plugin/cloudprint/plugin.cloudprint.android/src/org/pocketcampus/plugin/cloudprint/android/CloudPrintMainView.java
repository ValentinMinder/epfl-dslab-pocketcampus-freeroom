package org.pocketcampus.plugin.cloudprint.android;

import java.io.IOException;

import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
import org.pocketcampus.plugin.cloudprint.R;
import org.pocketcampus.plugin.cloudprint.android.CloudPrintController.CloudPrintImageLoader;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintColorConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintDoubleSidedConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageConfig;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultiPageLayout;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintMultipleCopies;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintNbPagesPerSheet;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintOrientation;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintPageRange;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.Action;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * CloudPrintMainView - Main view that shows CloudPrint courses.
 * 
 * This is the main view in the CloudPrint Plugin. It checks if the user is
 * logged in, if not it pings the Authentication Plugin. It uploads a file to
 * print and prints it
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CloudPrintMainView extends PluginView implements ICloudPrintView {

	public static final String EXTRA_JOB_ID = "JOB_ID";
	public static final String EXTRA_FILE_NAME = "FILE_NAME";

	private CloudPrintController mController;
	private CloudPrintModel mModel;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CloudPrintController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (CloudPrintController) controller;
		mModel = (CloudPrintModel) controller.getModel();

		setActionBarTitle(getString(R.string.cloudprint_plugin_title));
		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				trackEvent("Info", null);
				DialogUtils.alert(CloudPrintMainView.this, getString(R.string.cloudprint_plugin_title),
						getString(R.string.cloudprint_dialog_help_string));
			}

			@Override
			public int getDrawable() {
				return R.drawable.sdk_info_white;
			}

			@Override
			public String getDescription() {
				return getString(R.string.cloudprint_info);
			}
		});

	}

	@Override
	protected void handleIntent(Intent aIntent) {

		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		// String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) /* && type != null */) {
			// if (type.startsWith("application/pdf")) {
			// }
			Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
			mModel.setFileToPrint(imageUri);

		} else if (intent.hasExtra(EXTRA_JOB_ID)) {
			mModel.setPrintJobId(intent.getLongExtra(EXTRA_JOB_ID, 0));
			mModel.setFileToPrint(Uri.parse("dummy://dummy/"
					+ (intent.hasExtra(EXTRA_FILE_NAME) ? intent.getStringExtra(EXTRA_FILE_NAME) : ("job " + intent
							.getLongExtra(EXTRA_JOB_ID, 0)))));

		} else {
			finish();
			// Handle other intents, such as being started from the home screen
		}

		if (CloudPrintController.sessionExists(this)) { // I think this is no
														// longer necessary,
														// since the auth plugin
														// doesnt blindly redo
														// auth (well, this
														// saves the one call
														// that the auth plugin
														// does to check if the
														// session is valid)
			updateDisplay();
		} else {
			showLoading();
			CloudPrintController.pingAuthPlugin(this);
		}

	}

	@Override
	protected String screenName() {
		return "/cloudprint";
	}

	/**
	 * Displays the waiting screen.
	 */
	private void showLoading() {
		setContentView(R.layout.cloudprint_loading);
	}

	/**
	 * Displays the authentication form.
	 */
	private void updateDisplay() {

		if (mModel.getPrintJobId() != null) {
			setContentView(R.layout.cloudprint_print);

			// Spinner sel = (Spinner)
			// findViewById(R.id.cloudprint_select_pageselection);
			// ArrayAdapter<String> spinnerAdapter = new
			// ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
			// android.R.id.text1);

			updateSpinnerColorConfig();
			updateSpinnerDoubleSided();
			updateSpinnerMultiPage();
			updateSpinnerMultipleCopies();
			updateSpinnerOrientation();
			updateSpinnerPageSelection();

			TextView tv = (TextView) findViewById(R.id.cloudprint_preview_print);
			tv.setText(Html.fromHtml(getString(R.string.cloudprint_dialog_text_print, mModel.getFileName())));

			Button b = (Button) findViewById(R.id.cloudprint_preview_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					trackEvent("PrintPreview", null);
					showLoading();
					mController.printFileJob(CloudPrintMainView.this, mModel.getPrintJobId(), true);
				}
			});
			Button b1 = (Button) findViewById(R.id.cloudprint_print_button);
			b1.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					trackEvent("Print", null);
					showLoading();
					mController.printFileJob(CloudPrintMainView.this, mModel.getPrintJobId(), false);
				}
			});

		} else if (mModel.getFileToPrint() != null) {
			setContentView(R.layout.cloudprint_upload);

			TextView tv = (TextView) findViewById(R.id.cloudprint_preview_upload);
			tv.setText(Html.fromHtml(getString(R.string.cloudprint_dialog_text_upload, mModel.getFileName())));

			Button b = (Button) findViewById(R.id.cloudprint_upload_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					trackEvent("Upload", null);
					showLoading();
					try {
						mController.uploadFileToPrint(CloudPrintMainView.this, mModel.getFileToUpload());
					} catch (IOException e) {
						e.printStackTrace();
						localFileErrorHappened();
					}
				}
			});

		} else {
			finish();
		}

	}

	private void showPrintPreview() {
		setContentView(R.layout.cloudprint_preview);

		TextView tv = (TextView) findViewById(R.id.cloudprint_pagenumber);
		tv.setText(Html.fromHtml(getString(R.string.cloudprint_dialog_text_pagenumber, mModel.getCurrPage() + 1,
				mModel.getPageCount())));

		final ImageView iv = (ImageView) findViewById(R.id.cloudprint_printpreview_image);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.cloudprint_printpreview_loading);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.drawable.ic_popup_sync)
				.showImageForEmptyUri(android.R.drawable.ic_menu_gallery)
				.showImageOnFail(android.R.drawable.ic_menu_report_image).cacheInMemory(false).cacheOnDisk(false)
				.extraForDownloader(((GlobalContext) getApplicationContext()).getPcSessionId()).build();
		CloudPrintImageLoader.getInstance().displayImage(mController.getPageThumbnailUrl(), iv, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				iv.setVisibility(View.GONE);
				pb.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				pb.setVisibility(View.GONE);
				iv.setVisibility(View.VISIBLE);
				iv.setImageResource(android.R.drawable.ic_menu_report_image);
				iv.setPadding(0, 0, 0, 0);
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				pb.setVisibility(View.GONE);
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(arg2);
				iv.setPadding(1, 1, 1, 1);
				iv.setBackgroundColor(Color.GRAY);
				
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				pb.setVisibility(View.GONE);
				iv.setVisibility(View.VISIBLE);
				iv.setImageResource(R.drawable.sdk_transparent);
				iv.setPadding(0, 0, 0, 0);
				
			}
		});

		Button p = (Button) findViewById(R.id.cloudprint_previouspage_button);

		if (mModel.getCurrPage() == 0) {
			p.setVisibility(View.INVISIBLE);
		} else {
			p.setVisibility(View.VISIBLE);
			p.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					trackEvent("PreviousPage", null);
					mModel.setCurrPage(Math.max(mModel.getCurrPage() - 1, 0));
					showPrintPreview();
				}
			});
		}

		Button n = (Button) findViewById(R.id.cloudprint_nextpage_button);
		if (mModel.getCurrPage() == mModel.getPageCount() - 1) {
			n.setVisibility(View.INVISIBLE);
		} else {
			n.setVisibility(View.VISIBLE);

			n.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					trackEvent("NextPage", null);
					mModel.setCurrPage(Math.min(mModel.getCurrPage() + 1, mModel.getPageCount() - 1));
					showPrintPreview();
				}
			});
		}

		Button b = (Button) findViewById(R.id.cloudprint_back_button);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				trackEvent("BackFromPreview", null);
				updateDisplay();
			}
		});
		Button b1 = (Button) findViewById(R.id.cloudprint_print_button);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				trackEvent("Print", null);
				showLoading();
				mController.printFileJob(CloudPrintMainView.this, mModel.getPrintJobId(), false);
			}
		});

	}

	private void updateSpinnerPageSelection() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_pageselection);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintPageRange r : mModel.getPageRangeList()) {
			adapter.add(mController.pageRangeToDisplayString(r));
		}
		adapter.add(getString(R.string.cloudprint_string_page_selection_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelPageRangeList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getPageRangeList().size()) {
					mModel.setSelPageRangeList(arg2);
				} else {
					final String[] arr = CloudPrintController.generateArray(1, 101, 1);
					showSelector(getString(R.string.cloudprint_string_from_page), arr,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final int from = Integer.parseInt(arr[arg1]);
									final String[] arr2 = CloudPrintController.generateArray(from, from + 100, 1);
									showSelector(getString(R.string.cloudprint_string_to_page), arr2,
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0, int arg1) {
													final int to = Integer.parseInt(arr2[arg1]);
													mModel.addPageRangeList(new CloudPrintPageRange(from, to));
													mModel.setSelPageRangeList(arg2);
													updateDisplay();
												}
											}, getCancelListener());
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void updateSpinnerMultipleCopies() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_copies);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintMultipleCopies r : mModel.getMultipleCopiesList()) {
			adapter.add(mController.multipleCopiesToDisplayString(r));
		}
		adapter.add(getString(R.string.cloudprint_string_more_copies_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelMultipleCopiesList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getMultipleCopiesList().size()) {
					mModel.setSelMultipleCopiesList(arg2);
				} else {
					final String[] arr = CloudPrintController.generateArray(2, 102, 1);
					showSelector(getString(R.string.cloudprint_string_copies), arr,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final int copies = Integer.parseInt(arr[arg1]);
									showSelector(getString(R.string.cloudprint_string_collate), new String[] {
											getString(R.string.cloudprint_string_collate),
											getString(R.string.cloudprint_string_do_not_collate) },
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0, int arg1) {
													final boolean collate = (arg1 == 0);
													mModel.addMultipleCopiesList(new CloudPrintMultipleCopies(copies,
															collate));
													mModel.setSelMultipleCopiesList(arg2);
													updateDisplay();
												}
											}, getCancelListener());
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void updateSpinnerMultiPage() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_multipage);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintMultiPageConfig r : mModel.getMultiPageList()) {

			adapter.add(mController.multiPageToDisplayString(r));
		}
		adapter.add(getString(R.string.cloudprint_string_multiple_pages_per_sheet_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelMultiPageList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getMultiPageList().size()) {
					mModel.setSelMultiPageList(arg2);
				} else {

					showSelector(getString(R.string.cloudprint_string_pages_per_sheet), nbPagesPerSheetToArray(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final CloudPrintNbPagesPerSheet nbPagesPerSheet = CloudPrintNbPagesPerSheet
											.values()[arg1];
									showSelector(getString(R.string.cloudprint_string_page_layout),
											multiPageLayoutToArray(), new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0, int arg1) {
													final CloudPrintMultiPageLayout layout = CloudPrintMultiPageLayout
															.values()[arg1];
													mModel.addMultiPageList(new CloudPrintMultiPageConfig(
															nbPagesPerSheet, layout));
													mModel.setSelMultiPageList(arg2);
													updateDisplay();
												}
											}, getCancelListener());
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void updateSpinnerOrientation() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_orientation);

		// s.setVisibility(View.GONE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintOrientation r : mModel.getOrientationList()) {

			adapter.add(mController.orientationToDisplayString(r));
		}
		// adapter.add(getString(R.string.cloudprint_string_other_orientation_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelOrientationList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getOrientationList().size()) {
					mModel.setSelOrientationList(arg2);
				} else {

					showSelector(getString(R.string.cloudprint_string_orientation), orientationToArray(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final CloudPrintOrientation ori = CloudPrintOrientation.values()[arg1];
									mModel.addOrientationList(ori);
									mModel.setSelOrientationList(arg2);
									updateDisplay();
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void updateSpinnerColorConfig() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_color);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintColorConfig r : mModel.getColorConfigList()) {

			adapter.add(mController.colorConfigToDisplayString(r));
		}
		// adapter.add(getString(R.string.cloudprint_string_other_color_configuration_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelColorConfigList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getColorConfigList().size()) {
					mModel.setSelColorConfigList(arg2);
				} else {

					showSelector(getString(R.string.cloudprint_string_color_configuration), colorConfigToArray(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final CloudPrintColorConfig color = CloudPrintColorConfig.values()[arg1];
									mModel.addColorConfigList(color);
									mModel.setSelColorConfigList(arg2);
									updateDisplay();
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void updateSpinnerDoubleSided() {

		final Spinner s = (Spinner) findViewById(R.id.cloudprint_select_doublesided);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CloudPrintMainView.this,
				android.R.layout.simple_spinner_item, android.R.id.text1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		for (CloudPrintDoubleSidedConfig r : mModel.getDoubleSidedList()) {

			adapter.add(mController.doubleSidedToDisplayString(r));
		}
		adapter.add(getString(R.string.cloudprint_string_double_sided_lps));
		adapter.notifyDataSetChanged();

		s.setSelection(mModel.getSelDoubleSidedList());

		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < mModel.getDoubleSidedList().size()) {
					mModel.setSelDoubleSidedList(arg2);
				} else {

					showSelector(getString(R.string.cloudprint_string_double_sided), doubleSidedToArray(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									final CloudPrintDoubleSidedConfig ds = CloudPrintDoubleSidedConfig.values()[arg1];
									mModel.addDoubleSidedList(ds);
									mModel.setSelDoubleSidedList(arg2);
									updateDisplay();
								}
							}, getCancelListener());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private OnCancelListener getCancelListener() {
		return new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				updateDisplay();
			}
		};
	}

	private void showSelector(String title, String[] options, DialogInterface.OnClickListener clickListener,
			OnCancelListener cancelListener) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(title)
				.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options), clickListener)
				.setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();

	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_connection_error_happened),
				Toast.LENGTH_SHORT).show();
		updateDisplay();
	}

	@Override
	public void printServerError() {
		Toast.makeText(getApplicationContext(), getString(R.string.cloudprint_string_print_server_error),
				Toast.LENGTH_SHORT).show();
		updateDisplay();

	}

	public void localFileErrorHappened() {
		Toast.makeText(getApplicationContext(), getString(R.string.cloudprint_string_print_server_error),
				Toast.LENGTH_SHORT).show();
		updateDisplay();
	}

	@Override
	public void uploadComplete(long jobId) {
		mModel.setPrintJobId(jobId);
		mModel.fileUploaded();
		updateDisplay();
	}

	@Override
	public void printedSuccessfully() {
		mModel.setPrintJobId(null);
		mModel.setFileToPrint(null);
		Toast.makeText(getApplicationContext(), getString(R.string.cloudprint_string_document_sent_to_printer),
				Toast.LENGTH_SHORT).show();
		finish();

	}

	@Override
	public void printPreviewReady(int pageCount) {
		mModel.setPageCount(pageCount);
		mModel.setCurrPage(0);
		showPrintPreview();
	}

	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_authentication_failed), Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}

	@Override
	public void notLoggedIn() {
		CloudPrintController.pingAuthPlugin(this);
		showLoading();

	}

	@Override
	public void authenticationFinished() {
		updateDisplay();

	}

	private String[] orientationToArray() {
		CloudPrintOrientation[] vals = CloudPrintOrientation.values();
		String strings[] = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			strings[i] = mController.getLocalizedStringByName("cloudprint_enum_orientation_", vals[i].name());
		}
		return strings;
	}

	private String[] colorConfigToArray() {
		CloudPrintColorConfig[] vals = CloudPrintColorConfig.values();
		String strings[] = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			strings[i] = mController.getLocalizedStringByName("cloudprint_enum_color_", vals[i].name());
		}
		return strings;
	}

	private String[] doubleSidedToArray() {
		CloudPrintDoubleSidedConfig[] vals = CloudPrintDoubleSidedConfig.values();
		String strings[] = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			strings[i] = mController.getLocalizedStringByName("cloudprint_enum_double_sided_", vals[i].name());
		}
		return strings;
	}

	private String[] multiPageLayoutToArray() {
		CloudPrintMultiPageLayout[] vals = CloudPrintMultiPageLayout.values();
		String strings[] = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			strings[i] = String.format("%s (%s)",
					mController.getLocalizedStringByName("cloudprint_enum_layout_", vals[i].name()),
					mController.getLocalizedStringByName("cloudprint_enum_layout_short_", vals[i].name()));
		}
		return strings;
	}

	private String[] nbPagesPerSheetToArray() {
		CloudPrintNbPagesPerSheet[] vals = CloudPrintNbPagesPerSheet.values();
		String strings[] = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			strings[i] = "" + vals[i].getValue();
		}
		return strings;
	}

}
