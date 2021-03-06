package lp.german.slidingmenu.model;

/**
 * Created by paullichtenberger on 24.06.14.
 */
public class NavDrawerItem {

    private String title;
    private String count = "0";
    private String absolutePath;
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private int design_int = 0;

    public NavDrawerItem(){}

    public NavDrawerItem(String title, String absolutePath){
        this.absolutePath = absolutePath;
        this.title = title;
    }

    public NavDrawerItem(String title,  String absolutePath, boolean isCounterVisible, String count){
        this.title = title;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
        this.absolutePath = absolutePath;
    }

    public String getTitle(){
        return this.title;
    }

    public String getCount(){
        return this.count;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

    public void setTitle(String title){
        this.title = title;
    }



}
