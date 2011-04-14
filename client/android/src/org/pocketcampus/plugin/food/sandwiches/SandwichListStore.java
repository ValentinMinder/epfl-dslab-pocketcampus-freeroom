///**
// * Sandwich List Store
// * 
// * @author Oriane
// * 
// */
//
//package org.pocketcampus.plugin.food.sandwiches;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Vector;
//
//import org.pocketcampus.shared.plugin.food.Sandwich;
//
//import android.util.Log;
//
//public class SandwichListStore {
//
//	// List of sandwiches sorted by Restaurant
//	private Vector<Vector<Sandwich>> sandwichList_;
//	
//	// Allow to modify the sandwich list from the server 
//	private boolean modifiySandwichList_ = false;
//	// tests
//	private boolean test = false;
//
//	/* Constructor */
//	public SandwichListStore(){
//		if (test) {
//			sandwichList_ = new Vector<Vector<Sandwich>>();
//			createListSandwich();
//		} else if (modifiySandwichList_) {
//			sandwichList_ = new Vector<Vector<Sandwich>>();
//			createListSandwich();
//		} else {
//			Log.d("SANDWICH_LIST_STORE", "test=false");
//			sandwichList_ = sortListSandwichByRestaurant();
//		}
//		valid();
//	}
//
//	private void valid() {
//		if (sandwichList_ == null)
//			throw new IllegalArgumentException("sandwichList cannot be null");
//	}
//
//	/* return the list of the restaurant */
//	public Vector<Vector<Sandwich>> getStoreList() {
//		return sandwichList_;
//	}
//
//	/**
//	 * Sort sandwich list by restaurant
//	 */
//	private Vector<Vector<Sandwich>> sortListSandwichByRestaurant(){
//
//		/* my list of sandwich sort by Restaurant */
//		Vector<Vector<Sandwich>> list = new Vector<Vector<Sandwich>>();
//		/* List of Restaurant names */
//		Vector<String> storeList = new Vector<String>();
//
//		/* FAKE CONTENT ON */
//
//		Collection<Sandwich> sandwichListServer = new ArrayList<Sandwich>();
//		Sandwich s1 = new Sandwich("Satellite", "Poulet Curry", true,
//				new Date());
//		Sandwich s2 = new Sandwich("Satellite", "Jambon Cru", true, new Date());
//		Sandwich s3 = new Sandwich("Satellite", "Tomate Mozzarella", true,
//				new Date());
//
//		Sandwich s4 = new Sandwich("Le Négoce", "Thon", true, new Date());
//		Sandwich s5 = new Sandwich("Le Négoce", "Poulet Curry", true,
//				new Date());
//
//		Sandwich s6 = new Sandwich("Cafeteria INM", "Poulet Curry", true,
//				new Date());
//
//		sandwichListServer.add(s1);
//		sandwichListServer.add(s2);
//		sandwichListServer.add(s3);
//		sandwichListServer.add(s4);
//		sandwichListServer.add(s5);
//		sandwichListServer.add(s6);
//
//		/* FAKE CONTENT OFF */
//
//		for (Sandwich i : sandwichListServer) {
//			if (storeList.contains(i.getRestaurant())) {
//				int position = findPositionRestaurant(i.getRestaurant(), list);
//				if (position < 0)
//					throw new IllegalArgumentException(
//							"no element in the list !");
//				else
//					list.get(position).add(i);
//			} else {
//				Vector<Sandwich> newList = new Vector<Sandwich>();
//				newList.add(i);
//				list.add(newList);
//				storeList.add(i.getRestaurant());
//			}
//		}
//		return list;
//	}
//
//	/*
//	 * help the method sortListSandwichByRestaurant() to find the position of
//	 * the restaurant
//	 */
//	private int findPositionRestaurant(String name,
//			Vector<Vector<Sandwich>> list) {
//		int sizeList = list.size();
//		for (int i = 0; i < sizeList; i++) {
//			if (name.equals(list.get(i).get(0).getRestaurant()))
//				return i;
//		}
//		return -1;
//	}
//
//	/*
//	 * create the list of sandwich in the EPFL (to do -> change
//	 * modifiySandwichList_ to TRUE)
//	 */
//	private void createListSandwich() {
//
//		/* Cafeteria INM */
//		Vector<Sandwich> CafeteriaINM = new Vector<Sandwich>();
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Poulet au Curry", true,
//				new Date()));
//		CafeteriaINM
//				.add(new Sandwich("Cafeteria INM", "Thon", true, new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Jambon", true,
//				new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Fromage", true,
//				new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Tomate Mozzarella",
//				true, new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Jambon Cru", true,
//				new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Salami", true,
//				new Date()));
//		CafeteriaINM.add(new Sandwich("Cafeteria INM", "Autres", true,
//				new Date()));
//
//		/* Cafeteria BM */
//		Vector<Sandwich> CafeteriaBM = defaultSandwichList("Cafeteria BM");
//
//		/* Cafeteria BC */
//		Vector<Sandwich> CafeteriaBC = defaultSandwichList("Cafeteria BM");
//
//		/* Cafeteria SV */
//		Vector<Sandwich> CafeteriaSV = defaultSandwichList("Cafeteria SV");
//
//		/* Cafeteria MX */
//		Vector<Sandwich> CafeteriaMX = defaultSandwichList("Cafeteria MX");
//
//		/* Cafeteria PH */
//		Vector<Sandwich> CafeteriaPH = defaultSandwichList("Cafeteria PH");
//
//		/* Cafeteria ELA */
//		Vector<Sandwich> CafeteriaELA = defaultSandwichList("Cafeteria ELA");
//
//		/* Le Giacomettia (Cafeteria SG) */
//		Vector<Sandwich> CafeteriaSG = new Vector<Sandwich>();
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Jambon", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Salami", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Jambon de dinde", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Gruyière", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Viande Séchée", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Jambon cru", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Roast-Beef", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Poulet Jijommaise",
//				true, new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Crevettes", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Saumon fumé", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Poulet au Curry", true,
//				new Date()));
//		CafeteriaSG.add(new Sandwich("Le Giacomettia", "Autres", true,
//				new Date()));
//
//		/* L'Esplanade */
//		Vector<Sandwich> esplanade = new Vector<Sandwich>();
//		esplanade.add(new Sandwich("L'Esplanade", "Thon", true, new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Poulet au Curry", true,
//				new Date()));
//		esplanade
//				.add(new Sandwich("L'Esplanade", "Aubergine", true, new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Roast-Beef", true,
//				new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Jambon Cru", true,
//				new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Vuabde Séchée", true,
//				new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Saumon Fumé", true,
//				new Date()));
//		esplanade.add(new Sandwich("L'Esplanade", "Autres", true, new Date()));
//
//		/* L'Arcadie */
//		Vector<Sandwich> arcadie = defaultSandwichList("L'Arcadie");
//
//		/* Atlantide */
//		Vector<Sandwich> atlantide = new Vector<Sandwich>();
//		atlantide.add(new Sandwich("L'Atlantide", "Sandwich long", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Sandwich au pavot", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Sandwich intégral", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Sandwich provençal", true,
//				new Date()));
//		atlantide
//				.add(new Sandwich("L'Atlantide", "Parisette", true, new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Jambon", true, new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Salami", true, new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Dinde", true, new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Thon", true, new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Mozzarella", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Saumon Fumé", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Viande Séchée", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Jambon Cru", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Roast-Beef", true,
//				new Date()));
//		atlantide.add(new Sandwich("L'Atlantide", "Autres", true, new Date()));
//
//		/* Satellite */
//		Vector<Sandwich> satellite = new Vector<Sandwich>();
//		satellite.add(new Sandwich("Satellite", "Thon", true, new Date()));
//		satellite.add(new Sandwich("Satellite", "Jambon fromage", true,
//				new Date()));
//		satellite
//				.add(new Sandwich("Satellite", "Roast-Beef", true, new Date()));
//		satellite.add(new Sandwich("Satellite", "Poulet au Curry", true,
//				new Date()));
//		satellite
//				.add(new Sandwich("Satellite", "Jambon Cru", true, new Date()));
//		satellite.add(new Sandwich("Satellite", "Tomate mozza", true,
//				new Date()));
//		satellite.add(new Sandwich("Satellite", "Salami", true, new Date()));
//		satellite.add(new Sandwich("Satellite", "Parmesan", true, new Date()));
//		satellite.add(new Sandwich("Satellite", "Aubergine grillé", true,
//				new Date()));
//		satellite.add(new Sandwich("Satellite", "Viande séchée", true,
//				new Date()));
//		satellite.add(new Sandwich("Satellite", "Autres", true, new Date()));
//
//		/* Negoce */
//		Vector<Sandwich> Negoce = new Vector<Sandwich>();
//		Negoce.add(new Sandwich("Negoce", "Dinde", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Thon", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Gratine Jambon", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Mozza Olives", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Poulet au Curry", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Jambon fromage", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Jambon", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Salami", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "RoseBeef", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Mozzarella", true, new Date()));
//		Negoce.add(new Sandwich("Negoce", "Autres", true, new Date()));
//
//		sandwichList_.add(CafeteriaINM);
//		sandwichList_.add(CafeteriaBM);
//		sandwichList_.add(CafeteriaBC);
//		sandwichList_.add(CafeteriaSV);
//		sandwichList_.add(CafeteriaMX);
//		sandwichList_.add(CafeteriaPH);
//		sandwichList_.add(CafeteriaELA);
//		sandwichList_.add(CafeteriaSG);
//		sandwichList_.add(esplanade);
//		sandwichList_.add(arcadie);
//		sandwichList_.add(atlantide);
//		sandwichList_.add(satellite);
//		sandwichList_.add(Negoce);
//	}
//
//	/* we don't have always the list, so it's the default list sandwich */
//	private Vector<Sandwich> defaultSandwichList(String name) {
//
//		Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();
//
//		defaultSandwichList.add(new Sandwich(name, "Thon", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Jambon", true, new Date()));
//		defaultSandwichList
//				.add(new Sandwich(name, "Fromage", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Tomate Mozzarella", true,
//				new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Jambon Cru", true,
//				new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Salami", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Autres", true, new Date()));
//
//		return defaultSandwichList;
//	}
//}
