package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.util.ArrayList;

import lp.german.bischofshofpresenter.app.SimpleGestureFilter.SimpleGestureListener;

public class PraesentationsActivity extends Activity implements SimpleGestureListener{


    private FileUtilities fileUtilities = new FileUtilities();
    private SimpleGestureFilter detector;
    private File[] filesList;
    private ArrayList<String> filePaths;
    private TextView platzHalter;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);

        //Setze Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_praesentations);

        //Holt den Pfad des Externen Speichers und von dort den Ordner Bischofshof, Hier werden wir dann alle Dateien ablegen lassen
        String path = Environment.getExternalStorageDirectory() + "/Bischofshof/";

        //Speichere alle Files im Hauptordner in ein Array aus Files
        File dir = new File(path);
        filesList = dir.listFiles();
        filePaths = new ArrayList<String>();


        if(filesList!=null){
            for (int i = 0; i< filesList.length; i++){
                filePaths.add(filesList[i].getAbsolutePath());
            }
            setupScrollViewContent(filesList);
        }

    }

    private void setupScrollViewContent(final File[] files){


        for(int i = 0; i<files.length; i++)
        {
            final File currentFile = files[i];
            String fileName = currentFile.getName();
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            textView.setText(fileName.substring(0, 10)+"...");

            ImageView imageView = (ImageView)v.findViewById(R.id.file_template_img);


            if(fileUtilities.getFileExtension(fileName).equals("pdf")){
                imageView.setImageResource(R.drawable.pdf2);
            }else {
                imageView.setImageResource(R.drawable.video);
            }


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile(currentFile);
                }
            });

            View relativeLayout = findViewById(R.id.scroll_view_relative_layout);
            ((ViewGroup)relativeLayout).addView(v);
        }

    }

    //Diese Methode wird bei Click auf ein File im Menu aufgerufen und soll später dann dieses File in der Präsentation starten
    private void openFile(File f){

        pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.fromFile(f)
                .defaultPage(1)
                .load();

        Toast toast = Toast.makeText(getApplicationContext(),"Öffne jetzt File "+f.getAbsolutePath(),Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){

        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :
                str = "Swipe Up";
                Intent i = new Intent(this,SlideUpMenu.class);
                i.putStringArrayListExtra("filePaths", filePaths);
                startActivityForResult(i,1);
                break;
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Toast toast = Toast.makeText(this,"Result: "+data.getStringExtra("path"),Toast.LENGTH_SHORT);
                toast.show();
                platzHalter = (TextView)findViewById(R.id.presentation);
                platzHalter.setText(data.getStringExtra("path"));
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
