package org.pocketcampus.plugin.food.android;

import static org.pocketcampus.platform.android.utils.DialogUtils.showMultiChoiceDialog;
import static org.pocketcampus.platform.android.utils.DialogUtils.showMultiChoiceDialogSbN;
import static org.pocketcampus.platform.android.utils.DialogUtils.showSingleChoiceDialog;
import static org.pocketcampus.platform.android.utils.MapUtils.subMap;
import static org.pocketcampus.platform.android.utils.SetUtils.difference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuated;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.platform.android.ui.adapter.MultiListAdapter;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.platform.android.utils.DialogUtils.SingleChoiceHandler;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.food.R;
import org.pocketcampus.plugin.food.android.FoodController.AMeal;
import org.pocketcampus.plugin.food.android.FoodController.AResto;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.MealTime;
import org.pocketcampus.plugin.food.shared.MealType;
import org.pocketcampus.plugin.food.shared.PriceTarget;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * The Main View of the Food plugin, first displayed when accessing Food.
 * 
 * Displays menus by restaurants, preferences, suggestions and ratings
 * 
 * @author Amer <amer@accandme.com>
 * 
 */
public class FoodMainView extends PluginView implements IFoodView {

	public static final String MAP_KEY_MEAL_OBJ = "MAP_KEY_MEAL_OBJ";

	private FoodController mController;
	private FoodModel mModel;

	private boolean displayingList;

	private Set<Long> restosInRS = new HashSet<Long>();
	private Set<MealType> typesInRS = new HashSet<MealType>();

	Map<MealType, List<AMeal>> mealsByTypes;

	Set<Long> filteredRestos = new HashSet<Long>();
	Set<MealType> filteredTypes = new HashSet<MealType>();

	Long foodDay;
	MealTime foodTime = MealTime.LUNCH;

	ListView mList;
	ScrollStateSaver scrollState;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FoodController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (FoodController) controller;
		mModel = (FoodModel) controller.getModel();

		foodDay = getToday();

		setActionBarTitle(getString(R.string.food_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {

		mController.refreshFood(this, foodDay, foodTime, false);

	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the credentials. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null) {
			scrollState.restore(mList);
		}
	}

	@Override
	protected String screenName() {
		return "/food";
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList)
			scrollState = new ScrollStateSaver(mList);
	}

