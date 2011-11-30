package org.pocketcampus.server.plugin.takeout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javapns.Push;
import javapns.notification.PushNotificationPayload;

import org.apache.thrift.TException;
import org.json.JSONException;
import org.pocketcampus.platform.sdk.shared.common.Choice;
import org.pocketcampus.platform.sdk.shared.common.Currency;
import org.pocketcampus.platform.sdk.shared.common.MultiChoiceOption;
import org.pocketcampus.platform.sdk.shared.common.Rating;
import org.pocketcampus.platform.sdk.shared.common.SingleChoiceOption;
import org.pocketcampus.platform.sdk.shared.restaurant.ClientOrderReceipt;
import org.pocketcampus.platform.sdk.shared.restaurant.MenuCategory;
import org.pocketcampus.platform.sdk.shared.restaurant.MenuItem;
import org.pocketcampus.platform.sdk.shared.restaurant.MenuSubCategory;
import org.pocketcampus.platform.sdk.shared.restaurant.OrderPlacedByClient;
import org.pocketcampus.platform.sdk.shared.restaurant.PaymentMethod;
import org.pocketcampus.platform.sdk.shared.restaurant.Restaurant;
import org.pocketcampus.platform.sdk.shared.restaurant.RestaurantCategory;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutOrderService;
import org.pocketcampus.server.plugin.takeoutreceiver.Cook;
import org.pocketcampus.server.plugin.takeoutreceiver.DeviceTokenManager;
import org.pocketcampus.server.plugin.takeoutreceiver.IdManager;

public class TakeoutOrderServiceImpl implements TakeoutOrderService.Iface {
	private Restaurant restaurant;
	private Set<String> deviceIDs;
	private static final String PATH_TO_IDS = "./registeredDevices";
	private int restaurantVersion = 0;

