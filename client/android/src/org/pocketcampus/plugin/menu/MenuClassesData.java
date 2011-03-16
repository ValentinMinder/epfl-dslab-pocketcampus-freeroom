package org.pocketcampus.plugin.menu;

public class MenuClassesData {
    private static final MenuClassesData INSTANCE = new MenuClassesData();
    
    private static String ownerPackage_;
    private static String[] tabClasses_;
    // Private constructor prevents instantiation from other classes
    private MenuClassesData() {
    	ownerPackage_ = "org.pocketcampus";
    	tabClasses_ = null;/*{
    			DailyMenus.class.getName(), 
    			WeeklyMenus.class.getName() 
    		};*/
    }
 
    public static MenuClassesData getInstance() {
        return INSTANCE;
    }

    public static String getOwnerPackage(){
    	return ownerPackage_;
    }
    
    public static String[] getTabClasses(){
    	return tabClasses_;
    }

}