	private Long computeFoodDay(int year, int month, int day) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-M-d'T'HH:mm:ss'Z'", Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
			String d = year + "-" + month + "-" + day;
			return df.parse(d + "T12:00:00Z").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private long getToday() {
		Calendar c = Calendar.getInstance();
		return computeFoodDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public void foodUpdated() {

		setContentView(R.layout.food_main);
		mList = (ListView) findViewById(R.id.food_main_list);
		displayingList = false;
		StandardLayout sl = new StandardLayout(this);
		setContentView(sl);

		// System.out.println("foodUpdated # of meals " +
		// mModel.getMeals().size());

		mealsByTypes = new HashMap<MealType, List<AMeal>>();
		Set<Long> newRestosInRS = new HashSet<Long>();
		Set<MealType> newTypesInRS = new HashSet<MealType>();
		for (AMeal m : mController.getMeals().values()) {
			newRestosInRS.add(m.resto);
			newTypesInRS.addAll(m.types);
			for (MealType t : m.types) {
				if (!mealsByTypes.containsKey(t))
					mealsByTypes.put(t, new LinkedList<AMeal>());
				mealsByTypes.get(t).add(m);
			}
		}

		restosInRS = newRestosInRS;
		typesInRS = newTypesInRS;

		updateDisplay(true);

	}

	private void updateFilters() {
		filteredRestos = difference(restosInRS, mModel.getDislikedRestos());
		filteredTypes = difference(typesInRS, mModel.getDislikedTypes());

	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		final Map<Long, String> subMapRestos = subMap(mController.getRestoNames(), restosInRS);
		final int restoFilterIcon = (difference(restosInRS, filteredRestos).size() == 0 ? R.drawable.pocketcampus_filter
				: R.drawable.pocketcampus_filter_sel);
		if (subMapRestos.size() > 0) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("FilterByRestaurant", null);
					showMultiChoiceDialogSbN(FoodMainView.this, subMapRestos, getString(R.string.food_dialog_resto),
							filteredRestos, new MultiChoiceHandler<Long>() {
								public void saveSelection(Long t, boolean isChecked) {
									if (isChecked)
										mModel.removeDislikedResto(t);
									else
										mModel.addDislikedResto(t);
									updateDisplay(true);
								}
							});
				}

				public int getDrawable() {
					return restoFilterIcon;
				}

				@Override
				public String getDescription() {
					return getString(R.string.food_dialog_resto);
				}

			});
		}
		final Map<MealType, String> subMapTypes = subMap(mController.getTypeNames(), typesInRS);
		final int typeFilterIcon = (difference(typesInRS, filteredTypes).size() == 0 ? R.drawable.pocketcampus_tags
				: R.drawable.pocketcampus_tags_sel);
		if (subMapTypes.size() > 0) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("FilterByIngredient", null);
					showMultiChoiceDialog(FoodMainView.this, subMapTypes, getString(R.string.food_dialog_ingredients),
							filteredTypes, new MultiChoiceHandler<MealType>() {
								public void saveSelection(MealType t, boolean isChecked) {
									if (isChecked)
										mModel.removeDislikedType(t);
									else
										mModel.addDislikedType(t);
									updateDisplay(true);
								}
							});
				}

				public int getDrawable() {
					return typeFilterIcon;
				}

				@Override
				public String getDescription() {
					return getString(R.string.food_dialog_ingredients);
				}
			});
		}

		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				foodTime = (foodTime == MealTime.DINNER ? MealTime.LUNCH : MealTime.DINNER);
				trackEvent((foodTime == MealTime.DINNER ? "ViewDinner" : "ViewLunch"), null);
				mController.refreshFood(FoodMainView.this, foodDay, foodTime, false);
			}

			@Override
			public int getDrawable() {
				return foodTime == MealTime.DINNER ? R.drawable.food_menu_lunch : R.drawable.food_menu_dinner;
			}

			@Override
			public String getDescription() {
				return getString(foodTime == MealTime.DINNER ? R.string.food_menu_view_lunch_menus
						: R.string.food_menu_view_evening_menus);
			}
		});

		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date(foodDay));
				DatePickerDialog dpd = new DatePickerDialog(FoodMainView.this, new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						if (!view.isShown())
							return; // bug in Jely Bean:
									// http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
						foodDay = computeFoodDay(year, monthOfYear + 1, dayOfMonth);
						trackEvent("ViewDay", "" + (foodDay - getToday()) / 1000 / 3600 / 24);
						mController.refreshFood(FoodMainView.this, foodDay, foodTime, false);
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dpd.show();
			}

			@Override
			public int getDrawable() {
				return R.drawable.sdk_pick_date;
			}

			@Override
			public String getDescription() {
				return getString(R.string.food_menu_view_other_day);
			}
		});
	}

	private void updateDisplay(boolean saveScroll) {

		updateFilters();
		updateActionBar();

		if (saveScroll && displayingList)
			scrollState = new ScrollStateSaver(mList);

		Set<AMeal> dislikedMeals = new HashSet<AMeal>();
		for (MealType typ : mModel.getDislikedTypes()) {
			List<AMeal> typeMeals = mealsByTypes.get(typ);
			if (typeMeals == null) // if tag becomes empty (shorter period
									// selected)
				continue; // then skip it
			dislikedMeals.addAll(typeMeals);
		}

		LongSparseArray<List<AMeal>> mealsByResto = new LongSparseArray<List<AMeal>>();

		for (AMeal m : difference(mController.getMeals().values(), dislikedMeals)) {
			if (mealsByResto.get(m.resto) == null)
				mealsByResto.put(m.resto, new LinkedList<AMeal>());
			mealsByResto.get(m.resto).add(m);
		}

		// SeparatedListAdapter adapter = new SeparatedListAdapter(this,
		// R.layout.food_list_header);
		MultiListAdapter adapter = new MultiListAdapter();
		List<AResto> restoList = new ArrayList<AResto>(mController.getRestos().values());
		Collections.sort(restoList, getRestoComp4sort());
		for (AResto r : restoList) {
			if (!filteredRestos.contains(r.id))
				continue;
			List<AMeal> categEvents = mealsByResto.get(r.id);
			if (categEvents == null) // if category becomes empty (filtering by
										// tags)
				continue; // then skip it

			Preparated<AResto> pH = new Preparated<AResto>(Arrays.asList(new AResto[] { r }), new Preparator<AResto>() {
				public int[] resources() {
					return new int[] { R.id.food_list_header_title, R.id.food_list_header_satisfaction,
							R.id.food_list_header_map };
				}

				public Object content(int res, final AResto e) {
					switch (res) {
					case R.id.food_list_header_title:
						return e.name;
					case R.id.food_list_header_satisfaction:
						return e.satisfaction;
					case R.id.food_list_header_map:
						return e.location != null ? new Actuated(getString(R.string.food_button_seemap_inline),
								new Actuator() {
									public void triggered() {
										showOnMap(e);
									}
								}) : null;
					default:
						return null;
					}
				}

				public void finalize(Map<String, Object> map, AResto item) {
					map.put(LazyAdapter.NOT_SELECTABLE, "1");
				}
			});
			adapter.addSection(new LazyAdapter(this, pH.getMap(), R.layout.food_list_header_row, pH.getKeys(), pH
					.getResources()));

			Collections.sort(categEvents, getMealComp4sort());
			Preparated<AMeal> p = new Preparated<AMeal>(categEvents, new Preparator<AMeal>() {
				public int[] resources() {
					return new int[] { R.id.food_title, R.id.food_description, R.id.food_thumbnail, R.id.food_price,
							R.id.food_meal_satisfaction, R.id.food_meal_vote };
				}

				public Object content(int res, final AMeal e) {
					switch (res) {
					case R.id.food_title:
						return e.name;
					case R.id.food_description:
						return e.desc;
					case R.id.food_thumbnail:
						return mController.getMealTypePicUrls().get(e.types.get(0));
					case R.id.food_price:
						Object price = getMealPrice(e.prices, null, "%s<br>CHF", null);
						return new Actuated(price, new Actuator() {
							public void triggered() {
								promptUserStatus(e.prices);
							}
						});
					case R.id.food_meal_satisfaction:
						return e.satisfaction;
					case R.id.food_meal_vote:
						return new Actuated(getString(R.string.food_button_vote_inline), new Actuator() {
							public void triggered() {
								voteFor(e);
								// Toast.makeText(getApplicationContext(),
								// "You are trying to vote -- I know",
								// Toast.LENGTH_SHORT).show();
							}
						});
					default:
						return null;
					}
				}

				public void finalize(Map<String, Object> map, AMeal item) {
					map.put(MAP_KEY_MEAL_OBJ, item);
				}
			});
			adapter.addSection(new LazyAdapter(this, p.getMap(), R.layout.food_list_row, p.getKeys(), p.getResources()));

		}

		if (mController.getMeals().size() == 0) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			DateFormat dateFormat = new SimpleDateFormat("EEE dd", getResources().getConfiguration().locale);
			String t = (foodTime == MealTime.DINNER ? getString(R.string.food_string_no_evening_menus)
					: getString(R.string.food_string_no_lunch_menus));
			String d = (dateFormat.format(new Date(foodDay)));
			sl.setText(String.format(t, d));
			setContentView(sl);
		} else {
			if (!displayingList) {
				setContentView(R.layout.food_main);
				mList = (ListView) findViewById(R.id.food_main_list);
				displayingList = true;
			}
			// TextView headerTitle = (TextView)
			// findViewById(R.id.food_header_title);
			// TextView headerDate = (TextView)
			// findViewById(R.id.food_header_date);
			// DateFormat dateFormat =
			// android.text.format.DateFormat.getDateFormat(this);
			DateFormat dateFormat = new SimpleDateFormat("EEE dd", getResources().getConfiguration().locale);
			String t = (foodTime == MealTime.DINNER ? getString(R.string.food_title_evening_menus)
					: getString(R.string.food_title_lunch_menus));
			String d = (dateFormat.format(new Date(foodDay)));
			setActionBarTitle(String.format(t, d));

			// headerDate.setText("");
			mList.setAdapter(adapter);
			// mList.setCacheColorHint(Color.TRANSPARENT);
			// mList.setFastScrollEnabled(true);
			// mList.setScrollingCacheEnabled(false);
			// mList.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
			// mList.setDivider(null);
			// mList.setDividerHeight(0);

			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if (o instanceof Map<?, ?>) {
						Object obj = ((Map<?, ?>) o).get(MAP_KEY_MEAL_OBJ);
						if (obj != null && obj instanceof AMeal) {
							mealMenu((AMeal) obj);
						}
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});

			if (scrollState != null)
				scrollState.restore(mList);

		}
	}

	private void sendToGoogleTranslate(AMeal m) {
		trackEvent("SendToGoogleTranslate", "" + m.id);
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_TEXT, m.getSummary());
			i.setComponent(new ComponentName("com.google.android.apps.translate",
					"com.google.android.apps.translate.TranslateActivity"));
			startActivity(i);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), getString(R.string.food_toast_nogoogletranslate),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showOnMap(AResto r) {
		trackEvent("ViewRestaurantOnMap", r.name);
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse("pocketcampus://map.plugin.pocketcampus.org/search"));
			i.putExtra("MapElement", r.location);
			startActivity(i);
		} catch (Exception e) {
			// Should never happen
			Toast.makeText(getApplicationContext(), "The Map plugin is not installed??", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("UseSparseArrays")
	private void promptUserStatus(Map<PriceTarget, Double> prices) {
		trackEvent("PromptUserStatus", null);
		String priceTagFormat = "<br><i>(%s CHF)</i>";
		Map<Integer, CharSequence> priceTargets = new HashMap<Integer, CharSequence>();
		if (mController.getServerDetectedPriceTarget() != null) {
			String priceTag = getMealPrice(prices, mController.getServerDetectedPriceTarget(), priceTagFormat, "");
			priceTargets.put(0, Html.fromHtml(getString(R.string.food_pricetag_auto) + priceTag));
		}
		for (PriceTarget t : PriceTarget.values()) {
			if (t == PriceTarget.ALL)
				continue;
			String priceTag = getMealPrice(prices, t, priceTagFormat, "");
			priceTargets.put(t.getValue(), Html.fromHtml(mController.translateEnum(t.name()) + priceTag));
		}
		int selected = 0;
		if (mModel.getUserStatus() != null)
			selected = mModel.getUserStatus().getValue();
		showSingleChoiceDialog(this, priceTargets, getString(R.string.food_dialog_prices), selected,
				new SingleChoiceHandler<Integer>() {
					public void saveSelection(Integer t) {
						if (t == 0) {
							mModel.setUserStatus(null);
						} else {
							mModel.setUserStatus(PriceTarget.findByValue(t));
						}
						mController.refreshFood(FoodMainView.this, foodDay, foodTime, false);
					}
				});
	}

	private String getMealPrice(Map<PriceTarget, Double> prices, PriceTarget priceTarget, String format,
			String retValIfNoPrice) {
		if (priceTarget == null) {
			// first use server detection
			priceTarget = mController.getServerDetectedPriceTarget();
			// then override with user selection
			if (mModel.getUserStatus() != null)
				priceTarget = mModel.getUserStatus();
		}
		Double price = prices.get(priceTarget != null ? priceTarget : PriceTarget.VISITOR);
		if (price == null)
			price = prices.get(PriceTarget.ALL);
		if (price == null)
			return retValIfNoPrice;
		String priceTag = String.format(Locale.US, "%1$.2f", price);
		return String.format(format, priceTag);
	}

	private void voteFor(final AMeal e) {
		trackEvent("RateMeal", "" + e.id);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// v.setOnClickListener()
		final View bodyV = inflater.inflate(R.layout.food_vote_view, new LinearLayout(this));
		((TextView) bodyV.findViewById(R.id.food_dialog_h1)).setText(mController.getRestos().get(e.resto).name);
		((TextView) bodyV.findViewById(R.id.food_dialog_h2)).setText(e.name);
		final LinearLayout im1 = (LinearLayout) bodyV.findViewById(R.id.food_smiley_sad);
		final LinearLayout im2 = (LinearLayout) bodyV.findViewById(R.id.food_smiley_soso);
		final LinearLayout im3 = (LinearLayout) bodyV.findViewById(R.id.food_smiley_happy);
		im1.setClickable(true);
		im2.setClickable(true);
		im3.setClickable(true);
		im1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "Click!!",
				// Toast.LENGTH_SHORT).show();
				bodyV.setTag((Double) 0.0);
				Resources res = getResources();
				im1.setBackgroundColor(res.getColor(R.color.epfl_corrected_red));
				im2.setBackgroundColor(res.getColor(R.color.transparent));
				im3.setBackgroundColor(res.getColor(R.color.transparent));
				bodyV.invalidate();
			}
		});
		im2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "Click!!",
				// Toast.LENGTH_SHORT).show();
				bodyV.setTag((Double) 0.5);
				Resources res = getResources();
				im1.setBackgroundColor(res.getColor(R.color.transparent));
				im2.setBackgroundColor(res.getColor(R.color.epfl_corrected_red));
				im3.setBackgroundColor(res.getColor(R.color.transparent));
				bodyV.invalidate();
			}
		});
		im3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "Click!!",
				// Toast.LENGTH_SHORT).show();
				bodyV.setTag((Double) 1.0);
				Resources res = getResources();
				im1.setBackgroundColor(res.getColor(R.color.transparent));
				im2.setBackgroundColor(res.getColor(R.color.transparent));
				im3.setBackgroundColor(res.getColor(R.color.epfl_corrected_red));
				bodyV.invalidate();
			}
		});
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.food_dialog_vote))
				.setInverseBackgroundForced(true)
				.setPositiveButton(getString(R.string.food_button_send), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Object o = bodyV.getTag();
						if (o != null && o instanceof Double) {
							// Toast.makeText(getApplicationContext(),
							// "Voting " + o,
							// Toast.LENGTH_SHORT).show();
							mController.sendVoteReq(FoodMainView.this, e.id, (Double) o);
						} else {
							Toast.makeText(getApplicationContext(), getString(R.string.food_toast_vote_noselection),
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setView(bodyV).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void mealMenu(final AMeal m) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(m.name)
				.setAdapter(
						new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new String[] {
								getString(R.string.food_button_vote), getString(R.string.food_button_googletranslate),
								getString(R.string.food_button_copytext), getString(R.string.food_button_showprices) }),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								switch (arg1) {
								case 0:
									voteFor(m);
									break;
								case 1:
									sendToGoogleTranslate(m);
									break;
								case 2:
									android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
									clipboard.setText(m.getSummary());
									Toast.makeText(getApplicationContext(), getString(R.string.food_toast_copied),
											Toast.LENGTH_SHORT).show();
									trackEvent("CopyToClipboard", "" + m.id);
									break;
								case 3:
									promptUserStatus(m.prices);
									break;
								default:
									Toast.makeText(getApplicationContext(), arg1, Toast.LENGTH_SHORT).show();
									break;
								}

							}

						}).setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

	}

	public static Comparator<AMeal> getMealComp4sort() {
		return new Comparator<AMeal>() {
			public int compare(AMeal lhs, AMeal rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		};
	}

	public static Comparator<AResto> getRestoComp4sort() {
		return new Comparator<AResto>() {
			public int compare(AResto lhs, AResto rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		};
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT)
				.show();
		mController.refreshFood(this, foodDay, foodTime, true);
	}

	@Override
	public void foodServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_upstream_server_down));
	}

	@Override
	public void voteCastFinished(SubmitStatus status) {
		switch (status) {
		case ALREADY_VOTED:
			Toast.makeText(getApplicationContext(), getString(R.string.food_toast_alreadyvoted), Toast.LENGTH_SHORT)
					.show();
			break;
		case TOO_EARLY:
			Toast.makeText(getApplicationContext(), getString(R.string.food_toast_tooearly), Toast.LENGTH_SHORT).show();
			break;
		case MEAL_IN_DISTANT_PAST:
			Toast.makeText(getApplicationContext(), getString(R.string.food_toast_toolate), Toast.LENGTH_SHORT).show();
			break;
		case VALID:
			Toast.makeText(getApplicationContext(), getString(R.string.food_toast_thanksforvote), Toast.LENGTH_SHORT)
					.show();
			mController.refreshFood(this, foodDay, foodTime, false);
			break;
		default:
			break;
		}

	}

}
