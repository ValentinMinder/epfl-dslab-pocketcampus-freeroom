package org.pocketcampus.plugin.food;

import org.pocketcampus.plugin.news.NewsDisplay;



public class FoodClassesData {
    private static final FoodClassesData INSTANCE = new FoodClassesData();
    
    private static String ownerPackage_;
    private static String[] tabActivities_;

    // Private constructor prevents instantiation from other classes
    private FoodClassesData() {
    	ownerPackage_ = "org.pocketcampus";
    	tabActivities_ = new String[1];
    	tabActivities_[0] = NewsDisplay.class.getName();
    	//tabActivities_ = null;
    		/*{
    			DailyMenus.class.getName(), 
    			WeeklyMenus.class.getName() 
    		};*/
    }
 
    public static FoodClassesData getInstance() {
        return INSTANCE;
    }

    public static String getOwnerPackage(){
    	return ownerPackage_;
    }
    
    public static String[] getTabActivities(){
    	return tabActivities_;
    }
}
