package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

public class PraesentationsActivity extends Activity{

    private SimpleGestureFilter detector;
    private File[] filesList;
    private ArrayList<String> filePaths;

    //Setzt index des aktuellen files auf das erste im Array
    private int mCurrentFile = 0;

    public final static int NEXT_FILE_RESULT = 101;
    public final static int NEXT_FILE = 102;
    public final static int PREVIOUS_FILE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setze Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_praesentations);

        //Hole die Dateien aus dem Präsentationsordner in ein Array aus Files und die absoluten Pfade
        File dir = new File(FileUtilities.PFAD_PRAESENTATION);
        filesList = dir.listFiles();
        filePaths = new ArrayList<String>();

        startFile(getIntent().getStringExtra("file"));

        if(filesList!=null){
            for (int i = 0; i< filesList.length; i++){
                filePaths.add(filesList[i].getAbsolutePath());
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){

        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }



    //Checkt ob ein weiter oder zurück Button
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case NEXT_FILE_RESULT:
                switch (resultCode){
                    case RESULT_OK:
                        startFile(data.getStringExtra("file"));
                        break;
                    default:
                        finish();
                        break;
                }
                break;
            default:
                break;
        }
    }

    //Started die Anzeigeactivity und gibt parameter mit ob es vorher oder nachher auch noch ein file gibt
    private void startFile(String path){

            Intent i = new Intent(getApplicationContext(),SimpleFileViewActivity.class);
            i.putExtra("filePaths",filePaths);
            i.putExtra("path",path);
            startActivityForResult(i,NEXT_FILE_RESULT);
    }
}