	public TakeoutOrderServiceImpl() {
		try {
			getRestaurant(null);
		} catch (TException e2) {
			e2.printStackTrace();
		}
		deviceIDs = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(PATH_TO_IDS)));
			String devices = br.readLine();
			for (String device : devices.split(",\\s+")) {
				deviceIDs.add(device);
			}
		} catch (Exception e) {
			System.out.println("Cannot read the stupid file");
			e.printStackTrace();
		}
		new Thread() {
			public void run() {
				System.out
						.println("Starting seding alive messages in 5 minutes");
				while (true) {
					PushNotificationPayload paylod = new PushNotificationPayload();
					try {
						paylod.addCustomDictionary("restaurantStateVersion", ""
								+ System.currentTimeMillis());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					for (String deviceID : deviceIDs) {
						System.out.println("Sending an I'm alive message to "
								+ deviceID);

						try {
							System.out.println(Push.payload(paylod,
									"./certificate/APNCertificate.p12",
									"mywaiter", false, deviceID));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					try {
						Thread.sleep(1 * 60 * 1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

				}
			}
		}.start();
	}

	@Override
	public Restaurant getRestaurant(String deviceToken) throws TException {
		System.out.println("getRestaurant");
		if (deviceToken != null) {
			deviceToken = DeviceTokenManager.formatDeviceToken(deviceToken);
			boolean added = deviceIDs.add(deviceToken);
			if (added) {
				try {
					PrintWriter pw = new PrintWriter(new FileOutputStream(
							PATH_TO_IDS));
					String string = deviceIDs.toString();
					string = string.substring(1, string.length() - 1);
					pw.println(string);
					pw.close();
				} catch (Exception e) {
					System.out.println("Cannot SAVE the device IDs");
					e.printStackTrace();
				}
			}
		}
		if (restaurant == null) {
			restaurant = new Restaurant();

			// PIZZA INGREDIENTS
			List<Choice> ingredientsChoices = new ArrayList<Choice>();
			MultiChoiceOption ingredients = new MultiChoiceOption();
			ingredients.setMultiChoiceId(IdManager.getID(ingredients));
			ingredients.setChoices(ingredientsChoices);
			ingredients.setDefaultChoices(ingredientsChoices);
			ingredients.setName("Ingrédients supplémentaires");

			Choice oignons = new Choice(0, "Oignons");
			oignons.setChoiceId(IdManager.getID(oignons));
			ingredients.addToChoices(oignons);

			Choice champignons = new Choice(0, "Champignons");
			champignons.setChoiceId(IdManager.getID(champignons));
			ingredients.addToChoices(champignons);

			Choice olives = new Choice(0, "Olives");
			olives.setChoiceId(IdManager.getID(olives));
			ingredients.addToChoices(olives);

			Choice ananas = new Choice(0, "Ananas");
			ananas.setChoiceId(IdManager.getID(ananas));
			ingredients.addToChoices(ananas);

			Choice mais = new Choice(0, "Mais");
			mais.setChoiceId(IdManager.getID(mais));
			ingredients.addToChoices(mais);

			Choice poivrons = new Choice(0, "Poivrons");
			poivrons.setChoiceId(IdManager.getID(poivrons));
			ingredients.addToChoices(poivrons);

			// Condiments
			List<Choice> condimenChoices = new ArrayList<Choice>();
			MultiChoiceOption condiments = new MultiChoiceOption();
			condiments.setMultiChoiceId(IdManager.getID(condiments));
			condiments.setChoices(condimenChoices);
			condiments.setDefaultChoices(condimenChoices);
			condiments.setName("Epices et plus");

			Choice huilleOlives = new Choice(0, "Huille Olives");
			huilleOlives.setChoiceId(IdManager.getID(huilleOlives));
			condiments.addToChoices(huilleOlives);

			Choice oregano = new Choice(0, "Oregano");
			oregano.setChoiceId(IdManager.getID(oregano));
			condiments.addToChoices(oregano);

			Choice epicesThai = new Choice(0, "Epices Thai");
			epicesThai.setChoiceId(IdManager.getID(epicesThai));
			condiments.addToChoices(epicesThai);

			List<MultiChoiceOption> options = Arrays.asList(ingredients,
					condiments);
			// PIZZAS
			List<MenuItem> pizzas = new ArrayList<MenuItem>();

			// Roulotte
			MenuItem pizzaRoulotte = new MenuItem();
			pizzaRoulotte.setName("Pizza \"Roulotte\"");
			pizzaRoulotte.setItemId(IdManager.getID(pizzaRoulotte));
			pizzaRoulotte
					.setItemDescription("Jambon, salami, sudjuk, oignons, poivrons, champignons, olives.");
			pizzaRoulotte.setPrice(10.0);
			pizzaRoulotte.setStars(Rating.UNKNOWN);
			pizzaRoulotte.setPricingUnit("pizza");
			pizzaRoulotte.setMultiChoiceOptions(options); // add
															// to
															// all
			pizzas.add(pizzaRoulotte);

			// Tomate Mozzarella
			MenuItem pizzaMozarella = new MenuItem();
			pizzaMozarella.setName("Pizza Tomate&Mozzarella");
			pizzaMozarella.setItemId(IdManager.getID(pizzaMozarella));
			pizzaMozarella.setItemDescription("Tomate et Mozzarella");
			pizzaMozarella.setPrice(8.0);
			pizzaMozarella.setStars(Rating.UNKNOWN);
			pizzaMozarella.setPricingUnit("pizza");
			pizzaMozarella.setMultiChoiceOptions(options);
			pizzas.add(pizzaMozarella);

			// Végétarienne
			MenuItem pizzaVege = new MenuItem();
			pizzaVege.setName("Pizza Végétarienne");
			pizzaVege.setItemId(IdManager.getID(pizzaVege));
			pizzaVege
					.setItemDescription("Oignons, poivrons, champignons, olives.");
			pizzaVege.setPrice(8.5);
			pizzaVege.setStars(Rating.UNKNOWN);
			pizzaVege.setMultiChoiceOptions(options);
			pizzaVege.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			pizzaVege.setPricingUnit("pizza");
			pizzas.add(pizzaVege);

			// Jambon
			MenuItem pizzaJambon = new MenuItem();
			pizzaJambon.setName("Pizza Jambon");
			pizzaJambon.setItemId(IdManager.getID(pizzaJambon));
			pizzaJambon
					.setItemDescription("Jambon, champignons, olives, oignons.");
			pizzaJambon.setPrice(9.0);
			pizzaJambon.setStars(Rating.UNKNOWN);
			pizzaJambon.setMultiChoiceOptions(options);
			pizzaJambon.setPricingUnit("pizza");
			pizzas.add(pizzaJambon);

			// Salami
			MenuItem pizzaSalami = new MenuItem();
			pizzaSalami.setName("Pizza Salami");
			pizzaSalami.setItemId(IdManager.getID(pizzaSalami));
			pizzaSalami
					.setItemDescription("Salami, champignons, olives, oignons.");
			pizzaSalami.setPrice(9.0);
			pizzaSalami.setStars(Rating.UNKNOWN);
			pizzaSalami.setMultiChoiceOptions(options);
			pizzaSalami.setPricingUnit("pizza");
			pizzas.add(pizzaSalami);

			// Sudjuk
			MenuItem pizzaSudjuk = new MenuItem();
			pizzaSudjuk.setName("Pizza Sudjuk");
			pizzaSudjuk.setItemId(IdManager.getID(pizzaSudjuk));
			pizzaSudjuk
					.setItemDescription("Sudjuk (chorizo de boeuf), poivrons, oignons, olives.");
			pizzaSudjuk.setPrice(9.0);
			pizzaSudjuk.setStars(Rating.UNKNOWN);
			pizzaSudjuk.setMultiChoiceOptions(options);
			pizzaSudjuk.setPricingUnit("pizza");
			pizzas.add(pizzaSudjuk);

			// Thon
			MenuItem pizzaThon = new MenuItem();
			pizzaThon.setName("Pizza Thon");
			pizzaThon.setItemId(IdManager.getID(pizzaThon));
			pizzaThon.setItemDescription("Thon, oignons, olives, champignons.");
			pizzaThon.setPrice(9.0);
			pizzaThon.setStars(Rating.UNKNOWN);
			pizzaThon.setMultiChoiceOptions(options);
			pizzaThon.setPricingUnit("pizza");
			pizzas.add(pizzaThon);

			// PIZZA SUB CATEGORY
			MenuSubCategory menuSubCategoryPizzas = new MenuSubCategory();
			menuSubCategoryPizzas.setName("Pizzas");
			menuSubCategoryPizzas.setSubCategoryId(IdManager
					.getID(menuSubCategoryPizzas));
			menuSubCategoryPizzas.setItems(pizzas);

			// DRINKS
			List<MenuItem> cannedDrinks = new ArrayList<MenuItem>();
			List<MenuItem> bottledDrinks = new ArrayList<MenuItem>();

			// Coca
			MenuItem drinkCoca = new MenuItem();
			drinkCoca.setName("Coca Cola");
			drinkCoca.setItemId(IdManager.getID(drinkCoca));
			drinkCoca.setPrice(3.0);
			drinkCoca.setStars(Rating.UNKNOWN);
			drinkCoca.setMultiChoiceOptions(new Vector<MultiChoiceOption>());
			drinkCoca.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			drinkCoca.setPricingUnit("canette");
			bottledDrinks.add(drinkCoca);
			cannedDrinks.add(drinkCoca);

			MenuItem drinkNestea = new MenuItem();
			drinkNestea.setName("Nestea");
			drinkNestea.setItemId(IdManager.getID(drinkNestea));
			drinkNestea.setPrice(3.5);
			drinkNestea.setStars(Rating.UNKNOWN);
			drinkNestea.setMultiChoiceOptions(new Vector<MultiChoiceOption>());
			drinkNestea
					.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			drinkNestea.setPricingUnit("canette");
			bottledDrinks.add(drinkNestea);
			cannedDrinks.add(drinkCoca);

			// CANS SUB CATEGORY
			MenuSubCategory menuSubCategoryCannedDrinks = new MenuSubCategory();
			menuSubCategoryCannedDrinks.setName("Canettes");
			menuSubCategoryCannedDrinks
					.setSubCategoryDescription("Boissons en canettes.");
			menuSubCategoryCannedDrinks.setSubCategoryId(IdManager
					.getID(menuSubCategoryCannedDrinks));
			menuSubCategoryCannedDrinks.setItems(cannedDrinks);

			// BOTTLES SUB CATEGORY
			MenuSubCategory menuSubCategoryBottledDrinks = new MenuSubCategory();
			menuSubCategoryBottledDrinks.setName("Bouteilles");
			menuSubCategoryBottledDrinks
					.setSubCategoryDescription("Boissons en bouteilles.");
			menuSubCategoryBottledDrinks.setSubCategoryId(IdManager
					.getID(menuSubCategoryBottledDrinks));
			menuSubCategoryBottledDrinks.setItems(bottledDrinks);

			// MENU CATEGORIES
			// Pizzas
			MenuCategory menuCategoryPizza = new MenuCategory();
			menuCategoryPizza.setName("Pizzas");
			menuCategoryPizza.setCategoryDescription("Delicious fresh pizza");
			List<MenuSubCategory> subCategories = Arrays
					.asList(menuSubCategoryPizzas);
			menuCategoryPizza.setSubCategories(subCategories);
			menuCategoryPizza.setCategoryId(IdManager.getID(menuCategoryPizza));

			// Drinks
			MenuCategory menuCategoryDrinks = new MenuCategory();
			menuCategoryDrinks.setName("Boissons");
			subCategories = Arrays.asList(menuSubCategoryBottledDrinks);
			menuCategoryDrinks.setSubCategories(subCategories);
			menuCategoryDrinks.setCategoryId(IdManager
					.getID(menuCategoryDrinks));

			// PAYMENT METHODS
			Set<PaymentMethod> acceptedPaymentMethods = new HashSet<PaymentMethod>();
			acceptedPaymentMethods.add(PaymentMethod.PAY_BY_CASH);

			restaurant = new Restaurant();
			restaurant.setName("iRoulotte");
			restaurant.setMenuCategories(Arrays.asList(menuCategoryPizza,
					menuCategoryDrinks));
			restaurant.setAcceptedPaymentMethods(acceptedPaymentMethods);
			restaurant.setRestaurantDescription("Pizzeria");
			restaurant.setCategory(RestaurantCategory.FAST_FOOD);
			restaurant.setCurrency(new Currency("Francs Suisse", "CHF"));
			restaurant.setAddress("Diagonale EPFL");
			restaurant.setVersion(1);
			restaurant.setStars(Rating.FIVE);
			restaurant.setPayBeforeOrderIsPlacedIsSet(false);
			restaurant.setRestaurantId(IdManager.getID(restaurant));
			restaurantVersion = restaurant.hashCode();
			restaurant.setVersion(restaurantVersion);
		}
		System.out.println("Returning the restaurant  " + restaurant);		
		return restaurant;
	}

	@Override
	public boolean versionMatches(long version) throws TException {
		boolean result = version == restaurantVersion;
		System.out.println("Server version=" + restaurantVersion + " matches "
				+ version + " ? = " + result);
		return result;
	}

	@Override
	public ClientOrderReceipt placeOrder(OrderPlacedByClient order)
			throws TException {
		// Database.addOrder(order);

		System.out.println("Received: " + order);

		long newOrderId = IdManager.getID(order);
		order.orderId = newOrderId;
		String phoneToken = DeviceTokenManager.formatDeviceToken(order.phoneId);
		DeviceTokenManager.saveDeviceTokenForOrderId(phoneToken, newOrderId);
		dispatchOrderToCook(newOrderId);
		return new ClientOrderReceipt(order.userId, (byte) 0, newOrderId);
	}

	private void dispatchOrderToCook(final long orderId) {

		new Thread() {
			public void run() {
				// Order result = new Order();
				// result.setDate(clientOrder.getTimestamp());
				// result.setId(clientOrder.getOrderId());
				// result.setUserId(clientOrder.getUserId());
				// result.setChosenItems(clientOrder.getChosenItems());
				if (!Cook.dispatchToCook(orderId)) {
					System.out.println("COULD NOT DISPATCH");
				}
			}
		}.start();
	}
}
