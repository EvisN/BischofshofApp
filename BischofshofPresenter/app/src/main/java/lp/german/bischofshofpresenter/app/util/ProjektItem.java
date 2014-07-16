package lp.german.bischofshofpresenter.app.util;

/**
 * Created by paullichtenberger on 11.07.14.
 */
public class ProjektItem  {

    private String title;
    private String count = "0";
    private String absolutePath;
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private boolean isChecked = false;

    public ProjektItem(){}

    public ProjektItem(String title, String absolutePath){
        this.absolutePath = absolutePath;
        this.title = title;
    }

    public void setChecked(){
        isChecked = isChecked ? false : true;
    }

    public boolean isItemChecked(){
        return isChecked;
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

    public void setCount(String count){
        this.count = count;
    }

    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }

}
