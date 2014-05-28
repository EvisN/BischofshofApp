package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;

public class PraesentationsActivity extends Activity {

    private PresentationFile[] files;
    private FileUtilities fileUtilities = new FileUtilities();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setze Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_praesentations);

        //Holt den Pfad des Externen Speichers und von dort den Ordner Bischofshof, Hier werden wir dann alle Dateien ablegen lassen
        String path = Environment.getExternalStorageDirectory() + "/Bischofshof/";

        //Speichere alle Files im Hauptordner in ein Array aus Files
        File dir = new File(path);
        File[] filesList = dir.listFiles();

        //Wenn die Fileliste nicht leer ist, kann das Menü unten befüllt werden
        if(filesList!=null){
            setupScrollViewContent(filesList);
        }
    }

    private void setupScrollViewContent(File[] files){


        for(int i = 0; i<files.length; i++)
        {
            final int nCurrentIndex = i;
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            textView.setText(files[i].getName().substring(0,10)+"...");

            ImageView imageView = (ImageView)v.findViewById(R.id.file_template_img);


            if(fileUtilities.getFileExtension(files[i].getName()).equals("pdf")){
                imageView.setImageResource(R.drawable.pdf2);
            }else {
                imageView.setImageResource(R.drawable.video);
            }

            View fileList = findViewById(R.id.file_scroll_view_linear_layout);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile(nCurrentIndex);
                }
            });
            ((ViewGroup)fileList).addView(v);
        }

    }

    //Diese Methode wird bei Click auf ein File im Menu aufgerufen und soll später dann dieses File in der Präsentation starten
    private void openFile(int i){
        Toast toast = Toast.makeText(getApplicationContext(),"Öffne jetzt File an Index Nr "+String.valueOf(i),Toast.LENGTH_SHORT);
        toast.show();
    }

}
