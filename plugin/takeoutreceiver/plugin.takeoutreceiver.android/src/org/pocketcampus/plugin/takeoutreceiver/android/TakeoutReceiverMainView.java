package org.pocketcampus.plugin.takeoutreceiver.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.dialog.PopupDialog;
import org.pocketcampus.android.platform.sdk.ui.element.ButtonElement;
import org.pocketcampus.android.platform.sdk.ui.element.LabeledListViewElement;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedItem;
import org.pocketcampus.platform.sdk.shared.restaurant.CookReceivedOrder;
import org.pocketcampus.platform.sdk.shared.restaurant.PendingOrders;
import org.pocketcampus.plugin.takeoutreceiver.android.iface.ITakeoutReceiverMainView;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class TakeoutReceiverMainView extends PluginView implements ITakeoutReceiverMainView {
	private StandardLayout mMainLayout;
	private TakeoutReceiverController mController;
	private TakeoutReceiverModel mModel;
	private LabeledListViewElement mListView;

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (TakeoutReceiverController) controller;
		mModel = (TakeoutReceiverModel) mController.getModel();

		mMainLayout = new StandardLayout(this); 

		setContentView(mMainLayout);

		mMainLayout.setText("Chargement...");

		mController.loadPendingOrders();
	}

	@Override
	public void ordersUpdated() {
		System.out.println("ordersUpdated from view");

		PendingOrders pendingOrders = mModel.getPendingOrders();

		if(pendingOrders == null) {
			System.out.println("Error: pendingOrders null!");
			return;
		}

		List<CookReceivedOrder> orders = pendingOrders.getOrders();

		if(orders==null || orders.size()==0) {
			mMainLayout.setText("Pas de commande en attente.");

		} else {
			mMainLayout.hideText();
			//mController.fireNotification();
		}

		ILabeler<CookReceivedOrder> labeler = new ILabeler<CookReceivedOrder>() {
			@Override
			public String getLabel(CookReceivedOrder obj) {
				String orderDesc = "";

				// summary of ordered item
				for(CookReceivedItem item : obj.orderedItems) {
					orderDesc += item.name + ", ";
				}

				// time since order
				int deltaMinutes = (int) ((new Date().getTime()/1000 - obj.date)/60);
				String timeSinceOrder = "";

				if(deltaMinutes < 0) {
					timeSinceOrder = "<1 minute";
				} else if(deltaMinutes == 1) {
					timeSinceOrder = "1 minute";
				} else {
					timeSinceOrder = deltaMinutes + " minutes";
				}

				timeSinceOrder = "il y a " + timeSinceOrder;
				orderDesc += timeSinceOrder;

				return orderDesc ;
			}
		};


		if(mListView == null) {
			// creates the list
			mListView = new LabeledListViewElement(this, orders, labeler);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
					CookReceivedOrder order = (CookReceivedOrder) adapter.getItemAtPosition(pos);
					displayOrderDetails(order);
				}
			});

			mMainLayout.addView(mListView);
		} else {

			// updates it
			mListView.setAdapter(new LabeledArrayAdapter(this, orders, labeler));
		}
	}

	private void displayOrderDetails(final CookReceivedOrder order) {
		TextView desc = new TextView(this);
		desc.setTextSize((float) 19.0);

		String descString ="";

		if(order.orderedItems!=null && order.orderedItems.size()!=0) {
			for(CookReceivedItem item : order.orderedItems) {
				descString += "<b>- " + item.name + "</b>";

				if(item.singleChoices!=null && item.singleChoices.size()!=0) {

					Iterator<Entry<String, String>> it = item.singleChoices.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pair = it.next();
						descString += "<br>" + pair.getKey() +": " + pair.getValue() + ", ";
					}

					descString = removeLastChars(descString, 2);
				}


				if(item.multipleChoices!=null && item.multipleChoices.size()!=0) {

					for (Map.Entry<String, List<String>> entry : item.multipleChoices.entrySet()) {
						String key = entry.getKey();
						List<String> value = entry.getValue();

						descString += "<br>" + key + ": ";

						for(String option : value) {
							descString += option + ", ";
						}

						descString = removeLastChars(descString, 2);
					}
				}

				if(!item.comments.equals("")) {
					descString += "<br>" + item.comments;
				}

				descString += "<br>";


			}

			descString = removeLastChars(descString, 4);
			descString += "<br><br><b>Total: " + order.getPrice() +  " francs</b>";
			descString += "<br>From " + order.getUserId() +  ".";
		}

		desc.setText(Html.fromHtml(descString));

		List<ButtonElement> buttons = new ArrayList<ButtonElement>();
		buttons.add(new ButtonElement(this, "Fermer"));
		buttons.add(new ButtonElement(this, "C'est prêt!"));

		String title = "Commande n." + Long.toString(order.getOrderId());

		final PopupDialog detailDialog = new PopupDialog(this, title, desc, buttons);

		buttons.get(0).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				detailDialog.close();
			}
		});

		buttons.get(1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mController.setOrderStatus(order.getOrderId());
				detailDialog.close();
			}
		});

		detailDialog.show();
	}

	private String removeLastChars(String descString, int nbChars) {
		if(descString.length() - nbChars > 0) {
			return descString.substring(0, descString.length() - nbChars);
		}

		return "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.takeoutreceiver_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.takeoutreceiver_menu_refresh) {
			mController.loadPendingOrders();
		} else if (item.getItemId() == R.id.takeoutreceiver_menu_add) {
			mController.addBogusOrder();
		}

		return true;
	}

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TakeoutReceiverController.class;
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}

}
