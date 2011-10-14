package org.pocketcampus.server.plugin.takeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.thrift.TException;
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

	@Override
	public Restaurant getRestaurant() throws TException {
		System.out.println("getRestaurant");
		
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
			
			
			// PIZZAS
			List<MenuItem> pizzas = new ArrayList<MenuItem>();
			
			// Roulotte
			MenuItem pizzaRoulotte = new MenuItem();
			pizzaRoulotte.setName("Pizza \"Roulotte\"");
			pizzaRoulotte.setItemId(IdManager.getID(pizzaRoulotte));
			pizzaRoulotte.setItemDescription("Jambon, salami, sudjuk, oignons, poivrons, champignons, olives.");
			pizzaRoulotte.setPrice(10.0);
			pizzaRoulotte.setStars(Rating.UNKNOWN);
			pizzaRoulotte.setPricingUnit("pizza");
			pizzaRoulotte.setMultiChoiceOptions(Arrays.asList(ingredients)); // add to all
			pizzas.add(pizzaRoulotte);
			
			// Tomate Mozzarella
			MenuItem pizzaMozarella = new MenuItem();
			pizzaMozarella.setName("Pizza Tomate Mozzarella");
			pizzaMozarella.setItemId(IdManager.getID(pizzaMozarella));
			pizzaMozarella.setItemDescription("Tomate et Mozzarella");
			pizzaMozarella.setPrice(8.0);
			pizzaMozarella.setStars(Rating.UNKNOWN);
			pizzaMozarella.setPricingUnit("pizza");
			pizzaMozarella.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzas.add(pizzaMozarella);
			
			// Végétarienne
			MenuItem pizzaVege = new MenuItem();
			pizzaVege.setName("Pizza Végétarienne");
			pizzaVege.setItemId(IdManager.getID(pizzaVege));
			pizzaVege.setItemDescription("Oignons, poivrons, champignons, olives.");
			pizzaVege.setPrice(8.5);
			pizzaVege.setStars(Rating.UNKNOWN);
			pizzaVege.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaVege.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			pizzaVege.setPricingUnit("pizza");
			pizzas.add(pizzaVege);
			
			// Tomate Mozzarella
			MenuItem pizzaMozz = new MenuItem();
			pizzaMozz.setName("Pizza Tomate Mozzarella");
			pizzaMozz.setItemId(IdManager.getID(pizzaMozz));
			pizzaMozz.setItemDescription("Tomate et Mozzarella");
			pizzaMozz.setPrice(8.0);
			pizzaMozz.setStars(Rating.UNKNOWN);
			pizzaMozz.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaMozz.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			pizzaMozz.setPricingUnit("pizza");
			pizzas.add(pizzaMozz);
			
			// Jambon 
			MenuItem pizzaJambon = new MenuItem();
			pizzaJambon.setName("Pizza Jambon");
			pizzaJambon.setItemId(IdManager.getID(pizzaJambon));
			pizzaJambon.setItemDescription("Jambon, champignons, olives, oignons.");
			pizzaJambon.setPrice(9.0);
			pizzaJambon.setStars(Rating.UNKNOWN);
			pizzaJambon.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaJambon.setPricingUnit("pizza");
			pizzas.add(pizzaJambon);
			
			// Salami
			MenuItem pizzaSalami = new MenuItem();
			pizzaSalami.setName("Pizza Salami");
			pizzaSalami.setItemId(IdManager.getID(pizzaSalami));
			pizzaSalami.setItemDescription("Salami, champignons, olives, oignons.");
			pizzaSalami.setPrice(14.0);
			pizzaSalami.setStars(Rating.UNKNOWN);
			pizzaSalami.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaSalami.setPricingUnit("pizza");
			pizzas.add(pizzaSalami);
			
			// Sudjuk
			MenuItem pizzaSudjuk = new MenuItem();
			pizzaSudjuk.setName("Pizza Sudjuk");
			pizzaSudjuk.setItemId(IdManager.getID(pizzaSudjuk));
			pizzaSudjuk.setItemDescription("Sudjuk (chorizo de boeuf), poivrons, oignons, olives.");
			pizzaSudjuk.setPrice(9.0);
			pizzaSudjuk.setStars(Rating.UNKNOWN);
			pizzaSudjuk.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaSudjuk.setPricingUnit("pizza");
			pizzas.add(pizzaSudjuk);
			
			// Thon
			MenuItem pizzaThon = new MenuItem();
			pizzaThon.setName("Pizza Thon");
			pizzaThon.setItemId(IdManager.getID(pizzaThon));
			pizzaThon.setItemDescription("Thon, oignons, olives, champignons.");
			pizzaThon.setPrice(9.0);
			pizzaThon.setStars(Rating.UNKNOWN);
			pizzaThon.setMultiChoiceOptions(Arrays.asList(ingredients));
			pizzaThon.setPricingUnit("pizza");
			pizzas.add(pizzaThon);
			
			// PIZZA SUB CATEGORY
			MenuSubCategory menuSubCategoryPizzas = new MenuSubCategory();
			menuSubCategoryPizzas.setName("Pizzas");
			menuSubCategoryPizzas.setSubCategoryDescription("Delicious fresh pizzas.");
			menuSubCategoryPizzas.setSubCategoryId(IdManager.getID(menuSubCategoryPizzas));
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
			drinkNestea.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
			drinkNestea.setPricingUnit("canette");
			bottledDrinks.add(drinkNestea);
			cannedDrinks.add(drinkCoca);
			
			
			// CANS SUB CATEGORY
			MenuSubCategory menuSubCategoryCannedDrinks = new MenuSubCategory();
			menuSubCategoryCannedDrinks.setName("Canettes");
			menuSubCategoryCannedDrinks.setSubCategoryDescription("Boissons en canettes.");
			menuSubCategoryCannedDrinks.setSubCategoryId(IdManager.getID(menuSubCategoryCannedDrinks));
			menuSubCategoryCannedDrinks.setItems(cannedDrinks);
			
			// BOTTLES SUB CATEGORY
			MenuSubCategory menuSubCategoryBottledDrinks = new MenuSubCategory();
			menuSubCategoryBottledDrinks.setName("Bouteilles");
			menuSubCategoryBottledDrinks.setSubCategoryDescription("Boissons en bouteilles.");
			menuSubCategoryBottledDrinks.setSubCategoryId(IdManager.getID(menuSubCategoryBottledDrinks));
			menuSubCategoryBottledDrinks.setItems(bottledDrinks);
			
			
			// MENU CATEGORIES
			// Pizzas
			MenuCategory menuCategoryPizza = new MenuCategory();
			menuCategoryPizza.setName("Pizzas");
			List<MenuSubCategory> subCategories = Arrays.asList(menuSubCategoryPizzas);
			menuCategoryPizza.setSubCategories(subCategories);
			menuCategoryPizza.setCategoryId(IdManager.getID(menuCategoryPizza));
			
			// Drinks
			MenuCategory menuCategoryDrinks = new MenuCategory();
			menuCategoryDrinks.setName("Boissons");
			subCategories = Arrays.asList(menuSubCategoryBottledDrinks);
			menuCategoryDrinks.setSubCategories(subCategories);
			menuCategoryDrinks.setCategoryId(IdManager.getID(menuCategoryDrinks));

			// PAYMENT METHODS
			Set<PaymentMethod> acceptedPaymentMethods = new HashSet<PaymentMethod>();
			acceptedPaymentMethods.add(PaymentMethod.PAY_BY_CASH);
			
			restaurant = new Restaurant();
			restaurant.setName("Roulotte");
			restaurant.setMenuCategories(Arrays.asList(menuCategoryPizza, menuCategoryDrinks));
			restaurant.setAcceptedPaymentMethods(acceptedPaymentMethods);
			restaurant.setRestaurantDescription("Pizzeria");
			restaurant.setCategory(RestaurantCategory.FAST_FOOD);
			restaurant.setCurrency(new Currency("Francs Suisse", "CHF"));
			restaurant.setAddress("Diagonale EPFL");
			restaurant.setVersion(0);
			restaurant.setStars(Rating.FIVE);
			restaurant.setPayBeforeOrderIsPlacedIsSet(false);
			restaurant.setRestaurantId(IdManager.getID(restaurant));
			
//			restaurant = new Restaurant();
//			MenuItem item = new MenuItem();
//			item.setName("J.P.Chenet");
//			item.setItemId(IdManager.getID(item));
//			item.setItemDescription("Chardonnay");
//			item.setPrice(3.8);
//			item.setStars(Rating.FIVE);
//			item.setMultiChoiceOptions(new Vector<MultiChoiceOption>());
//			item.setSingleChoiceOptions(new Vector<SingleChoiceOption>());
//			item.setPricingUnit("1dl");
//			List<MenuItem> wine = Arrays.asList(item);
//			MenuSubCategory menuSubCategoryWine = new MenuSubCategory();
//			menuSubCategoryWine.setName("Wine");
//			menuSubCategoryWine
//					.setSubCategoryDescription("A selection of wines");
//			menuSubCategoryWine.setSubCategoryId(IdManager
//					.getID(menuSubCategoryWine));
//			menuSubCategoryWine.setItems(wine);
//
//			item = new MenuItem();
//			item.setName("Martini");
//			item.setItemId(IdManager.getID(item));
//			item.setItemDescription("Yummy!");
//			item.setPrice(5);
//			item.setStars(Rating.FIVE);
//			SingleChoiceOption options = new SingleChoiceOption();
//			options.setName("Type of Martini");
//			options.setSingleChoiceId(IdManager.getID(options));
//			Choice dry = new Choice(0, "Dry");
//			dry.setChoiceId(IdManager.getID(dry));
//			Choice rosso = new Choice(0, "Rosso");
//			rosso.setChoiceId(IdManager.getID(rosso));
//			Choice bianco = new Choice(0, "Bianco");
//			bianco.setChoiceId(IdManager.getID(bianco));
//			options.setChoices(Arrays.asList(dry, bianco, rosso));
//			options.setDefaultChoice(dry);
//			item.setSingleChoiceOptions(Arrays.asList(options));
//
//			Choice ice = new Choice(0, "Ice");
//			ice.setChoiceId(IdManager.getID(ice));
//			Choice olive = new Choice(0, "Olive");
//			olive.setChoiceId(IdManager.getID(olive));
//			Choice lemon = new Choice(0, "Lemon");
//			lemon.setChoiceId(IdManager.getID(lemon));
//
//			List<Choice> ingredientsChoices = Arrays.asList(ice, lemon, olive);
//			MultiChoiceOption ingredients = new MultiChoiceOption();
//			ingredients.setMultiChoiceId(IdManager.getID(ingredients));
//			ingredients.setChoices(ingredientsChoices);
//			ingredients.setDefaultChoices(ingredientsChoices);
//			ingredients.setName("What to put inside the Martini");
//			item.setMultiChoiceOptions(Arrays.asList(ingredients));
//
//			item.setPricingUnit("4dl");
//
//			List<MenuItem> vermuth = Arrays.asList(item);
//			MenuSubCategory menuSubCategoryVermuth = new MenuSubCategory();
//			menuSubCategoryVermuth.setName("Vermuth");
//			menuSubCategoryVermuth.setSubCategoryId(0);
//			menuSubCategoryVermuth.setItems(vermuth);
//			menuSubCategoryVermuth.setSubCategoryId(IdManager
//					.getID(menuSubCategoryVermuth));
//
//			Set<PaymentMethod> acceptedPaymentMethods = new HashSet<PaymentMethod>();
//			acceptedPaymentMethods.add(PaymentMethod.PAY_BY_CASH);
//
//			List<MenuSubCategory> subCategories = Arrays.asList(
//					menuSubCategoryVermuth, menuSubCategoryWine);
//
//			MenuCategory menuCategory = new MenuCategory();
//			menuCategory.setName("Alcoholic Drinks");
//			menuCategory.setSubCategories(subCategories);
//			menuCategory.setCategoryId(IdManager.getID(menuCategory));
//
//			restaurant = new Restaurant();
//			restaurant.setName("Silviu's");
//			restaurant.setMenuCategories(Arrays.asList(menuCategory));
//			restaurant.setAcceptedPaymentMethods(acceptedPaymentMethods);
//			restaurant.setRestaurantDescription("The best restaurant. Ever!");
//			restaurant.setCategory(RestaurantCategory.HIGHEST);
//			restaurant.setCurrency(new Currency("Swiss Franc", "CHF"));
//			restaurant.setAddress("inn 329");
//			restaurant.setVersion(0);
//			restaurant.setStars(Rating.FIVE);
//			restaurant.setPayBeforeOrderIsPlacedIsSet(false);
//			restaurant.setRestaurantId(IdManager.getID(restaurant));
		}

		System.out.println("Returning the restaurant  " + restaurant);
		return restaurant;
	}

	@Override
	public boolean versionMatches(long version) throws TException {
		System.out.println("Version matches");
		return false;
	}

	@Override
	public ClientOrderReceipt placeOrder(OrderPlacedByClient order) throws TException {
//		Database.addOrder(order);
		
		System.out.println("Received: " + order);
		String phoneToken = order.phoneId.replaceAll(" ", "");
		phoneToken = phoneToken.substring(1, phoneToken.length() - 1);
		long newOrderId = IdManager.getID(order);
		order.orderId = newOrderId;
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
