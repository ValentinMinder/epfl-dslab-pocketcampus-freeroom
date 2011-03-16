package org.pocketcampus.plugin.food;

public class FoodClassesData {
    private static final FoodClassesData INSTANCE = new FoodClassesData();
    
    private static String ownerPackage_;
    private static String[] tabClasses_;
    // Private constructor prevents instantiation from other classes
    private FoodClassesData() {
    	ownerPackage_ = "org.pocketcampus";
    	tabClasses_ = null;/*{
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
    
    public static String[] getTabClasses(){
    	return tabClasses_;
    }

}
