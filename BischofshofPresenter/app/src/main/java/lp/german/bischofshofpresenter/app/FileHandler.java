package lp.german.bischofshofpresenter.app;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by paullichtenberger on 08.05.14.
 */
public class FileHandler {

    private Context context;

    public FileHandler(Context context){
        this.context = context;
    }

    public ArrayList<File> getPräsentationen(){
        File verzeichnis = new File("/praesentationen");

        if(verzeichnis.exists()&&verzeichnis.isDirectory()){

        }else{
            Toast keinVerzeichnisToast = Toast.makeText(context,"Das Verzeichnis mit den Präsentationen wurde nicht gefunden!",Toast.LENGTH_SHORT);
            keinVerzeichnisToast.show();
        }

        return null;
    }
}
