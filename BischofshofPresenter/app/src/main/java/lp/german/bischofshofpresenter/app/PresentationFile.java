package lp.german.bischofshofpresenter.app;

import java.lang.reflect.Type;

/**
 * Created by paullichtenberger on 27.05.14.
 */
public class PresentationFile {

    String sName, sType;

    public PresentationFile(String name, String type){
        sName = name;
        sType = type;
    }

    public String Name() {
        return sName;
    }

    public String Type() {
        return sType;
    }
}
